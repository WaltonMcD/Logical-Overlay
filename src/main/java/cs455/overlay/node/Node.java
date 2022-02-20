package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.Main;
import cs455.overlay.protocols.Message;

public class Node implements Runnable {
    public Integer identifier;
    public Socket  socketToServer;
    public Integer port;
    public String  ip;
    public Integer numMessagesSent;
    public Integer numMessagesReceived;
    public long payloadReceivedTotal = 0;
    public long payloadSentTotal = 0;

    
    public Node(String ipAddress, Integer port, Integer identifier){
        this.ip = ipAddress;
        this.port = port;
        this.identifier = identifier;
    }
    
    public Node(Socket socketToServer) {
        this.socketToServer = socketToServer;
        InetAddress ipAddress = socketToServer.getLocalAddress();
        this.ip = ipAddress.getHostName();
        this.port = socketToServer.getLocalPort();
        this.payloadReceivedTotal = 0;
        this.payloadSentTotal = 0;
    }

    public synchronized void waitNode(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyNode(){
        notify();
    }

    public void run() {
    	
        try {
            DataOutputStream serverOutputStream = new DataOutputStream( new BufferedOutputStream(socketToServer.getOutputStream()));
            DataInputStream serverInputStream = new DataInputStream(new BufferedInputStream(socketToServer.getInputStream()));
            
            //Send Register Request
            Integer messageType = 0;
            Message registrationRequestMsg = new Message(messageType, ip, port);
            registrationRequestMsg.packMessage(serverOutputStream);

            //Receive Register Response
            Message registrationResponseMsg = new Message();
            registrationResponseMsg.unpackMessage(serverInputStream);
            this.identifier = registrationResponseMsg.getIdentifier();
            
            //Receive Connection Directive
            Message recvConnDirMsg = new Message();
            recvConnDirMsg.unpackMessage(serverInputStream);
            
            //Create front node's server socket
            Integer nodeServerPort = Main.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket(nodeServerPort, 1);

            //Spawns a thread to connect to front nodes server socket
            ToNode node = new ToNode(recvConnDirMsg.getFrontNodeIp(), recvConnDirMsg.getFrontNodePort(), nodeServerPort, this, recvConnDirMsg.getBackNodePort(), recvConnDirMsg.getBackNodeIp(), recvConnDirMsg.getNumConnections());
            Thread nodeThread = new Thread(node);
            nodeThread.start();
            
            //Accepts back nodes connection.
            Socket fromSocket = nodeServer.accept();
            FromNode fromNode = new FromNode(this, fromSocket, node, this.port, this.ip);
            Thread fromThread = new Thread(fromNode);
            fromThread.start();
            
            //Receive Task Initiate
            Message taskInitiateMsg = new Message();
            taskInitiateMsg.unpackMessage(serverInputStream);
            node.numberOfMessages = taskInitiateMsg.getMessagesToSend();
            fromNode.numberOfMessages = taskInitiateMsg.getMessagesToSend();

            node.setFromNode(fromNode);

            //Notify worker threads to start message passing.
            fromNode.notifyFromNode();
            node.notifyToNode();
            
            //Wait for thread completion
            fromThread.join();
            nodeThread.join();
            nodeServer.close(); 

            // Send Task Complete to registry
            Message taskCompleteMsg = new Message(6,identifier, ip, port);
            taskCompleteMsg.packMessage(serverOutputStream);

            //Receive Traffic Summary Request.
            Message trafficSummaryReqMsg = new Message();
            trafficSummaryReqMsg.unpackMessage(serverInputStream);

            //Send Traffic Summary
            Message trafficSummary = new Message(8, ip, port, numMessagesSent, payloadSentTotal, numMessagesReceived, payloadReceivedTotal);
            trafficSummary.packMessage(serverOutputStream);

            Message deregistration = new Message(1, this.ip, this.port);
            deregistration.packMessage(serverOutputStream);

            nodeServer.close();
            serverOutputStream.close();
            serverInputStream.close();
            this.socketToServer.close();
            
        } 
        catch (IOException | InterruptedException ioe) {
            System.out.println(ioe.getMessage());
        }

    }

    public synchronized void updateReceivedPayloadTotal(long payload){
        this.payloadReceivedTotal += payload;
    }

    public synchronized void updateSentPayloadTotal(long payload){
        this.payloadSentTotal += payload;
    }
    
}
