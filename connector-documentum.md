# Configure the connectorInstance.xml file #

The advanced configuration is done through the modification of the
file connectorInstance.xml located in the folder
/connector-manager/WEB-INF/connectors/EMC\_Documentum\_Content\_Server\_5.3/`instance_of_connector`/

By changing this file, you can add an additional where clause to
specify for example a specific folder in the Documentum repository, and you can change the
included or excluded metadata of the document.


## Add an additional where clause ##

By modifying the value of the property named �additional\_where\_clause�,
you can change the where clause of the connector. This clause is a part
of a query begining by an `AND`. To know how to create a Documentum query using DQL,
please contact your Documentum administrator.
There is an example of a simple query to only explore a specific folder:

```

And Folder ('/`path_to_add`', descend) 

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

<value>`name_of_the_metadata`</value>

```

If the metadata is in any list or in included, the metadata is
included during the indexing process.
If the metadata is in both lists or in excluded, the metadata is
excluded during the indexing process.


Once the modification of the file is done, you have to restart the
connector service.