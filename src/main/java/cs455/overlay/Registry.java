package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.routing.Server;
import cs455.overlay.routing.Server.ServerThread;

public class Registry {
    public static ArrayList<Node> nodesList = null;
    public static void main(String[] args) {
        nodesList = new ArrayList<Node>();

        if(args[1].equals("server")){
            Scanner input = new Scanner(System.in);
            Server server = null;
            Boolean setupComplete = false;
            String command = "";
            Integer port = Integer.parseInt(args[2]);
            Integer numOfConnections = Integer.parseInt(args[3]);

            while(true){
                System.out.println("Enter a command: ");
                command = input.next();

                if(command.equals("setup-overlay")){
                    server = new Server(port, numOfConnections);
                    ServerThread serverThread = new ServerThread(server);
                    new Thread(serverThread).start();
                    setupComplete = true;
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(Server.NodeThread node : server.getNodes()){
                        System.out.println("Node #" + node.nodeNum + " is connected from Host: " + node.nodeSocket.getInetAddress() + " Port: " + node.nodeSocket.getLocalPort());
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                }
                else if(nodesList.size() == numOfConnections ){
                    continue;
                }
                else if(command.equals("exit-overlay")){
                    System.out.println("Closing All Connections... ");
                    System.exit(0);
                }
                else {
                    System.out.println("Error: Commands consist of 'setup-overlay', 'list-messaging-nodes', and 'start {NUMBER_OF_MESSAGES}'");
                    System.out.println("Note: To start or list-messaging-nodes you must setup-overlay first.");
                }
            }
        }
        else if(args[1].equals("node")){
            String serverHost = args[2];
                int serverPort = Integer.parseInt(args[3]);
                try{
                    Socket nodeSocket = new Socket(serverHost, serverPort);
                    Node node = new Node(nodeSocket);
                    nodesList.add(node);
                    node.run();
                }catch (IOException ioe){
                    System.out.println(ioe.getMessage());
                } 
        }
    }
    
    
}
