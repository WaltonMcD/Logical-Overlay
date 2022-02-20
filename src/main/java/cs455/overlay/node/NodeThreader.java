package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.protocols.Message;

public class NodeThreader extends Thread {
    public Integer numberOfMessages = 0;
    public String ip;
    public Integer port;
    public Integer serverPort;
    public Node node;
    public int toPort;
    public String toHost;
    public int numConnections;

    public Socket backSocket;

    public NodeThreader(String ip, Integer port, Integer serverPort, Node node, int toPort, String toHost, int numConnections){
        this.ip = ip;
        this.port = port;
        this.serverPort = serverPort;
        this.node = node;
        this.toPort = toPort;
        this.toHost = toHost;
        this.numConnections = numConnections;
    }

    public long getRandomNumberUsingNextLong() {
        Random random = new Random();
        long num = random.nextLong();
        return num;
    }

    @Override
    public void run(){
        
        try {
            Socket frontSocket = new Socket(ip, serverPort);
            System.out.println("Connected to node: " + frontSocket.getInetAddress());

            DataOutputStream toOut = new DataOutputStream( new BufferedOutputStream(frontSocket.getOutputStream()));
            ArrayList<Message> payloads = new ArrayList<Message>();

            //Waiting for task initiate.
            waitNodeThreader();

            int totalMessages = 0;
            for(int i = 0; i < numberOfMessages; i++){
                long payload = getRandomNumberUsingNextLong();
                Message dataTrafficMsg = new Message(5,i,i,payload,this.port, this.ip, this.toPort, this.toHost);
                dataTrafficMsg.packMessage(toOut);
                node.updateSentPayloadTotal(payload);
                totalMessages++;
            }
            node.numMessagesSent = totalMessages;

            DataInputStream nodeIn = new DataInputStream(new BufferedInputStream(this.backSocket.getInputStream()));
            

            Integer messagesReceived = 0;
            for(int z = 0; z < this.numConnections; z++ ){
                for(int i = 0; i < numberOfMessages; i++){
                    Message traffic = new Message();
                    traffic.unpackMessage(nodeIn);
                    payloads.add(traffic);
                    node.updateReceivedPayloadTotal(traffic.getPayload());
                    messagesReceived++;
                }
                if(numberOfMessages * numConnections == messagesReceived){
                    break;
                }
                for(int i = 0; i < payloads.size(); i++){
                    Message dataTrafficMsg = payloads.get(i);
                    dataTrafficMsg.packMessage(toOut);
                }
                payloads = new ArrayList<Message>();
            }
            
            node.numMessagesReceived = messagesReceived;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public synchronized void waitNodeThreader() throws InterruptedException {
        wait();
    }

    public synchronized void notifyNodeThreader(){
        notify();
    }

    public void setBackSocket(Socket backSocket) {
        this.backSocket = backSocket;
    }

    
}
