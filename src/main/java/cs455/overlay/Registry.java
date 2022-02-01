package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cs455.overlay.Node;
import cs455.overlay.Server;

public class Registry {
  
        public static void main(String[] args) throws IOException{
            if(args[1].equals("server")){
                Integer port = Integer.parseInt(args[2]);
                Integer numOfConnections = Integer.parseInt(args[3]);
                Server server = new Server(port, numOfConnections);
    
            }else if(args[1].equals("node")){
                String serverHost = args[2];
                int serverPort = Integer.parseInt(args[3]);
                try{
                    Socket nodeSocket = new Socket(serverHost, serverPort);
                    Node node = new Node(nodeSocket);
                    node.run();
                }catch (UnknownHostException un){
                    System.out.println(un.getMessage());
                }
                
                
            }else{
                System.out.println("Error: Possible inputs include 'node [HOST] [PORT]' or 'server [PORT] [NUMBER_OF_CONNECTIONS]'");
            }
        }
}

