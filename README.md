# Semaphore CI API Client
***

This library is intended to be used as a simple client for the [Semaphore CI](https://semaphoreci.com) REST API. The library supports Semaphore CI API version 1. It wraps the API calls covering most of the available API endpoints, enabling you to [securely](https://semaphoreci.com/docs/api_authentication.html):
 - list [projects](https://semaphoreci.com/docs/projects-api.html), 
 - manage [branches and builds](https://semaphoreci.com/docs/branches-and-builds-api.html), 
 - check [servers and deploys](https://semaphoreci.com/docs/servers-and-deploys-api.html) and 
 - manage [webhooks](https://semaphoreci.com/docs/webhooks-api.html).
 
All the responses from the API are in turn mapped into the corresponding Java objects (defined in `org.devfort.semaphoreci4j.model` package). 

## Getting Started

In order to use the library, you should first: 

- Clone the repository

```sh
$ git clone https://github.com/ksokolovic/semaphoreci4j.git
```

- Build the project

```sh
# Including tests 
$ gradle clean build

# Without tests
$ gradle clean build -x test
```

- Add the `semaphoreci4j` library to your project classpath.

## Usage

Since Semaphore CI requires all API requests to be authenticated, as the first step you should obtain your API authentication token. Head over to [this page](https://semaphoreci.com/users/edit), and copy the token which is shown at the bottom of the page. 

The `org.devfort.semaphoreci4j.SemaphoreCI` class is the entry point class for interacting with the Semaphore API. You create a reference to it using the previously obtained authentication token:

```java
SemaphoreCI semaphore = new SemaphoreCI("authentication-token");
```

At the top level you can list your projects, which are returned as a map of project names to projects:

```java
Map<String, Project> projects = semaphoreCI.getProjects();
```

Alternatively, you can request for a specific project by its name, using the `getProject("project-name");` method of the `SemaphoreCI` class. 

The `Project` instance will in turn allow you to access all the relevant information listed above. 

## Contribution

### Issues 

In case you run into any issues while using the library or have a suggestion for improving it, please [create an issue here](https://github.com/ksokolovic/semaphoreci4j/issues), with as detailed as possible description of what's going on; a working example, code snippet or log file are the best.

## License

Copyright (c) Kemal Sokolović <kemal DOT sokolovic AT gmail DOT com>, Miloš Panasiuk <milos DOT panasiuk AT gmail DOT com>

Distributed under the [MIT License](http://opensource.org/licenses/MIT)