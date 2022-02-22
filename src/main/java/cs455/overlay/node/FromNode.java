package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.protocols.Message;

public class FromNode extends Thread{
    public Integer numberOfMessages = 0;
    public Node node;
    public int toPort;
    public String toHost;
    public int numConnections;

    public Socket fromSocket;
    public Socket toSocket;
    public ArrayList<Message> payloads;
    public ToNode toNode;
    public Buffer buffer;

    public FromNode(Node node, Socket fromSocket,ToNode toNode, int toPort, String toHost, Buffer buffer, int numConnections){
        this.node = node;
        this.fromSocket = fromSocket;
        this.toSocket = toNode.toSocket;
        this.payloads = new ArrayList<Message>();
        this.toNode = toNode;
        this.toHost = toHost;
        this.toPort = toPort;
        this.buffer = buffer;
        this.numConnections = numConnections;
    }

    @Override
    public void run(){
        try {
            DataInputStream nodeIn = new DataInputStream(new BufferedInputStream(this.fromSocket.getInputStream()));

            waitFromNode();
            
            Integer messagesReceived = 0;
            long payloadTotal = 0;
            for(int z = 0; z < numConnections; z++){
                for(int i = 0; i < numberOfMessages; i++){
                    Message msg = new Message();
                    msg.unpackMessage(nodeIn);
                    payloadTotal += msg.getPayload();
                    payloads.add(msg);
                    messagesReceived++;
                }
                ArrayList<Message> clone = new ArrayList<Message>(payloads);
                toNode.setPayloads(clone);
                
                
                toNode.notifyToNode();
                payloads = new ArrayList<Message>();
            }
            node.payloadReceivedTotal = payloadTotal;
            node.numMessagesReceived = messagesReceived;
            
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void waitFromNode() throws InterruptedException {
        wait();
    }

    public synchronized void notifyFromNode(){
        notify();
    }
}
