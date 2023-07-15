A complete Spring boot dating application

1. Features

	- form security / csrf protection / server password encryption
	
	- new user actovation via email
	
	- role based authorization
	
	- data model backed by a Postgresql database
	
	- support for internationalization
	
2. Installation

This is a Maven based project so simply download the project to your computer and import it in your IDE as a Maven project. 

3. Running

The easiest way to run this application from an IDE is to install a Spring Tools plugin. This way you can right-click on your project and choose Run as Spring Boot application (in Eclipse
but I guess it's similar in every IDE )

To run the application outside of the IDE, you will first have to build it. This is equivalent to running an mvn -DskipTests install command (in Eclipse you would right-click 
on the project and choose Build ..., which opens a dialogue where you should specify package as the goal and check the skip tests check box. Then, you can run the exe jar as any Java
runnable application.

There's one key difference in the configuration between running the app in the IDE and outside of the IDE. For IDE, you should uncomment ngd.ext-resource.location=static/user/images/ entry 
in /resources/config/application-ngd.properties, whereas outside of the IDE you should uncomment ngd.ext-resource.location=file:///C:/ngd-app/user/images/

4. Setting up your database

The file dating-app-ddl.sql contains all the necessary sql to build your Postgresql database. You will still need to modify the datasource.ngd.jdbc-url entry to match your connetion details.

5. To do

Database backup is not currently implemented.








