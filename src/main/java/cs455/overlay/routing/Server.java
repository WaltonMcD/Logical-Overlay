package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.Registry;
import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;

public class Server{
    private static ArrayList<NodeThread> nodes = null;
    private ServerSocket serverSocket = null;
    private Integer numOfConnections; 

    public Server(Integer port, Integer numOfConnections){
        try{
            serverSocket = new ServerSocket(port);
            nodes = new ArrayList<NodeThread>();
            this.numOfConnections = numOfConnections;
        }
        catch (IOException ioe) {
            System.out.print(ioe.getMessage());
        }
    }

    // Spawns a server thread
    public static class ServerThread implements Runnable{
        private Server server = null;

        public ServerThread(Server server){
            this.server = server;
        }

        @Override
        public void run() {
            try {
                while(true) {
                    Socket incomingConnectionSocket = server.serverSocket.accept();
                    incomingConnectionSocket.setReuseAddress(true);
    
                    NodeThread nodeSock = new NodeThread(incomingConnectionSocket);
                    nodes.add(nodeSock);
    
                    // Once all nodes are connected this will assign nodes to connect to.
                    if(nodes.size() == server.numOfConnections){
                        System.out.println("Maximum number of nodes connected.");
                    }
                    else if(nodes.size() > server.numOfConnections){
                        System.out.println("Maximum number of connections exceeded. Max: " + server.numOfConnections);
                        server.serverSocket.close();
                        break;
                    }
                    new Thread(nodeSock).start();
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

        public NodeThread(Socket nodeSocket) {
            this.nodeSocket = nodeSocket;
            this.identifier = nodeSocket.getPort();
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
                    System.out.println("\nRegistration Request From Host: " + registrationRequest.ipAddress + "  Port: " + registrationRequest.port);

                    // Send Registration Response
                    Message registrationResponse = new Message(1, 200, 20, "Welcome");
                    
                    outputStream.writeInt(registrationResponse.messageType);
                    outputStream.writeInt(registrationResponse.statusCode);
                    outputStream.writeInt(registrationResponse.identifier);
                    outputStream.writeUTF(registrationResponse.additionalInfo);
                    outputStream.flush();
                    
                }
                catch(IOException ioe){
                    System.out.println(ioe.getMessage());
                }
                
                inputStream.close();
                nodeSocket.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
    }

    //Getters and Setters
    public ArrayList<NodeThread> getNodes(){
        return nodes;
    }

}
