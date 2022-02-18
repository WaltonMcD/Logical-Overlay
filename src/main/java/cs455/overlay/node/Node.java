package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.Registry;
import cs455.overlay.node.NodeThread.BackNodeReader;
import cs455.overlay.node.NodeThread.FrontNodeSender;
import cs455.overlay.protocols.Message;

public class Node implements Runnable {
    public Integer identifier;
    public Socket  socketToServer;
    public Integer port;
    public String  ip;
    public Integer numMessagesSent;
    public Integer numMessagesReceived;
    public long payloadReceivedTotal;
    public long payloadSentTotal;
    
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

            Integer nodeServerPort = Registry.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 1);

            //Spawns a thread to connect to front nodes server socket
            FrontNodeSender frontNode = new FrontNodeSender(recvConnDirMsg.getFrontNodeIp(), recvConnDirMsg.getFrontNodePort(), nodeServerPort, this, recvConnDirMsg.getBackNodePort(), recvConnDirMsg.getBackNodeIp());
            Thread frontNodeThread = new Thread(frontNode);
            frontNodeThread.start();
            
            //Accepts back nodes connection.
            Socket backSocket = nodeServer.accept();
            BackNodeReader backNodeReader = new BackNodeReader(backSocket, this);
            Thread backNodeReaderThread = new Thread(backNodeReader);
            backNodeReaderThread.start();
            
            //Receive Task Initiate
            Message taskInitiateMsg = new Message();
            taskInitiateMsg.unpackMessage(serverInputStream);
            NodeThread.numberOfMessages = taskInitiateMsg.getMessagesToSend();

            //Notify worker threads to start message passing.
            frontNode.notifyNodeSender();
            backNodeReader.notifyNodeReader();

            waitNode(); //Wait for message sending to complete.

            // Send Task Complete to registry
            Message taskCompleteMsg = new Message(6,identifier, ip, port);
            taskCompleteMsg.packMessage(serverOutputStream);

            //Receive Traffic Summary Request.
            Message trafficSummaryReqMsg = new Message();
            trafficSummaryReqMsg.unpackMessage(serverInputStream);
            System.out.println(trafficSummaryReqMsg.getType());

            while(numMessagesSent == null || payloadSentTotal ==  0 || numMessagesReceived == null || payloadReceivedTotal == 0){
                if(numMessagesSent != null && payloadSentTotal !=  0 && numMessagesReceived != null && payloadReceivedTotal != 0){
                    break;
                }
            }

            //Send Traffic Summary
            Message trafficSummary = new Message(8, ip, port, numMessagesSent, payloadSentTotal, numMessagesReceived, payloadReceivedTotal);
            trafficSummary.packMessage(serverOutputStream);

            serverOutputStream.close();
            serverInputStream.close();
            
        } 
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public synchronized void updatePayloadTotal(long payload){
        this.payloadReceivedTotal += payload;
    }
    
}
