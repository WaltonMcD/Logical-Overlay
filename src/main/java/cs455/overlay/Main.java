package cs455.overlay;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import cs455.overlay.node.MessagerNode;
import cs455.overlay.node.Node;
import cs455.overlay.routing.NewRegistry;
import cs455.overlay.routing.RegistryNodeThread;

public class Main {
    public static Boolean setupComplete = false;
    public static void main(String[] args) throws IOException {
        if(args.length != 4){
            System.out.println("Error: Incorrect Arguments");
            System.out.println("Expected arguments for registry: java -jar build/libs/[YOUR_JAR_FILE] cs455.overlay.Registry registry [YOUR_PORT] [NUMBER_OF_NODES]");
            System.out.println("Expected arguments for node: java -jar build/libs/[YOUR_JAR_FILE] cs455.overlay.node.Node node [REGISTRY_HOSTNAME] [REGISTRY_PORT]");
            return;
        }


        if(args[1].equals("registry")){
            
            Scanner input = new Scanner(System.in);
            String command = "";
            
            int serverPort = Integer.parseInt(args[2]);
            int numberOfConnections = Integer.parseInt(args[3]);
            NewRegistry registry = null;

            while(!command.equals("exit-overlay")){

                System.out.print("Enter a command: ");
                command = input.next();

                if(command.equals("setup-overlay")){
                	if(!setupComplete) {
	                    registry = new NewRegistry(serverPort, numberOfConnections);
                        new Thread(registry).start();
	                    setupComplete = true;
                	} else {
                		System.out.println("Overlay is already setup.");
                		continue;
                	}
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(RegistryNodeThread node : registry.getNodesList()){
                        System.out.println("Node"+ " is connected from Host: " + node.getIp() + " Port: " + node.getPort());
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                    registry.setNumberOfMessagesToSend(numberOfMessages);
                    for(RegistryNodeThread node : registry.getNodesList()){
                        node.notifyRegNodeThread();
                    }
                }
                else if(command.equals("exit-overlay")) {
                	if(setupComplete)
                		registry.exitOverlay();
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
            String command = "";
            
            String regHost = args[2];
            int regPort = Integer.parseInt(args[3]);
           
            Random random = new Random();
            String identifier = InetAddress.getLocalHost().getHostName();

            try {
                MessagerNode node = new MessagerNode(regHost, regPort, identifier);
                Thread thread = new Thread(node);
                thread.start();

                while(!command.equals("exit-overlay")){
                    command = input.next();
                }
                
                thread.interrupt();
            } catch (UnknownHostException un) {
                System.out.println("Error Node: " + un.getMessage());
            } catch (IOException e) {
                System.out.println("Error Node: " + e.getMessage());
            }
        }
    }
}
