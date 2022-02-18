package cs455.overlay.routing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.routing.Registry;
import cs455.overlay.Main;
import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;

public class Registry extends Thread {
    public ArrayList<Message> directives = new ArrayList<Message>();
    public ArrayList<Message> trafficSummaryMessages = new ArrayList<Message>();
    public ArrayList<Message> completedTasks = new ArrayList<Message>();

    public ArrayList<RegistryThread> nodeThreads = new ArrayList<RegistryThread>();
    public ArrayList<Node> nodesList = new ArrayList<Node>();

    private ServerSocket serverSocket;
    public int numConnections;
    public int numOfMessagesToSend;
    public boolean complete;
    public boolean done;

    public Registry(int port, int numConnections){
        try{
            serverSocket = new ServerSocket(port);
            this.numConnections = numConnections;
            this.complete = false;
            this.done = false;
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(!complete) {
                Socket incomingConnectionSocket = this.serverSocket.accept();
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " detected interruption, exiting...");
                    incomingConnectionSocket.close();
                    return;
                }
                
                incomingConnectionSocket.setReuseAddress(true);

                RegistryThread nodeSock = new RegistryThread(incomingConnectionSocket, this, numConnections);
                this.nodeThreads.add(nodeSock);
                new Thread(nodeSock).start();
                waitRegistry(); //We need to wait for the node to register before doing checks

                // Once all nodes are connected this will assign nodes to connect to.
                if(this.nodesList.size() == this.numConnections){
                    // Uses arraylist to assign a ring structure if first node is i = 0 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 9
                    // next rendition i = 1 : front = i + 1 mod 10 = 1 : back = i + 9 mod 10 = 0
                    for(int i = 0; i < this.numConnections; i++){
                        Integer messageType = 9;
                        Integer identifier = this.nodesList.get(i).identifier;
                        Integer frontPort = this.nodesList.get((i + 1) % this.numConnections).identifier;
                        String frontIp = this.nodesList.get((i + 1) % this.numConnections).ip;
                        Integer backPort = this.nodesList.get((i + this.numConnections-1) % this.numConnections).identifier;
                        String backIp = this.nodesList.get((i + this.numConnections-1) % this.numConnections).ip;
                        Message connDirective = new Message(frontIp, messageType, identifier, frontPort, backIp, backPort);
                        directives.add(connDirective);
                        
                    }
                    for(RegistryThread node: nodeThreads){
                        node.notifyRegThread();
                    }
                }
                
                this.complete = this.nodeThreads.size() == this.numConnections;
            }

            for (RegistryThread thread: this.nodeThreads){
                thread.join();
            }
        }
        catch (IOException | InterruptedException ioe) {
            System.out.print(ioe.getMessage());
        }
    }

    public synchronized void waitRegistry() throws InterruptedException{
        wait();
    }

    public synchronized void notifyRegistry(){
        notify();
    }

    public void setNumberOfMessages(Integer number) {
        this.numOfMessagesToSend = number;
        for(RegistryThread thread: this.nodeThreads){
            thread.setNumberOfMessages(number);
        }
    }

    public void startSequenceCompletion(){
        if(trafficSummaryMessages.size() == getNumConnections() && !done){
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
            done = true;
        }
    }

    public int getNumConnections() {
        return this.numConnections;
    }
}