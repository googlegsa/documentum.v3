This readme file presents how to configure the development environment for the Documentum connector.

REQUIRED FILES to compile and run the tests
•	dfc.jar (in <Documentum  root folder>/Shared)
•	dfcbase.jar (in <Documentum  root folder>/Shared)
•	xml-apis.jar (in <Documentum  root folder>/Shared)
•	log4j.jar (in <Documentum  root folder>/Shared)
•	commons-logging.jar (in third_party/lib folder of the project)
•	json.jar (in third_party/lib folder of the project)
•	junit.jar (in third_party/lib folder of the project)
•	jcr-1.0.jar (to copy in third_party/lib folder of the project)
•	spring.jar (to copy in third_party/lib folder of the project)
•	connector-spi.jar (to copy in third_party/lib folder of the project)
•	connector.jar (to copy in third_party/lib folder of the project
•	connector-tests.jar (to copy in third_party/lib folder of the project


To build and run the junit tests for the connector manager, you must download 
jcr-1.0.jar and put it in the directory third_party/lib.  This jar is part of JSR-170.  
To get this jar, go to the official JSR-170 site, and follow the links from there:
http://jcp.org/aboutJava/communityprocess/review/jsr170/index.html


CONFIGURATION
Open Eclipse and create a java project from the existing source "connector-dctm"
Add to the classpath of the project all the jars that are in third_party\lib and dctm_third_party\lib
Also, add to the classpath 3 Class folders linked to the folder 
•	google_enterprise_connector_dctm/config
•	<Documentum root folder>/config

JUNIT TESTS
To run the unit tests, open the classes 
•	com.google.enterprise.connector.dctm.DmInitialize in the folder dctm-client/source/javatests
•	com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize in the folder dctm-core/source/javatests
to add your environment configuration.

