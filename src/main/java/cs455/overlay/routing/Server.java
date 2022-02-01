package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

    public static class ServerThread implements Runnable{
        private Server server = null;

        public ServerThread(Server server){
            this.server = server;
        }

        @Override
        public void run() {
            try {
                System.out.println("Created Server Socket... ");
                while(true) {
                    Socket incomingConnectionSocket = server.serverSocket.accept();
                    incomingConnectionSocket.setReuseAddress(true);
                    System.out.println("Received a connection. Node: " + incomingConnectionSocket.getPort() + " " + incomingConnectionSocket.getInetAddress());
    
                    NodeThread nodeSock = new NodeThread(incomingConnectionSocket, incomingConnectionSocket.getPort(), nodes);
                    nodes.add(nodeSock);
                    System.out.println("Currently " + nodes.size() + " node(s) connected.");
    
                    if(nodes.size() == server.numOfConnections){
                        System.out.println("Maximum number of clients connected.");
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

    // Thread Handler
    public static class NodeThread implements Runnable {
        public final Socket nodeSocket;
        public final Integer clientNum;
        private ArrayList<NodeThread> neighborNodes;

        public NodeThread(Socket nodeSocket, Integer clientNum, ArrayList<NodeThread> neighborNodes) {
            this.nodeSocket = nodeSocket;
            this.clientNum = clientNum;
            this.neighborNodes = neighborNodes;
        }

        @Override
        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                String line = "";

                //constantly excepting input until termination string is provided.
                while(!line.equals("exit-overlay")){
                    try{
                        Integer msgLength = inputStream.readInt();
                        byte[] msg = new byte[msgLength];
                        inputStream.readFully(msg, 0, msgLength);
                        String str = new String(msg);
                        line = str;
                        System.out.println("Node #" + clientNum + " says: " + line);
                    }
                    catch(IOException ioe){
                        System.out.println(ioe.getMessage());
                    }
                }

                System.out.println("Closing Connection with Node: #" + clientNum);
                getNeighborNodes().remove(this);
                inputStream.close();
                nodeSocket.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

        public synchronized ArrayList<NodeThread> getNeighborNodes() {
            return this.neighborNodes;
        }

    }

    //Getters and Setters
    public ArrayList<NodeThread> getNodes(){
        return nodes;
    }

}
