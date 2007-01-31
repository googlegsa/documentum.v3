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

CONFIGURATION
Create a shared folder in projects\third_party
Copy dmcl.ini, dmcl40.dll and dfc.properties in this folder. 
Copy the jars in the lib folder.

Modify the "path" environment variable by adding the value:
<path to source>\third_party\shared

Create a new environment variable called DMCL_CONFIG with the path to the dmcl.ini file.

