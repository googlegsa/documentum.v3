This readme file presents how to configure the development environment for the Documentum connector.

REQUIRED FILES
•	dfc.jar
•	dfcbase.jar
•	xml-apis.jar
•	dmcl.ini
•	dcml40.dll
•	dfc.properties
•	connector_spi.jar
•	connector.jar
•	connector_tests.jar
•	json.jar
•	jcr-1.0.jar
•	junit.jar
•	spring.jar
•	spring-context.jar

CONFIGURATION
Create a shared folder in projects\third_party
Copy dmcl.ini, dmcl40.dll and dfc.properties in this folder. 
Copy the jars in the lib folder.

Modify the "path" environment variable by adding the value:
<path to source>\third_party\shared

Create a new environment variable called DMCL_CONFIG with the path to the dmcl.ini file.

Open Eclipse and create a java project from the existing source "connector-dctm"
Add to the classpath of the project all the jars that are in third_party\lib
Also, add to the classpath a Class folder linked to the folder <path to source>\third_party\shared.

