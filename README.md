ShareHouse (https://play.google.com/store/apps/details?id=com.akash.sharehouse)

An Android app to share a family owned vacation house and makes sure that every owner gets their turn to enjoy the house. 


Prerequisites

You would need to install the following list of software in your system

Java 7, Maven, Eclipse IDE (Mars+), Android Studio, Git, Tortoise Git (Optional), MySQL


Deployment

    Step 1 - Maven Build of the Server

Navigate to src/Server/MavAdvise and run the following in the command line

	mvn clean install

This will generate the WAR file in the src/Server/MavAdvise/target folder, which can be deployed in Tomcat server

    Step 2 - Run the Android App

Built With

    Android SDK - Android SDK
    SpringBoot - Java Web Framework
    Hibernate - Java ORM Framework
    Maven - Dependency Management
    Gradle - Android Build tool

