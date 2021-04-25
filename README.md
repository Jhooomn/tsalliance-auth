# TSAlliance Authentication Service
Simple service for authenticating users on TSAlliance sites. 
This service is the main entry point for account management 
and service discovery.

## Table of Contents
1. Prerequisites
2. Basic setup

### 1. Prerequisites
* Java 8
* When deploying as .war file, you need Tomcat 9+
* MySQL as database is recommended (because of spring's nature you could use any SQL database, but at your own risk)

### 2. Basic Setup
Because this service is built using Spring Boot, a configuration file 
(in the case of spring an ``application.properties`` files) can be created 
in the root directory of the app. You can then configure the application 
with the help of the spring documentation. For the most important configuration
you can follow the steps below.<br>
If you wish to change the port of the service, then set it like that:
````
server.port=<PORT>
````

#### Setting up database
For setting up the database (mysql is recommended) you just need to configure following
options:

````
spring.datasource.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?autoReconnect=true
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
````