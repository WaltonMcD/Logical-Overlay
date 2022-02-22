package cs455.overlay.node;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.protocols.Message;

public class ToNode extends Thread{
    public Integer numberOfMessages = 0;
    public String ip;
    public Integer port;
    public Integer serverPort;
    public Node node;
    public int toPort;
    public String toHost;
    public int numConnections;

    public Socket toSocket;
    public DataOutputStream toOut;
    public FromNode fromNode;
    public Buffer buffer;
    public int count;

    public ToNode(String ip, Integer port, Integer serverPort, Node node, int toPort, String toHost, int numConnections, Buffer buffer) throws UnknownHostException, IOException, InterruptedException{
        this.ip = ip;
        this.port = port;
        this.serverPort = serverPort;
        this.node = node;
        this.toPort = toPort;
        this.toHost = toHost;
        this.numConnections = numConnections;
        this.buffer = buffer;
        Thread.sleep(50);
        this.toSocket = new Socket(ip, serverPort);
        System.out.println("Connected to node: " + toSocket.getInetAddress());
    }

    public long getRandomNumberUsingNextLong() {
        Random random = new Random();
        long num = random.nextLong();
        return num;
    }

    public void relayMessages() throws InterruptedException{
        synchronized(buffer){
            while(!buffer.isEmpty()){
                Message payload = buffer.remove();
                payload.packMessage(toOut);
    
            }
        }

    }

    @Override
    public void run(){
        try {
            this.toOut = new DataOutputStream( new BufferedOutputStream(toSocket.getOutputStream()));

            waitToNode();

            int totalMessages = 0;
            for(int i = 0; i < numberOfMessages; i++){
                long payload = getRandomNumberUsingNextLong();
                Message dataTrafficMsg = new Message(5, i, i, payload, node.port, node.ip, this.toPort, this.toHost);
                dataTrafficMsg.packMessage(toOut);
                node.updateSentPayloadTotal(payload);
                totalMessages++;
            }
            node.numMessagesSent = totalMessages;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void waitToNode() throws InterruptedException {
        wait();
    }

    public synchronized void notifyToNode(){
        notify();
    }

    public void setFromNode(FromNode fromNode) {
        this.fromNode = fromNode;
    }
}
