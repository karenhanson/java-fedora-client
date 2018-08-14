# Java Fedora Client 
Java client for managing interactions with the PASS data in Fedora. Includes the data model represented as POJOs, annotated with Jackson for easy conversion to JSON.

## PASS POJOs
The model is kept up to date with the [pass-data-model](https://github.com/OA-PASS/pass-data-model) project, but may change as new model requirements are identified.

## PASS Client
The interfaces in `pass-client-api` can be used to access both Fedora and Elasticsearch

Note: this client does not currently perform any validation such as duplicate checking, or verifying required fields, it assumes these kinds of checks take place outside of the client. It also does not yet support batch transactions, or respond appropriately to various HTTP statuses that come back from Fedora. These will need to be added as needed once the client can be developed fully.

### CRUD functions
The CRUD calls for Fedora perform basic read, write, update and delete functions using the model objects in the `pass-model` module. The Java doc provide guidance on how to use the various functions. For example, to create a record, simply pass a populated model object into the client's createResource function and you will receive a URI that can be used to retrieve the object:
```
Grant grant = new Grant();
//populate Grant
...
PassClient client = PassClientFactory.getPassClient();
URI uri = client.createResource(grant);
```

Note that to update an object, it is important to first read it from the database, make your changes to the retrieved object, then pass the same object to the updateResource method. Information is stored within the object that is vital to perform a proper update. In a future iteration, this will throw an exception if the object changed between the read and update. Here is an example:
```
PassClient client = PassClientFactory.getPassClient();
Grant grant = (Grant) client.readResource(uri);
//modify the grant object.
grant.setAwardNumber("abc123");
client.updateResource(grant);

```

### findBy functions

The findBy functions allow you to look up records by a specific field, for example, searching for Grant by `localAwardId` might look like this:
```
String awardNumber = "AB123456";
PassClient client = PassClientFactory.getPassClient();
URI grantUri = client.findByAttribute(Grant.class, "awardNumber", awardNumber);
```
The Java docs provide more information about this functionality.

### Configuration
Configuration may be provided via system properties, or environment variables.  System properties are case-sensitive and separated by periods, as per Java conventions.
Environment variables should be uppercase and separated by underscores, as per OS conventions.  For example, the fedora user may be provided by 
a system property `-Dpass.fedora.user=myUser` or as an environment variable `PASS_FEDORA_USER=myUser`.  System properties override environment variables. 

* pass.fedora.baseurl (default=http://localhost:8080/fcrepo/rest)
* pass.fedora.user (default=fedoraAdmin)
* pass.fedora.password (default=moo)
* pass.elasticsearch.url (defaults = http://localhost:9200/pass)
* pass.elasticsearch.limit (defaults = 200) you can also override the default by using the findBy functions that accept a limit and offset value

## Integration tests with Fedora and Elasticsearch

The integration test module `pass-client-integration` uses Docker to spin up an instance of Fedora and Elasticsearch for testing the client against.

To run a the integration testing environment manually, from within `pass-client-integration`, do

    mvn docker:run -Pstandard

This will run Fedora at standard port (8080) and Elasticsearch at port 9200. This mode is very useful for testing/debugging/developing against the databases from within the IDE.   Repository content is stored in `target`, so if it is run after integration tests, the repository will still retain all data deposited during.
