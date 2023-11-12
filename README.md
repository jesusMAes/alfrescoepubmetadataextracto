# Alfresco Epub metadata extractor
Simple action that add Epub files to mimetypes and extract its dublin core metadata

## Usage
Fork the project and build it using `./run.bat build_start` or `./run.sh build_start` or copy the Jars from [content](alfrescoepubmetadataextracto-platform/target/) and from [share](alfrescoepubmetadataextracto-share/target/) 
Copy the jars in your dockerized alfresco in `alfresco/modules/jars` and `share/modules/jars`

Create your library folder and add a rule to it: when a epub file enters, execute extract epub metadata action. Mark the autoclasify check if you want it to create and author/book folder estructure.
![imagen](https://github.com/jesusMAes/alfrescoepubmetadataextracto/assets/95760152/a71d0d46-92a8-4ccb-8093-71cde417ff39)

Autoclasify also extract the cover image of the book
![imagen](https://github.com/jesusMAes/alfrescoepubmetadataextracto/assets/95760152/1c3e7657-08b1-4d71-9b3b-49c506161cd5)

## Metadata
Alfresco base dublin core aspect lack of some useful dublin core metadata so I write my own extension. Extracted metadata would be located in `jme:extendedDublinCore` aspect.
Based on [dublin core standard](https://www.dublincore.org/specifications/dublin-core/dcmes-qualifiers/ ) 
Fields are:
| Metadata | Description | Dublin core equivalent |
| ----------- | ----------- | ----------- |
| jme:DCtitle | Title of the document | Title |
| jme:DCcreator | Creator(s) of the document - Multivalue | Creator|
| jme:DCsubject | Subject(s) of the document - Multivalue | Subject|
| jme:DCdescription | Description of the document | Description|
| jme:DCpublisher | Publisher of the document | Publisher|
| jme:DCcontributor | Contributor(s) of the document - Multivalue | Contributor|
| jme:DCdate | Date of the document | Date |
| jme:DCtype | Type of the document | Type |
| jme:DCformat | Format of the document | Format |
| jme:DCidentifier | ID of the document | Identifier |
| jme:DCsource | Source of the document | Source |
| jme:DClanguage | Language(s) of the document - Multivalue | language |
| jme:DCrelation | Relation(s) of the document - Multivalue | Relation |
| jme:DCcoverage | Coverage of the document | Coverage |
| jme:DCrights | Rights of the document | Rights |





# Alfresco AIO Project - SDK 4.6

This is an All-In-One (AIO) project for Alfresco SDK 4.6.

Run with `./run.sh build_start` or `./run.bat build_start` and verify that it

 * Runs Alfresco Content Service (ACS)
 * Runs Alfresco Share
 * Runs Alfresco Search Service (ASS)
 * Runs PostgreSQL database
 * Deploys the JAR assembled modules
 
All the services of the project are now run as docker containers. The run script offers the next tasks:

 * `build_start`. Build the whole project, recreate the ACS and Share docker images, start the dockerised environment composed by ACS, Share, ASS and 
 PostgreSQL and tail the logs of all the containers.
 * `build_start_it_supported`. Build the whole project including dependencies required for IT execution, recreate the ACS and Share docker images, start the 
 dockerised environment composed by ACS, Share, ASS and PostgreSQL and tail the logs of all the containers.
 * `start`. Start the dockerised environment without building the project and tail the logs of all the containers.
 * `stop`. Stop the dockerised environment.
 * `purge`. Stop the dockerised container and delete all the persistent data (docker volumes).
 * `tail`. Tail the logs of all the containers.
 * `reload_share`. Build the Share module, recreate the Share docker image and restart the Share container.
 * `reload_acs`. Build the ACS module, recreate the ACS docker image and restart the ACS container.
 * `build_test`. Build the whole project, recreate the ACS and Share docker images, start the dockerised environment, execute the integration tests from the
 `integration-tests` module and stop the environment.
 * `test`. Execute the integration tests (the environment must be already started).

# Few things to notice

 * No parent pom
 * No WAR projects, the jars are included in the custom docker images
 * No runner project - the Alfresco environment is now managed through [Docker](https://www.docker.com/)
 * Standard JAR packaging and layout
 * Works seamlessly with Eclipse and IntelliJ IDEA
 * JRebel for hot reloading, JRebel maven plugin for generating rebel.xml [JRebel integration documentation]
 * AMP as an assembly
 * Persistent test data through restart thanks to the use of Docker volumes for ACS, ASS and database data
 * Integration tests module to execute tests against the final environment (dockerised)
 * Resources loaded from META-INF
 * Web Fragment (this includes a sample servlet configured via web fragment)

# TODO

  * Abstract assembly into a dependency so we don't have to ship the assembly in the archetype
  * Functional/remote unit tests
