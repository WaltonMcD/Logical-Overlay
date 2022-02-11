package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cs455.overlay.Registry;
import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;

public class Server {
    public static ArrayList<Message> directives = new ArrayList<Message>();
    public static ArrayList<NodeThread> nodeThreads = new ArrayList<NodeThread>();
    private ServerSocket serverSocket = null;
    public static Integer numOfConnections; 
    private static Integer numberOfMessages;
    private static boolean startFlag = false;

    public Server(Integer port, Integer numOfConnections){
        try{
            serverSocket = new ServerSocket(port);
            Server.numOfConnections = numOfConnections;
        }
        catch (IOException ioe) {
            System.out.print(ioe.getMessage());
        }
    }


    // Spawns a server thread
    public static class ServerThread implements Runnable {
        private Server server = null;

        public ServerThread(Server server){
            this.server = server;
        }

        public synchronized void waitServer(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // we need a synchronized class to be concurrent
        public synchronized void notifyServer(){
            notify();
        }

        @Override
        public void run() {
            try {
                while(true) {
                    Socket incomingConnectionSocket = server.serverSocket.accept();
                    incomingConnectionSocket.setReuseAddress(true);
    
                    NodeThread nodeSock = new NodeThread(incomingConnectionSocket, this);
                    nodeThreads.add(nodeSock);
                    new Thread(nodeSock).start();
                    waitServer(); //We need to wait for the node to register before doing checks
    
                    // Once all nodes are connected this will assign nodes to connect to.
                    if(Registry.nodesList.size() == numOfConnections){
                        System.out.println("Maximum number of nodes connected.");
                        // Uses arraylist to assign a ring structure if first node is i = 0 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 9
                        // next rendition i = 1 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 0
                        for(int i = 0; i < numOfConnections; i++){
                            Integer messageType = 9;
                            Integer identifier = Registry.nodesList.get(i).identifier;
                            Integer frontPort = Registry.nodesList.get((i + 1) % numOfConnections).port;
                            String frontIp = Registry.nodesList.get((i + 1) % numOfConnections).ip;
                            Integer backPort = Registry.nodesList.get((i + numOfConnections-1) % numOfConnections).port;
                            String backIp = Registry.nodesList.get((i + numOfConnections-1) % numOfConnections).ip;
                            Message connDirective = new Message(messageType, identifier, frontPort, frontIp, backPort, backIp);
                            directives.add(connDirective);
                            
                        }
                        for(NodeThread node: nodeThreads){
                            node.notifyNodeThread();
                        }
                    }
                    else if(Registry.nodesList.size() > numOfConnections){
                        System.out.println("Maximum number of connections exceeded. Max: " + numOfConnections);
                        server.serverSocket.close();
                        break;
                    }
                    
                }
                System.out.println("Closing Server Socket... ");
            }
            catch (IOException ioe) {
                System.out.print(ioe.getMessage());
            }
        }
    }
    

    // Thread to handle Node socket 
    public static class NodeThread implements Runnable {
        public final Socket nodeSocket;
        public final Integer identifier;
        public ServerThread server;

        public NodeThread(Socket nodeSocket, ServerThread server) {
            this.nodeSocket = nodeSocket;
            this.identifier = nodeSocket.getPort();
            this.server = server;
        }

        public synchronized void waitNodeThread(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void notifyNodeThread(){
            notify();
        }

        @Override
        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(nodeSocket.getOutputStream()));

                try{

                    // Receive Registration Request
                    Integer messageType = inputStream.readInt();
                    String ip = inputStream.readUTF();
                    Integer port = inputStream.readInt();
                    
                    Message registrationRequest = new Message(messageType, ip, port);
                    Registry.nodesList.add(new Node(ip, identifier, port));
                    server.notifyServer();
    
                    System.out.println("\n" + registrationRequest.getType() + " From Host: " + registrationRequest.ipAddress + "  Port: " + registrationRequest.port);


                    // Send Registration Response
                    Message registrationResponse = new Message(2, 200, identifier, "\'Welcome\'");
                    
                    outputStream.writeInt(registrationResponse.messageType);
                    outputStream.writeInt(registrationResponse.statusCode);
                    outputStream.writeInt(registrationResponse.identifier);
                    outputStream.writeUTF(registrationResponse.additionalInfo);
                    outputStream.flush();


                    // Send Connection Directive
                    if(Server.directives.size() != numOfConnections){
                        waitNodeThread();
                    }

                    Message connectionDirective = null;
                    for(int i = 0; i < directives.size(); i++){
                        if(directives.get(i).identifier.equals(this.identifier)){
                            connectionDirective = new Message(3, directives.get(i).frontNodePort, directives.get(i).frontNodeIp, directives.get(i).backNodePort, directives.get(i).backNodeIp);

                            outputStream.writeInt(connectionDirective.messageType);
                            outputStream.writeInt(connectionDirective.frontNodePort);
                            outputStream.writeUTF(connectionDirective.frontNodeIp);
                            outputStream.writeInt(connectionDirective.backNodePort);
                            outputStream.writeUTF(connectionDirective.backNodeIp);
                            outputStream.flush();
                        }
                    }

                    this.waitNodeThread();

                    Message taskInitiate = new Message(4, numberOfMessages);
                    outputStream.writeInt(taskInitiate.messageType);
                    outputStream.writeInt(taskInitiate.messagesToSend);
                    outputStream.flush();
                }
                catch(IOException ioe){
                    System.out.println(ioe.getMessage());
                }
                
                inputStream.close();
                outputStream.close();
                nodeSocket.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
    }

    public static void setNumberOfMessages(Integer number) {
        numberOfMessages = number;
    }
}
