# Configure the connectorInstance.xml file #

The advanced configuration is done through the modification of the
file connectorInstance.xml. This file is available inside connector-dctm.jar. You should extract this file and place it in the folder

> Tomcat/webapps/connector-manager/WEB-INF/connectors/EMC\_Documentum\_Content\_Server<em>version</em>/_connector-instance_/

where _version_ is the Documentum version, either "5.2.5\_5.3" or "6.0", and _connector-instance_ is the name of your connector instance.

By changing this file, you can add an additional where clause to
specify for example a specific folder in the Documentum repository, you can change the
included or excluded metadata of the document and you can also define what object types are to be indexed.


## Add an additional where clause ##

By modifying the value of the property named “where\_clause”,
you can change the where clause of the connector. This clause is a part
of a query begining by an `AND`. To know how to create a Documentum query,
please contact your Documentum administrator.
There is an example of a simple query to only explore a specific folder:

```

And Folder ('/path_to_add', descend) 

```

You just have to change /`path_to_add` by using the path you wish.


## Change the required metadata ##

To change the required data, there are two lists of metadata: the
included metadata and the excluded metadata.

If you wish to add a metadata in the included list or excluded list
for the connector, you have to add a line between the tags 

&lt;set&gt;

 and


&lt;/set&gt;

:

```

<value>name_of_the_metadata</value>

```

If the metadata is in any list or in included, the metadata is
included during the indexing process.
If the metadata is in both lists or in excluded, the metadata is
excluded during the indexing process.


Once the modification of the file is done, you have to restart the
connector service.



## Define what object types are to be indexed ##

You can specify the root object type by changing the value of the property "root\_object\_type"
(dm\_sysobject by default).

There is a list of included object types. If you want to add an object type,
you have to add a line between the tags 

&lt;set&gt;

 and 

&lt;/set&gt;

 of the property "included\_object\_type":

```

<value>dm_document</value>

```

By default, only "dm\_document" is set. Each included object types must be a subtype of the root object type.