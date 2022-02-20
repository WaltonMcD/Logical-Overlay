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
    public String ip;
    public Integer port;
    public Integer serverPort;
    public Node node;
    public int toPort;
    public String toHost;
    public int numConnections;

    public Socket fromSocket;
    public Socket toSocket;
    public ArrayList<Message> payloads;
    public ToNode toNode;

    public FromNode(Node node, Socket fromSocket,ToNode toNode, int toPort, String toHost){
        this.node = node;
        this.fromSocket = fromSocket;
        this.toSocket = toNode.toSocket;
        this.payloads = new ArrayList<Message>();
        this.toNode = toNode;
        this.toHost = toHost;
        this.toPort = toPort;
    }

    @Override
    public void run(){
        try {
            DataInputStream nodeIn = new DataInputStream(new BufferedInputStream(this.fromSocket.getInputStream()));
            DataOutputStream toOut = new DataOutputStream( new BufferedOutputStream(toSocket.getOutputStream()));

            waitFromNode();
            
            Integer messagesReceived = 0;
            boolean done = false;
            while(!done){

                Message msg = new Message();
                msg.unpackMessage(nodeIn);
                
                if(msg.getMessageType() == 5){
                    node.updateReceivedPayloadTotal(msg.getPayload());
                    payloads.add(msg);
                    messagesReceived++;
                }
                else if(payloads.size() == numberOfMessages){
                    toNode.relayMessages(payloads);
                    Thread.sleep(25);
                }
                if(msg.getMessageType() == 1){
                    if(!msg.getIpAddress().equals(node.ip)){
                        toNode.forwardDereg(msg);
                    }
                    else{
                        done = true;
                        break;
                    }
                    
                }
                
            }
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
