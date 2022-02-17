package cs455.overlay.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import cs455.overlay.examples.MessagerNode;
import cs455.overlay.examples.NewRegistry;
import cs455.overlay.examples.RegistryNodeThread;


public class Main {
    public static Boolean setupComplete = false;
    public static void main(String[] args) throws IOException, InterruptedException {

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
                        System.out.print("Number of Messages to Send: ");
                        int numberOfMessages = input.nextInt();
	                    registry = new NewRegistry(serverPort, numberOfConnections);
                        registry.setNumberOfMessagesToSend(numberOfMessages);
                        new Thread(registry).start();
	                    setupComplete = true;
                	} else {
                		System.out.println("Overlay is already setup.");
                		continue;
                	}
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(RegistryNodeThread node : registry.getNodeThreads()){
                        System.out.println("Node: " + node.ip + " is connected from Port: " + node.port);
                    }
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
            } catch (IOException un) {
                System.out.println("Error Node: " + un.getMessage());
            }
        }
    }
}
