Required files

•	dfc.jar
•	dfcbase.jar
•	xml-apis.jar
•	dmcl.ini
•	dcml40.dll
•	dfc.properties

Create a shared folder in projects\third_party

Copy dmcl.ini, dmcl40.dll and dfc.properties in this folder. 
Copy the jars in the lib folder.

Add to the environment variable “path” the value:
<path to source>\third_party\shared

Create a new environment variable called DMCL_CONFIG with the path to the dmcl.ini file.

