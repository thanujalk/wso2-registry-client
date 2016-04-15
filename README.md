# WSO2 G-REG Sample Client for Copy Resources
This sample client provide resource copying capability with WSO2 G-REG 5.1.0.

## Features:
* Copy single resource
* Copy entire resource tree
* Ignore specific paths while copying a resource tree

## Run Binary
* Download this [zip](https://github.com/thanujalk/wso2-registry-client/raw/master/wso2-registry-client-binary.zip) file and extract it.
* Go to wso2-registry-client-binary folder/resources and open client.properties file. Update file with approopriate 
values. (If you don't have any paths to escape, comment "ESCAPE.PATHS" line)
```properties
#G-REG Server Host Name
GREG.URL = https://localhost:9443/services/
#User Name
GREG.USERNAME = admin
#Password
GREG.PASSWORD = admin
#From path
FROM.PATH = /_system/config
#To path
TO.PATH = /_system/config/prod
#Escaping paths (comma separated)
ESCAPE.PATHS = /_system/config/dev,/_system/config/qa
#Additional configs
AXIS2.REPO = resources
AXIS2.CONF = resources/axis2_client.xml
TRUST.STORE.LOCATION = resources/security/wso2carbon.jks
TRUST.STORE.PASSWORD = wso2carbon
```
* Open a terminal and go to the wso2-registry-client-binary folder. Enter following command to start copying.

    `java -jar wso2-registry-client.jar`

## Building From Source

Clone this repository first (`git clone https://github.com/thanujalk/wso2-registry-client.git`) and use Maven install to build
`mvn clean install`

