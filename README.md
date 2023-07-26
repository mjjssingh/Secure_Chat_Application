
# Secure Chat Application (CLI based)


The Secure Chat Application is a simple and secure chat application written in Java, providing an enhanced level of security through RSA encryption.





## Deployment

### Running the Application on the Same System

To run this application on the same system, please follow these steps:

1. Ensure you have the correct key file paths set in the loadPublicKey() and loadPrivateKey() functions.

#### Server side

- Open your terminal or command prompt and navigate to the folder containing

```bash
    javac Server.java
```

```bash
    java Server
```

#### Client side

- Open another terminal or command prompt and navigate to the folder containing Client.java.

```bash
    javac Client.java
```

```bash
    java Client
```

### Running the Application on Different Systems

To run this application on different systems, make the following adjustments:

1. Modify the code in Client.java: 

```java
    socket = new Socket("localhost", 1234);
```

Replace `"localhost"` with your Server's IP address.

- You can use the same key files provided in this repository for both the Server and Client sides. Make sure to set the correct key file paths in the loadPublicKey() and loadPrivateKey() functions.

*OR*

- Generate private and public keys yourself using the KeyPairRSAGeneratorUtil.java class and exchange the public keys between the Client and Server.



#### Server side

- Open your terminal or command prompt and navigate to the folder containing `Server.java`.
```bash
    javac Server.java
```

```bash
    java Server
```

#### Client side

- Open another terminal or command prompt and navigate to the folder containing `Client.java`.

```bash
    javac Client.java
```

```bash
    java Client
```

Remember to ensure that the correct key file paths are set for the RSA encryption to work correctly.
