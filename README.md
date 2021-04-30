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
* SMTP Server (Optional)

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

#### Setting up SMTP
The application makes use of the Spring JavaMailSender to notify users about actions on their
accounts. This package is used specifically to send welcome mails, account recoveries or 
notifications about account changes. The following properties need to be edited to use SMTP:
````
spring.mail.host=<SMTP_HOST>
spring.mail.port=<SMTP_PORT>
spring.mail.username=<USERNAME_OF_SMTP_MAIL>
spring.mail.password=<PASSWORD_OF_SMTP_MAIL>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

### Configure this option to change the base url of links in emails
alliance.url=<BASE_URL>
````