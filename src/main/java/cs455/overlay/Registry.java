package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cs455.overlay.Client;
import cs455.overlay.Server;

public class Registry {
  
        public static void main(String[] args) throws IOException{
            if(args[1].equals("server")){
                int port = Integer.parseInt(args[2]);
                Server server = new Server(port);
    
            }else if(args[1].equals("client")){
                String serverHost = args[2];
                int serverPort = Integer.parseInt(args[3]);
                try{
                    Socket clientSocket = new Socket(serverHost, serverPort);
                    Client client = new Client(clientSocket, serverPort);
                    client.run();
                }catch (UnknownHostException un){
                    System.out.println(un.getMessage());
                }
                
                
            }else{
                System.out.println("Error: Possible inputs include {client [HOST] [PORT]}");
            }
        }
}

