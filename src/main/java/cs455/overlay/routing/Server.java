package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cs455.overlay.Registry;
import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;

public class Server {
    public static ArrayList<Message> directives = new ArrayList<Message>();
    public static ArrayList<NodeThread> nodeThreads = new ArrayList<NodeThread>();
    private ServerSocket serverSocket = null;
    public static Integer numOfConnections; 
    private static Integer numberOfMessages;
    private static boolean startFlag = false;

    public Server(Integer port, Integer numOfConnections){
        try{
            serverSocket = new ServerSocket(port);
            Server.numOfConnections = numOfConnections;
        }
        catch (IOException ioe) {
            System.out.print(ioe.getMessage());
        }
    }


    // Spawns a server thread
    public static class ServerThread implements Runnable {
        private Server server = null;

        public ServerThread(Server server){
            this.server = server;
        }

        public synchronized void waitServer(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // we need a synchronized class to be concurrent
        public synchronized void notifyServer(){
            notify();
        }

        @Override
        public void run() {
        	
        	
            try {
                while(true) {
                    Socket incomingConnectionSocket = server.serverSocket.accept();
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println(Thread.currentThread().getName() + " detected interruption, exiting...");
                        incomingConnectionSocket.close();
                        return;
                    }
                    
                    incomingConnectionSocket.setReuseAddress(true);
    
                    NodeThread nodeSock = new NodeThread(incomingConnectionSocket, this);
                    nodeThreads.add(nodeSock);
                    new Thread(nodeSock).start();
                    waitServer(); //We need to wait for the node to register before doing checks
    
                    // Once all nodes are connected this will assign nodes to connect to.
                    if(Registry.nodesList.size() == numOfConnections){
                        // Uses arraylist to assign a ring structure if first node is i = 0 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 9
                        // next rendition i = 1 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 0
                        for(int i = 0; i < numOfConnections; i++){
                            Integer messageType = 9;
                            Integer identifier = Registry.nodesList.get(i).identifier;
                            Integer frontPort = Registry.nodesList.get((i + 1) % numOfConnections).identifier;
                            String frontIp = Registry.nodesList.get((i + 1) % numOfConnections).ip;
                            Integer backPort = Registry.nodesList.get((i + numOfConnections-1) % numOfConnections).identifier;
                            String backIp = Registry.nodesList.get((i + numOfConnections-1) % numOfConnections).ip;
                            Message connDirective = new Message(frontIp, messageType, identifier, frontPort, backIp, backPort);
                            directives.add(connDirective);
                            
                        }
                        for(NodeThread node: nodeThreads){
                            node.notifyNodeThread();
                        }
                    }   
                }
            }
            catch (IOException ioe) {
                System.out.print(ioe.getMessage());
            }
        }
    }
    

    // Thread to handle Node socket 
    public static class NodeThread implements Runnable {
        public final Socket nodeSocket;
        public final Integer identifier;
        public ServerThread server;

        public NodeThread(Socket nodeSocket, ServerThread server) {
            this.nodeSocket = nodeSocket;
            this.identifier = nodeSocket.getPort();
            this.server = server;
        }

        public synchronized void waitNodeThread(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void notifyNodeThread(){
            notify();
        }

        @Override
        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(nodeSocket.getOutputStream()));
            

                // Receive Registration Request
				Message registrationRequestMsg = new Message();
				registrationRequestMsg.unpackMessage(inputStream);
				Registry.nodesList.add(new Node(registrationRequestMsg.getIpAddress(), registrationRequestMsg.getPort(), identifier));
				server.notifyServer();

				// Send Registration Response
				Message registrationResponseMsg = new Message(2, 200, identifier, "\'Welcome\'");
				registrationResponseMsg.packMessage(outputStream);

				// Send Connection Directive
				if(Server.directives.size() != numOfConnections){
				    waitNodeThread();
				}

				Message connectionDirectiveMsg = null;
				for(int i = 0; i < directives.size(); i++){
				    if(directives.get(i).getIdentifier().equals(this.identifier)){
				        connectionDirectiveMsg = new Message(3, directives.get(i).getFrontNodePort(), directives.get(i).getFrontNodeIp(), directives.get(i).getBackNodePort(), directives.get(i).getBackNodeIp());
				        connectionDirectiveMsg.packMessage(outputStream);
				    }
				}

				this.waitNodeThread();

                while(numberOfMessages == null){
                    if(numberOfMessages != null){
                        break;
                    }
                }

				// Send Task Initiate
				Message taskInitiateMsg = new Message(4, numberOfMessages);
				taskInitiateMsg.packMessage(outputStream);

				//Receive Task Complete
				Message taskCompleteMsg = new Message();
				taskCompleteMsg.unpackMessage(inputStream);
                Registry.completedTasks.add(taskCompleteMsg);

                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                

				//Send Traffic Summary Request.
				Integer trafficSummReqType = 7;
				Message trafficSummReqMsg = new Message(trafficSummReqType, this.nodeSocket.getLocalAddress().getHostName());
				trafficSummReqMsg.packMessage(outputStream);

                //Receive Traffic Summary
                Message trafficSummary = new Message();
                trafficSummary.unpackMessage(inputStream);
                System.out.println(trafficSummary.getType() + " From: " + trafficSummary.getIpAddress());
                Registry.trafficSummaryMessages.add(trafficSummary);

                Registry.startSequenceCompletion();
                
                
                inputStream.close();
                outputStream.close();
                nodeSocket.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
    }

    public static void setNumberOfMessages(Integer number) {
        numberOfMessages = number;
    }
}
