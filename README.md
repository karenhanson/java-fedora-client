# Java Fedora Client 
Java client for managing interactions with the PASS data in Fedora. Includes the data model represented as POJOs, annotated with Jackson for easy conversion to JSON.

## PASS POJOs
The model is kept up to date with the [pass-data-model](https://github.com/OA-PASS/pass-data-model) project, but may change as new model requirements are identified.

## PASS Fedora Client
The Fedora client is is a *work in progress* and the interfaces in `pass-client-api` should be used for mocks only. 

Note: this client does not currently perform any validation such as duplicate checking, or verifying required fields, it assumes these kinds of checks take place outside of the client. It also does not yet support batch transactions, or respond appropriately to various HTTP statuses that come back from Fedora. These will need to be added as needed once the client can be developed fully.

The CRUD calls for Fedora are written but have not been tested, and likely do not work as they are. Calls to CRUD should look something like this:
```
Grant grant = new Grant();
//populate Grant
...
PassClient client = new FedoraPassClient();
URI uri = client.createResource(grant);
```

The index client has not been written yet. This will allow you to look up records by a specific field, for example, searching for Grant by `localAwardId` might look like this:
```
String localAwardId = "AB123456";
PassClient client = new FedoraPassClient();
URI grantUri = client.findByAttribute(Grant.class, "localAwardId", localAwardId);
```

## Integration with Fedora

The integration test module `pass-client-integration` uses Docker to spin up an instance of Fedora for testing the client against.

By default, it uses a statically-defined JSON-LD context, presently at `pass-client-integration/src/test/resources/docker/mnt/context.jsonld`.  Editing this (and re-starting the docker container, if applicable) will alow the client to be tested against unpublished contexts.

To run a Fedora instance manually, from within `pass-client-integration`, do

    mvn docker:run -Pstandard

This will run Fedora at standard ports (8080). This mode is very useful for testing/debugging/developing against Fedora within the IDE.   Repository content is stored in `target`, so if it is run after integration tests, the repository will still retain all data deposited during.