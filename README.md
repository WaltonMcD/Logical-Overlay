## Getting Started
___
### Cloning
- git clone https://github.com/WaltonMcD/Homework-1.git
___
### Gradle
#### To setup gradle with the current `build.gradle`
- `gradle init`
- `gradle build`

#### After a successful build an executable jar will be placed in gradle's `build` folder under `libs`.
___
### Server
- To use the executable you must first start the sever to host nodes.
#### `java -jar build/libs/Homework-1.jar cs455.overlay.Registry server [YOUR_PORT] [NUMBER_OF_CONNECTIONS]`
- You will be prompted to enter a command from the registry. The commands are as follows...
- `setup-overlay` : This will start the server allowing nodes to connect.
- `list-messaging-nodes` : This will list all nodes connected showing id, port, and host.
- `start [NUMBER_OF_MESSAGES]` : this will start the client process of sending the specified amount of messages to its neighbor.
- `exit-overlay` : Exits the program gracefully.
___
### Node
#### `java -jar build/libs/Homework-1.jar cs455.overlay.Registry node [YOUR_SERVER_ADDRESS] [YOUR_SERVER_PORT]`
- The current rendition sends a registration request and accepts a registration response `exit-overlay`.
