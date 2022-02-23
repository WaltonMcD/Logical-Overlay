Walton  McDonald - connor21@colostate.edu
Spencer Howlett  - spencer.v.howlett@gmail.com
Kevin   Conner   - kevcon@colostate.edu

Java programs have been compiled on a CSU server using Gradle.
The `gradle build` command can be used to compile a fresh build in the
Homework-1 project directory.

The Registry and Node programs can be started with the following commands.

Execute:
To use the executable we have provided a script `start.sh`.
To run the script...
`./start.sh [NUMBER_OF_NODES] [CSU_ename]`
`example: ./start.sh 10 janeDoe`

-After running the script you will have 15 seconds to setup the overlay before nodes attempt to connect.
-Highly recommend using ssh keys for using the script.

You will be prompted to enter a command for the registry. The commands are as follows...
`setup-overlay`              : This will start the server allowing nodes to connect.
`list-messaging-nodes`       : This will list all nodes connected showing id, port, and host.
`start [NUMBER_OF_MESSAGES]` : this will start the client process of sending the specified amount of messages to its neighbor.
`exit-overlay`               : Exits the program gracefully.

Registry:
java -jar build/libs/Homework-1.jar cs455.overlay.Main registry [Registry PORT] [n Connections]

Registry Example:
java -jar build/libs/Homework-1.jar cs455.overlay.Main registry 46001 2

Node:
java -jar build/libs/Homework-1.jar cs455.overlay.Main node [Registry Hostname] [Registry Port]

Node Example:
java -jar build/libs/Homework-1.jar cs455.overlay.Main node austin.cs.colostate.edu 46001


File Manifest:
Homework-1/README.txt
Homework-1/build.gradle

Homework-1/src/main/java/cs455/overlay/Main.java
Homework-1/src/main/java/cs455/overlay/node/Node.java
Homework-1/src/main/java/cs455/overlay/node/ToNode.java
Homework-1/src/main/java/cs455/overlay/node/FromNode.java
Homework-1/src/main/java/cs455/overlay/protocols/Message.java
Homework-1/src/main/java/cs455/overlay/routing/Registry.java
Homework-1/src/main/java/cs455/overlay/routing/RegistryThread.java
Homework-1/src/main/java/cs455/overlay/wireformats/ConnDirectiveFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/DoneMessageFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/PayloadMessageFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/RegisterMessageFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/RegResponseFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/TaskCompleteFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/TaskInitiateFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/TrafficSummaryFormat.java
Homework-1/src/main/java/cs455/overlay/wireformats/TrafficSumRequestFormat.java

Homework-1/build/libs/Homework-1.jar
Homework-1/build/classes/java/main/cs455/overlay/Main.class
Homework-1/build/classes/java/main/cs455/overlay/node/Node.class
Homework-1/build/classes/java/main/cs455/overlay/node/ToNode.class
Homework-1/build/classes/java/main/cs455/overlay/node/FromNode.class
Homework-1/build/classes/java/main/cs455/overlay/protocols/Message.class
Homework-1/build/classes/java/main/cs455/overlay/routing/Registry.class
Homework-1/build/classes/java/main/cs455/overlay/routing/RegistryThread.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/ConnDirectiveFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/DoneMessageFormat.classkkj
Homework-1/build/classes/java/main/cs455/overlay/wireformats/PayloadMessageFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/RegisterMessageFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/RegResponseFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/TaskCompleteFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/TaskInitiateFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/TrafficSummaryFormat.class
Homework-1/build/classes/java/main/cs455/overlay/wireformats/TrafficSumRequestFormat.class
Homework-1/test/build/tmp/compileJava/previous-compilation-data.bin
Homework-1/test/build/tmp/jar/MANIFEST.MF



