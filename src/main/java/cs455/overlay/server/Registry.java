package cs455.overlay.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.server.Server.NodeThread;
import cs455.overlay.server.Server.ServerThread;

public class Registry {
    public static void main(String[] args) {
        if(args[1].equals("server")){
            Scanner input = new Scanner(System.in);
            Server server = null;
            Boolean setupComplete = false;
            String command = "";

            while(true){
                System.out.println("Enter a command: ");
                command = input.next();

                if(command.equals("setup-overlay")){
                    Integer port = Integer.parseInt(args[2]);
                    Integer numOfConnections = Integer.parseInt(args[3]);

                    server = new Server(port, numOfConnections);
                    ServerThread serverThread = new ServerThread(server);
                    new Thread(serverThread).start();
                    setupComplete = true;
                }
                else if(command.equals("list-messaging-nodes")){
                    for(Server.NodeThread node : server.getNodes()){
                        System.out.println("Node #" + node.clientNum + " is connected from Host: " + node.nodeSocket.getInetAddress() + " Port: " + node.nodeSocket.getLocalPort());
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                }
                else if(command.equals("exit-overlay")){
                    System.out.println("Closing All Connections... ");
                    System.exit(0);
                }
                else {
                    System.out.println("Error: Commands consist of 'setup-overlay', 'list-messaging-nodes', and 'start {NUMBER_OF_MESSAGES}");
                    System.out.println("To start you must setup-overlay first.");
                }
            }
        }
        else if(args[1].equals("node")){
            String serverHost = args[2];
                int serverPort = Integer.parseInt(args[3]);
                try{
                    Socket nodeSocket = new Socket(serverHost, serverPort);
                    Node node = new Node(nodeSocket);
                    node.run();
                }catch (IOException ioe){
                    System.out.println(ioe.getMessage());
                } 
        }
    }
    
    
}
