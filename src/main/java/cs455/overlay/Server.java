package cs455.overlay;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    
    public Server(Integer port, Integer numOfConnections) throws IOException {
        ServerSocket serverSocket = null;
        ArrayList<NodeThread> nodes = new ArrayList<NodeThread>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Created Server Socket... ");
            int count = 0;
            while(true) {
                Socket incomingConnectionSocket = serverSocket.accept();
                incomingConnectionSocket.setReuseAddress(true);
                count++;
                System.out.println("Received a connection. Node: " + incomingConnectionSocket.getLocalPort() + " " + incomingConnectionSocket.getInetAddress());
                NodeThread nodeSock = new NodeThread(incomingConnectionSocket, count, nodes);
                nodes.add(nodeSock);
                if(nodes.size() == numOfConnections){
                    System.out.println("Maximum number of clients connected.");
                }
                else if(nodes.size() > numOfConnections){
                    System.out.println("Maximum number of connections exceeded. Max: " + numOfConnections);
                    break;
                }
                new Thread(nodeSock).start();
            }
        }
        catch (IOException ioe) {
            System.out.print(ioe.getMessage());
            serverSocket.close();
        }
    }

    // Thread Handler
    private static class NodeThread implements Runnable {
        private final Socket nodeSocket;
        private final Integer clientNum;
        private ArrayList<NodeThread> neighborNodes;

        public NodeThread(Socket nodeSocket, Integer clientNum, ArrayList<NodeThread> neighborNodes) {
            this.nodeSocket = nodeSocket;
            this.clientNum = clientNum;
            this.neighborNodes = neighborNodes;
        }

        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                String line = "";

                //constantly excepting input until termination string is provided.
                while(!line.equals("Exit")){
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
}
