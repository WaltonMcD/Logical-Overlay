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
    public Integer payloadReceivedTotal;
    public Integer payloadSentTotal;
    public Integer numMessagesSent;
    public Integer numMessagesReceived;
    
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
            
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " detected interruption, exiting Node 1-1...");
                serverOutputStream.close();
                serverInputStream.close();
                socketToServer.close();
                return;
            }
            
            registrationRequestMsg.packMessage(serverOutputStream);

            //Receive Register Response
            Message registrationResponseMsg = new Message();
            
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " detected interruption, exiting Node 1-2...");
                serverOutputStream.close();
                serverInputStream.close();
                socketToServer.close();
                return;
            }
            
            registrationResponseMsg.unpackMessage(serverInputStream);
            this.identifier = registrationResponseMsg.getIdentifier();

            System.out.println(registrationResponseMsg.getType() + " Received From Node: " + this.identifier + " Status Code: " + 
            				   registrationResponseMsg.getStatusCode() + "\nAdditional Info: " + 
            				   registrationResponseMsg.getAdditionalInfo());
            
            
            
            //Receive Connection Directive
            Message recvConnDirMsg = new Message();
            
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " detected interruption, exiting Node 1-3...");
                serverOutputStream.close();
                serverInputStream.close();
                socketToServer.close();
                return;
            }
            
            recvConnDirMsg.unpackMessage(serverInputStream);
            
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " detected interruption, exiting Node 1-4...");
                serverOutputStream.close();
                serverInputStream.close();
                socketToServer.close();
                return;
            }
          
            System.out.println("Connection Directive Front: " + recvConnDirMsg.getFrontNodePort() + " " + recvConnDirMsg.getFrontNodeIp() + 
            				   " Back: " + recvConnDirMsg.getBackNodePort() + " " + recvConnDirMsg.getBackNodeIp());

            Integer nodeServerPort = Registry.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 1);

            //Spawns a thread to connect to front nodes server socket
            FrontNodeSender frontNode = new FrontNodeSender(recvConnDirMsg.getFrontNodeIp(), recvConnDirMsg.getFrontNodePort(), nodeServerPort, this);
            Thread frontNodeThread = new Thread(frontNode);
            frontNodeThread.start();
            
            //Accepts back nodes connection.
            Socket backSocket = nodeServer.accept();
            BackNodeReader backNodeReader = new BackNodeReader(backSocket, this);
            Thread backNodeReaderThread = new Thread(backNodeReader);
            backNodeReaderThread.start();
            
            
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " detected interruption, exiting Node 1-5...");
                backNodeReaderThread.interrupt();
                frontNodeThread.interrupt();
                serverOutputStream.close();
                serverInputStream.close();
                backSocket.close();
                nodeServer.close();
                socketToServer.close();
                return;
            }
            
            //Receive Task Initiate
            //The read call will block until start sequence is initiated.
            Message taskInitiateMsg = new Message();
            taskInitiateMsg.unpackMessage(serverInputStream);
            NodeThread.numberOfMessages = taskInitiateMsg.getMessagesToSend();

            System.out.println("Received Task Initiate Messages to send: " + taskInitiateMsg.getMessagesToSend());

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
    
}
