package com.jesusmarmol.actions;


import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.domain.Resource;
import io.documentnode.epub4j.epub.EpubReader;
import org.activiti.bpmn.model.AssociationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.rest.api.model.Assoc;
import org.alfresco.rest.api.model.Association;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentExtractorFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.epub.EpubParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


import io.documentnode.epub4j.epub.EpubWriter;

/**
 * Action that extracts epub dublin core metadata and its cover, then creates a folder for the author and a subfolder for the title of the epub, then places the epub
 * and its cover into the subfolder and dublin core metadata into the epub node as properties
 * */
public class ExtractEpubMetadata  extends ActionExecuterAbstractBase {

  private ServiceRegistry serviceRegistry;
  public void setServiceRegistry(ServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
  }

  @Override
  protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

    NodeService nodeService = serviceRegistry.getNodeService();
    ContentService contentService = serviceRegistry.getContentService();

    String filename = Utils.normalizeName(nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME).toString());
    NodeRef parentFolder = nodeService.getPrimaryParent(actionedUponNodeRef).getParentRef();

    boolean autoClasify = (boolean) action.getParameterValue("AUTOCLASIFY");
    ContentReader contentReader = contentService.getReader(actionedUponNodeRef, ContentModel.PROP_CONTENT);

    //extract epub metadata
    Metadata metadata = new Metadata();
    BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);//disable max characters limit, can be slow on larger files
    ParseContext parseContext = new ParseContext();
    EpubParser tikaEpubParser = new EpubParser();
    try {
      tikaEpubParser.parse(contentReader.getContentInputStream(), bodyContentHandler, metadata, parseContext);//after the parse the metadata is stored in metadata object

      //parse metadata to dublin core properties
      Map<QName, Serializable> properties = Utils.parseDCMetadataToModelMetadata(metadata);
      nodeService.addAspect(actionedUponNodeRef, JMEmodelConstant.ASPECT_EXTENDED_DUBLIN_CORE, properties);

      // if autoclasify is on
      if(autoClasify){
        // localize author folder, if no exist create it, remove conflictive characters
        String authorName = Utils.normalizeName(metadata.get("dc:creator").replace(".",""));
        if(authorName.equals("")){
          authorName = "unrecognized";
        }
        NodeRef authorFolder = nodeService.getChildByName(parentFolder, ContentModel.ASSOC_CONTAINS, authorName);
        if(authorFolder == null){//create author folder
          authorFolder = serviceRegistry.getFileFolderService().create(parentFolder, authorName, ContentModel.TYPE_FOLDER).getNodeRef();
        }

        // create subfolder inside autor folder
        String bookFolderName = filename.replace(".epub", ""); //remove file extension
        NodeRef bookFolder = serviceRegistry.getFileFolderService().create(authorFolder, bookFolderName, ContentModel.TYPE_FOLDER).getNodeRef();

        // extract cover.jpg
        InputStream epubAsIs = serviceRegistry.getContentService().getReader(actionedUponNodeRef, ContentModel.PROP_CONTENT).getContentInputStream();
        EpubReader epubReader = new EpubReader();
        Book epub = epubReader.readEpub(epubAsIs);
        Resource coverImage = epub.getCoverImage();
        InputStream coverImageIs = coverImage.getInputStream();

        // move epub and cover to subfolder
        NodeRef coverNode = serviceRegistry.getFileFolderService().create(bookFolder, "cover"+coverImage.getMediaType().getDefaultExtension(), ContentModel.PROP_CONTENT).getNodeRef();
        ContentWriter coverWriter = serviceRegistry.getContentService().getWriter(coverNode, ContentModel.PROP_CONTENT, true);
        coverWriter.putContent(coverImageIs);
        serviceRegistry.getFileFolderService().move(actionedUponNodeRef, bookFolder, filename);
      }

    }catch (Exception e){
      System.err.println("There has been an exception parsing the epub:  " + e.getMessage());
      throw new RuntimeException("There has been an exception parsing the epub: " + filename);
    }


  }

  @Override
  protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    paramList.add(new ParameterDefinitionImpl("AUTOCLASIFY", DataTypeDefinition.BOOLEAN, false, getParamDisplayLabel("AUTOCLASIFY")));
  }
}
