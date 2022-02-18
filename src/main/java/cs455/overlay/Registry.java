package cs455.overlay;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;
import cs455.overlay.routing.Server;
import cs455.overlay.routing.Server.ServerThread;
import cs455.overlay.routing.Server.NodeThread;

public class Registry extends Thread{
    public static ArrayList<Message> trafficSummaryMessages = new ArrayList<Message>();
    public static ArrayList<Message> completedTasks = new ArrayList<Message>();
    public static ArrayList<Node> nodesList = null;
    public static Integer serverPort = 0;
    public static Integer numOfConnections = 0;

    public static synchronized void startSequenceCompletion(){
        if(trafficSummaryMessages.size() == numOfConnections){
            Integer totalMessagesSent = 0;
            Integer totalMessagesReceived = 0;
            long totalPayloadSent = 0;
            long totalPayloadReceived = 0;
            int totalMessages = 0;

            for(Message msg : trafficSummaryMessages){
                totalMessagesSent += msg.getNumMessagesSent();
                totalMessagesReceived += msg.getNumMessagesReceived();
                totalPayloadSent += msg.getSumOfSentMessages();
                totalPayloadReceived += msg.getSumOfReceivedMessages();
                totalMessages += msg.getNumMessagesSent() + msg.getNumMessagesReceived();
            }
            System.out.println("Sent a total of " + totalMessagesSent + " Messages" +
                            " Received a total of " + totalMessages + " Messages" +
                            " Total sent payload " + totalPayloadSent +
                            " Total received payload " + totalPayloadReceived);
        }
    }
    public static void main(String[] args) {
        nodesList = new ArrayList<Node>();

        if(args[1].equals("server")){
            Scanner input = new Scanner(System.in);
            Server server = null;
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
	                    server = new Server(serverPort, numOfConnections);
	                    ServerThread serverThread = new ServerThread(server);
	                    overlayThread = new Thread(serverThread);
	                    overlayThread.start();
	                    setupComplete = true;
                	} else {
                		System.out.println("Overlay is already setup.");
                		continue;
                	}
                }
                else if(command.equals("list-messaging-nodes") && setupComplete == true){
                    for(Node node : nodesList){
                        System.out.println("Node #" + node.identifier + " is connected from Host: " + node.ip + " Port: " + node.port);
                    }
                }
                else if(command.equals("start") && setupComplete == true){
                    Integer numberOfMessages = input.nextInt();
                    System.out.println("Starting to send messages. Count: " + numberOfMessages);
                    Server.setNumberOfMessages(numberOfMessages);

                    for(NodeThread node: Server.nodeThreads){
                        node.notifyNodeThread();
                    }
                }
                else if(command.equals("exit-overlay")) {
                	if(setupComplete)
                		overlayThread.interrupt();
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
