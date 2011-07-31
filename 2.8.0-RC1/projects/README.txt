This readme file presents how to configure the development environment
for the Documentum connector.

REQUIRED FILES to compile and run the tests

In your home directory, there must be build configuration
properties file, named google-enterprise-connector-dctm.properties,
that is tailored to the your development and test environment.
A sample google-enterprise-connector-dctm.properties is provided.

To build and run the junit tests for this connector, you must copy a
dfc.jar from <Documentum root folder>/dfc to dctm_third_party/lib.

To build and run the junit tests for this connector, you must download
jcr-1.0.jar and put it in the directory third_party/lib.  This jar is
part of JSR-170.  To get this jar, go to the official JSR-170 site,
and follow the links from there:
http://jcp.org/aboutJava/communityprocess/review/jsr170/index.html


CONFIGURATION

Open Eclipse and create a java project from the existing source
"connector-dctm".

Add to the classpath of the project all the jars that are in
third_party/lib and dctm_third_party/lib.

Also, add to the classpath Class folders linked to the folders
google_enterprise_connector_dctm/config and <Documentum root folder>/config.


JUNIT TESTS

To run the dctm-core unit tests, build the ant target "run_tests"
in projects/build.xml.

To run the dctm-client unit tests, open the class
com.google.enterprise.connector.dctm.DmInitialize in the folder
dctm-client/source/javatests to add your environment configuration.

