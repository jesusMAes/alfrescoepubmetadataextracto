package com.jesusmarmol.actions;

import org.alfresco.service.namespace.QName;
import org.apache.tika.metadata.Metadata;

import java.io.Serializable;
import java.util.*;

public class Utils {

  public static String normalizeName(String name){
    return name.replace(",", "").replace("_", "a").replace("-", "");
  }

  public static Map<QName,Serializable> parseDCMetadataToModelMetadata(Metadata metadata){
    Map<QName, Serializable> properties = new HashMap<>();
    for (String name : metadata.names()) {
      String metadataValue = metadata.get(name);
      if(name.contains("dc:")){
         String curatedName = name.replace("dc:", "");
         if(curatedName.equals("creator") || curatedName.equals("subject") || curatedName.equals("contributor") || curatedName.equals("language") || curatedName.equals("relation")){
          //multivalue properties ADD TO ARRAY
           ArrayList lastValue = new ArrayList(Arrays.asList(properties.get(dublinCoreTagToQNameEquivalence.get(curatedName))));
           lastValue.add(metadataValue);
           properties.put(dublinCoreTagToQNameEquivalence.get(curatedName), lastValue);
         }else{
           properties.put(dublinCoreTagToQNameEquivalence.get(curatedName), metadataValue);
         }
        }
    }
    return properties;
  }

  public static Map<String, QName> dublinCoreTagToQNameEquivalence;
  static {
    Map<String, QName> initializedMap = new HashMap<>();
    initializedMap.put("title" , JMEmodelConstant.EDC_PROP_TITLE);
    initializedMap.put("creator", JMEmodelConstant.EDC_PROP_CREATOR);
    initializedMap.put("subject", JMEmodelConstant.EDC_PROP_SUBJECT);
    initializedMap.put("description", JMEmodelConstant.EDC_PROP_DESCRIPTION);
    initializedMap.put("publisher", JMEmodelConstant.EDC_PROP_PUBLISHER);
    initializedMap.put("contributor", JMEmodelConstant.EDC_PROP_CONTRIBUTOR);
    initializedMap.put("date", JMEmodelConstant.EDC_PROP_DATE);
    initializedMap.put("type", JMEmodelConstant.EDC_PROP_TYPE);
    initializedMap.put("format", JMEmodelConstant.EDC_PROP_FORMAT);
    initializedMap.put("identifier", JMEmodelConstant.EDC_PROP_IDENTIFIER);
    initializedMap.put("source", JMEmodelConstant.EDC_PROP_SOURCE);
    initializedMap.put("language", JMEmodelConstant.EDC_PROP_LANGUAGE);
    initializedMap.put("relation", JMEmodelConstant.EDC_PROP_RELATION);
    initializedMap.put("coverage", JMEmodelConstant.EDC_PROP_COVERAGE);
    initializedMap.put("rights", JMEmodelConstant.EDC_PROP_RIGHTS);
    dublinCoreTagToQNameEquivalence = initializedMap;
  }



}
