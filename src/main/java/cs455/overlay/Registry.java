package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.routing.Server;
import cs455.overlay.routing.Server.ServerThread;

public class Registry extends Thread{
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

            while(!command.equals("exit-overlay")){

                System.out.print("Enter a command: ");
                command = input.next();

                if(command.equals("setup-overlay")){
                    server = new Server(port, numOfConnections);
                    ServerThread serverThread = new ServerThread(server);
                    new Thread(serverThread).start();
                    setupComplete = true;
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(Node node : nodesList){
                        System.out.println("Node #" + node.identifier + " is connected from Host: " + node.ip + " Port: " + node.port);
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                }
                else {
                    System.out.println("Error: Commands consist of 'setup-overlay', 'list-messaging-nodes', and 'start {NUMBER_OF_MESSAGES}'");
                    System.out.println("Note: To start or list-messaging-nodes you must setup-overlay first.");
                }
            }
            System.out.println("Closing All Connections... ");
            System.exit(0);
        }
        else if(args[1].equals("node")){
            Scanner input = new Scanner(System.in);
            String serverHost = args[2];
            int serverPort = Integer.parseInt(args[3]);
            String command = "";

            try {
                Socket sock = new Socket(serverHost, serverPort);
                Node node = new Node(sock);
                new Thread(node).start();

                while(!command.equals("exit-overlay")){
                    command = input.next();
                }

                sock.close();
            } catch (UnknownHostException un) {
                System.out.println("Error Node: " + un.getMessage());
            } catch (IOException e) {
                System.out.println("Error Node: " + e.getMessage());
            }
        }
    }
}
