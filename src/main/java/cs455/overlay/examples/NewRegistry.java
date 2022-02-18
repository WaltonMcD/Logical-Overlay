package cs455.overlay.examples;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NewRegistry extends Thread{
    final private int serverPort;
    private int numberOfConnections;
    private int connectedNodes = 0;
    private boolean finish = false;
    private long totalPayload;
    private int totalMessages;
    private int numberOfMessagesToSend;

    private ArrayList<Thread> nodesList;
    private ArrayList<RegistryNodeThread> nodeThreads;

    public NewRegistry(Integer port, Integer numberOfConnections){
            this.serverPort = port;
            this.numberOfConnections = numberOfConnections;
            this.nodesList = new ArrayList<Thread>();
            this.nodeThreads = new ArrayList<RegistryNodeThread>();
    }

    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(this.serverPort, this.numberOfConnections);
            while(!this.finish){
                Socket incomingConnectionSocket = serverSocket.accept();
                incomingConnectionSocket.setReuseAddress(true);

                Thread regNodeThread = new RegistryNodeThread(incomingConnectionSocket, this);
                RegistryNodeThread node = new RegistryNodeThread(incomingConnectionSocket, this);
                nodesList.add(regNodeThread);
                nodeThreads.add(node);
                regNodeThread.start();

                this.connectedNodes++;
                this.finish = this.connectedNodes == this.numberOfConnections;
            }

            serverSocket.close();

            // wait for each thread to complete its execution by calling the join method on it. 
            for (Thread node: nodesList){
                node.join();
            }

            Thread.sleep(1000);
        
            System.out.println("Registry: Total count of messages sent: "+this.totalMessages +" Sum of all sent messages: " + this.totalPayload);
        }
        catch (IOException ioe) {
            System.out.println("Registry: ");
            ioe.printStackTrace();
        } 
        catch (InterruptedException e) {
            System.out.println("Registry: ");
            e.printStackTrace();
        }
        
    }

    public synchronized void updatePayloadTotal(long payload){
        this.totalPayload += payload;
        this.totalMessages++;
    }

    public void setNumberOfMessagesToSend(int numberOfMessagesToSend) {
        this.numberOfMessagesToSend = numberOfMessagesToSend;
    }

    public int getNumberOfMessagesToSend() {
        return this.numberOfMessagesToSend;
    }

    public ArrayList<RegistryNodeThread> getNodeThreads() {
        return this.nodeThreads;
    }

    public ArrayList<Thread> getNodesList(){
        return this.nodesList;
    }

    public int getNumberOfConnections() {
        return this.numberOfConnections;
    }
    
}