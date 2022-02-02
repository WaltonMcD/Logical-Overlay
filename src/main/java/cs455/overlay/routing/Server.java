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
                System.out.println("Created Server Socket... ");
                while(true) {
                    Socket incomingConnectionSocket = server.serverSocket.accept();
                    incomingConnectionSocket.setReuseAddress(true);
                    System.out.println("Received a connection. Node: " + incomingConnectionSocket.getPort() + " " + incomingConnectionSocket.getInetAddress());
    
                    NodeThread nodeSock = new NodeThread(incomingConnectionSocket);
                    nodes.add(nodeSock);
                    Registry.nodesList.add(nodeSock);
                    System.out.println("Currently " + nodes.size() + " node(s) connected.");
    
                    // Once all nodes are connected this will assign nodes to connect to.
                    if(Registry.nodesList.size() == server.numOfConnections){
                        System.out.println("Maximum number of nodes connected.");
                        for(int i = 0; i < server.numOfConnections; i++){
                            NodeThread currentNode = Registry.nodesList.get(i);
                            currentNode.setFrontNode(Registry.nodesList.get( (i + 1) % server.numOfConnections).nodeSocket);
                            currentNode.setBackNode(Registry.nodesList.get( (i + 9) % server.numOfConnections).nodeSocket);
                        }
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
        public final Integer nodeNum;
        private NodeThread frontNode;
        private NodeThread backNode;

        public NodeThread(Socket nodeSocket) {
            this.nodeSocket = nodeSocket;
            this.nodeNum = nodeSocket.getPort();
        }

        @Override
        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(nodeSocket.getOutputStream()));
                String line = "";

                //constantly excepting input until termination string is provided.
                while(!line.equals("exit-overlay")){
                    try{
                        Integer msgLength = inputStream.readInt();
                        byte[] msg = new byte[msgLength];
                        inputStream.readFully(msg, 0, msgLength);
                        String str = new String(msg);
                        line = str;
                        System.out.println("Node #" + nodeNum + " says: " + line);
                    }
                    catch(IOException ioe){
                        System.out.println(ioe.getMessage());
                    }
                }

                System.out.println("Closing Connection with Node: #" + nodeNum);
                Registry.nodesList.remove(this);
                inputStream.close();
                nodeSocket.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

        public synchronized void setFrontNode(Socket frontNodeSocket){
            for(NodeThread node: Registry.nodesList){
                if(node.nodeSocket == frontNodeSocket){
                    this.frontNode = node;
                }
            }
        }

        public synchronized void setBackNode(Socket backNodeSocket){
            for(NodeThread node: Registry.nodesList){
                if(node.nodeSocket == backNodeSocket){
                    this.backNode = node;
                }
            }
        }

    }

    //Getters and Setters
    public ArrayList<NodeThread> getNodes(){
        return nodes;
    }

}
