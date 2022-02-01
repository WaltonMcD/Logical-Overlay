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
### Execution
- To use the executable you must first start the sever to host nodes.
#### `java -jar build/libs/Homework-1.jar cs455.overlay.Registry server [YOUR_PORT] [NUMBER_OF_CONNECTIONS]`
- After starting the server you then may connect as many nodes as you have chosen. The true max would be dependent on how many logical processors the machine hosting the server has available.
#### `java -jar build/libs/Homework-1.jar cs455.overlay.Registry node [YOUR_SERVER_ADDRESS] [YOUR_SERVER_PORT]`
- The current rendition allows the nodes to send a message to the server and be displayed by the server. Termination command is `Exit`.