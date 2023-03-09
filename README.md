# ASUR web-application for controlling robots with MQTT

## Building

### Main build in Linux OS with command:
Need to install java 8 or higher JDK
Need to install gradle
Build with gradle
./gradlew build

### Automatic script build and deploy to local or remote machine
Need to install java 8 or higher JDK (sudo apt install openjdk-java8-jdk)
Need to install gradle
Need to install docker and docker-compose

./deploy.sh -local

build and start on localhost

./deploy.sh -prod

build and deploy and start with ssh on remote machine. IP address and port update in deploy.sh file

## Settings
There are two main settings in app:
In docker-compose.yml file MQTT_ADDRESS and MQTT_ID

To change port of app you need to update var server.port in application.properties file

## Other 
Folder with scenario json files will mount in ~/asur directory


