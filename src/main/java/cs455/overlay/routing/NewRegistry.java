package cs455.overlay.routing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.protocols.Message;

public class NewRegistry extends Thread{
    final private int serverPort;
    private int numberOfConnections;
    private ArrayList<RegistryNodeThread> nodesList;
    private boolean finish = false;
    private long totalPayload;
    private int totalMessages;
    private int numberOfMessagesToSend;

    public NewRegistry(Integer port, Integer numberOfConnections){
            this.serverPort = port;
            this.numberOfConnections = numberOfConnections;
            this.nodesList = new ArrayList<RegistryNodeThread>();
    }

    public void exitOverlay(){
        this.finish = true; 
    }

    public void openOverlay() throws InterruptedException, IOException{
        ServerSocket serverSocket = new ServerSocket(this.serverPort, this.numberOfConnections);
        while(!this.finish){
            Socket incomingConnectionSocket = serverSocket.accept();
            incomingConnectionSocket.setReuseAddress(true);

            RegistryNodeThread socketToNode = new RegistryNodeThread(incomingConnectionSocket, this);
            nodesList.add(socketToNode);
            new Thread(socketToNode).start();

        }
        serverSocket.close();
    }

    @Override
    public void run(){
        try {
            openOverlay();
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
        return numberOfMessagesToSend;
    }

    public ArrayList<RegistryNodeThread> getNodesList(){
        return this.nodesList;
    }

    public int getNumberOfConnections() {
        return this.numberOfConnections;
    }
    
}
