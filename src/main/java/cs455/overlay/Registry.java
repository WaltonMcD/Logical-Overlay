package cs455.overlay;

import cs455.overlay.Server;

import java.io.IOException;

import cs455.overlay.Client;

public class Registry {
  
        public static void main(String[] args) throws IOException{
            if(args[1].equals("server")){
                int port = Integer.parseInt(args[2]);
                Server server = new Server(port);
    
            }else if(args[1].equals("client")){
                String serverHost = args[2];
                int serverPort = Integer.parseInt(args[3]);
                Client client = new Client(serverHost, serverPort);
            }else{
                System.out.println("Error: Possible inputs include {client [HOST] [PORT]}");
            }
        }
}

