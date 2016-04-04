# TatamiJHipster

Create a new cluster: ccm create 'nodeName' -v 2.1.6 -n 'nodeCount'

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Grunt][] as our build system. Install the grunt command-line tool globally with:

    npm install -g grunt-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    mvn
    grunt

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

# Building for production

To optimize the TatamiJHipster client for production, run:

    mvn -Pprod clean package

This will concatenate and minify CSS and JavaScript files. It will also modify `index.html` so it references
these new files.

To ensure everything worked, run:

    java -jar target/*.war --spring.profiles.active=prod

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

# Testing

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript` and can be run with:

    grunt test



# Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `TatamiJHipster`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/TatamiJHipster.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Grunt]: http://gruntjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/


# Docker for Cassandra

Docker and Docker-compose are the easiest way to start Cassandra locally for the development needs.

Dockerized Cassandra cluster for Tatami is an improvement of[JHipster original](http://jhipster.github.io/docker-compose/#cassandra) docker-compose to avoid the manual steps.

From the root directory, simply run:

    docker-compose -f src/main/docker/cassandra.yml build
    docker-compose -f src/main/docker/cassandra.yml up -d

Docker-compose will start 3 services:
- **tatami-cassandra**, a container with the Cassandra node contact point
- **tatami-cassandra-node-1**, a container with a second Cassandra node joining the cluster
- **tatami-cassandra-migration** a container to apply the keypsace and schema creation and data migration


To stop the cluster, run:

    docker-compose -f src/main/docker/cassandra.yml build

You can dynamically add nodes to the Cassandra cluster, but you should increment the number one by one as the cluster
will fail to add more than one node at the same time.
To dynamically change the number of nodes joining the Cassandra cluster, run:

    docker-compose -f src/main/docker/cassandra.yml scale tatami-cassandra-node=NUMBER_OF_NODES

The migration service is responsible to apply all the migration scripts from src/main/resources/config/cql in the following order:
1. create-keyspace.cql
2. create-tables.cql
3. all \*_added_entity_\*.cql in alphabetical order
4. all V\*.cql in alphabetical order

Some explanations:
- `create-tables.cql` is the initial bootstrap script. You shouldn't add many database changes after being in production here
- `*_added_entity_*.cql` are the scripts automatically generated by the `yo jhipster:entity` generator
- `V*.cql` are all scripts altering the schemas or data. This is where you will add all the database changes

The idea is to follow incremental database changes principle, where all migration scripts must be applied in a fixed order.
This is why the scripts tend to follow the conventions from [FlywayDB](https://flywaydb.org/documentation/migration/sql.html).

Although there is no tool like FlywayDB or Liquibase for Cassandra, this convention will allow to easily add such tool in the future.


## Problem with current JHipster implementation
- Scripts are not automatically applied in the Cassandra cluster for dev.
- No failure handling.
  When run manually with `docker exec <container id> init or entities`, if any of the query has a problem then the following queries are still applied.
  This is because cqlsh does not stop on query failure. But we made it more dangerous by first concat all the CQL files.
- When adding a new entity model generated with the JHipster entity generator, the CQL script is not added to AbstractCassandraTest


## Access Cassandra in Mac OS/x
Install [Docker toolbox for Mac](https://www.docker.com/products/docker-toolbox)

On Mac OSx, Docker containers are not hosted directly but on a VirtualBox VM.
Those, you can not access them in localhost but have to hit the VirtualBox IP first.

__new__: Docker has released an alternative of Docker running on VirtualBox, still on private beta, but you may want to test it to avoid the extra configuration. See [https://blog.docker.com/2016/03/docker-for-mac-windows-beta/]()

### Configuration
Find docker host ip :

    DOCKER_IP=`docker-machine ip default`

add this environment variable:

    SPRING_DATA_CASSANDRA_CONTACTPOINTS=$DOCKER_IP

### Cassandra nodes
Because Cassandra nodes are also hosted in the Virtual machine, the Cassandra Java driver will receive an error when trying to contact them after receiving their address from the contact point.
To workaround this, you can add a routing rule to your mac routing table.

Assuming the containers running the Cassandra nodes have IP address 172.18.0.x

    sudo route -n add 172.18.0.0/16 `docker-machine ip default`

source: http://krasserm.github.io/2015/07/13/chaos-testing-with-docker-and-cassandra/#port-mapping

### CQLSH or other tools:
Since you have configured the DOCKER_IP environment variable, you access the dockerized Cassandra cluster in VirtualBox by running:

    cqlsh $DOCKER_IP
