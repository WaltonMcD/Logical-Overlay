## Getting Started
___
### Cloning
- git clone https://github.com/WaltonMcD/Homework-1.git
___
### Gradle
#### To setup gradle with the current `build.gradle`
- `gradle build`

#### After a successful build an executable jar will be placed in gradle's `build` folder under `libs`.
___
### Execute
- To use the executable we have provided a script `start.sh`.
- To run the script...
- `./start.sh [NUMBER_OF_NODES] [CSU_ename]`
- `example: ./start.sh 10 janeDoe`
#### 
- You will be prompted to enter a command for the registry. The commands are as follows...
- `setup-overlay` : This will start the server allowing nodes to connect.
- `list-messaging-nodes` : This will list all nodes connected showing id, port, and host.
- `start [NUMBER_OF_MESSAGES]` : this will start the client process of sending the specified amount of messages to its neighbor.
- `exit-overlay` : Exits the program gracefully.
___

