package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.routing.Registry;
import cs455.overlay.routing.RegistryThread;

public class Main extends Thread{
    public static Integer serverPort = 0;
    public static Integer numOfConnections = 0;

    public static void main(String[] args) {

        if(args[1].equals("server")){
            Scanner input = new Scanner(System.in);
            Registry registry = null;
            Boolean setupComplete = false;
            String command = "";
            serverPort = Integer.parseInt(args[2]);
            Thread overlayThread = null;
            
            numOfConnections = Integer.parseInt(args[3]);

            while(!command.equals("exit-overlay")){

                System.out.print("Enter a command: ");
                command = input.next();

                if(command.equals("setup-overlay")){
                	if(!setupComplete) {
	                    registry = new Registry(serverPort, numOfConnections);
	                    overlayThread = new Thread(registry);
	                    overlayThread.start();
	                    setupComplete = true;
                	} else {
                		System.out.println("Overlay is already setup.");
                		continue;
                	}
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(Node node : registry.nodesList){
                        System.out.println("Node #" + node.identifier + " is connected from Host: " + node.ip + " Port: " + node.port);
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                    registry.setNumberOfMessages(numberOfMessages);

                    for(RegistryThread node: registry.nodeThreads){
                        node.notifyRegThread();
                    }
                }
                else if(command.equals("exit-overlay")) {
                	if(setupComplete)
                		overlayThread.interrupt();
                        System.out.println("Closing All Connections... ");
                        System.exit(1);
                }
                else {
                    System.out.println("Error: Commands consist of 'setup-overlay', 'list-messaging-nodes', and 'start {NUMBER_OF_MESSAGES}'");
                    System.out.println("Note: To start or list-messaging-nodes you must setup-overlay first.");
                }
            }
        }
        else if(args[1].equals("node")){
            Scanner input = new Scanner(System.in);
            String serverHost = args[2];
            serverPort = Integer.parseInt(args[3]);
            String command = "";
            Socket sock = null;

            try {
                sock = new Socket(serverHost, serverPort);
                Node node = new Node(sock);
                Thread thread = new Thread(node);
                thread.start();

                while(!command.equals("exit-overlay")){
                    command = input.next();
                }
                
                thread.interrupt();
                sock.close();
            } catch (UnknownHostException un) {
                System.out.println("Error Node: " + un.getMessage());
            } catch (IOException e) {
                System.out.println("Error Node: " + e.getMessage());
            }
        }
    }
}
