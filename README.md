# DseSslConnection

An example Maven project which demonstrates how to connect to a DSE (DataStax Enterprise) cluster using an SSL connection. This particular example is written in Scala. 


## How to Run: 

Navigate to the parent directory and build this project using maven: 
`mvn clean package`

Open up the the config.conf file and modify the values for the respective configuration keys. 

|Config Key                          |Explanation                         |
|-------------------------------|-----------------------------|
|`trustStorePath`            |Path to the truststore|
|`keyStorePath`            |Path to the keystore |
|`trustStorePassword`|Truststore password|
|`keyStorePassword`|Keystore password|
|`clusterIp`|IP address of cluster|
|`username`|DSE username|
|`password`|DSE password|

To run, execute the following command from the parent directory: 

`java -cp target/dse-ssl-1.0-SNAPSHOT-jar-with-dependencies.jar  com.dseExamples.ssl.ClusterConnect`

The output should be something similar to this: 

    Cluster and Session created with SSL
    Release Version: 3.11.0.1900
    Cluster and Session closed

## Modifications

If auth is not enabled on your cluster, navigate to the ClusterConnection class and remove `.withCredentials(username, password)` from the ClusterBuilder as well as the variables **username** and **password**

The **cipherSuites** variable contains the string values for the cipher_suite property in **cassandra.yaml** and **dse.yaml**. This example only includes the 256-bit encryption ciphers. 

The **clusterIp** config key can be modified with multiple ip address values. Here is an example: `clusterIp = 127.0.0.1`
