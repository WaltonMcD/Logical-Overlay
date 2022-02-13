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
    public Socket socketToServer;
    public Integer port;
    public String ip;
    public Integer payloadReceivedTotal;
    public Integer payloadSentTotal;
    
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
            Message registrationRequest = new Message(messageType, ip, port);
            serverOutputStream.writeInt(registrationRequest.messageType);
            serverOutputStream.writeUTF(registrationRequest.ipAddress);
            serverOutputStream.writeInt(registrationRequest.port);
            serverOutputStream.flush();

            
            //Receive Register Response
            messageType = serverInputStream.readInt();
            Integer statusCode = serverInputStream.readInt();
            Integer identifier = serverInputStream.readInt();
            String additionalInfo = serverInputStream.readUTF();
            this.identifier = identifier;

            Message registrationResponse = new Message(messageType, statusCode, identifier, additionalInfo);
            System.out.println(registrationResponse.getType() + " Received From Node: " + this.identifier + " Status Code: " + registrationResponse.statusCode + 
            "\nAdditional Info: " + registrationResponse.additionalInfo);
            
            //Receive Connection Directive
            messageType = serverInputStream.readInt();
            Integer frontPort = serverInputStream.readInt();
            String frontIP = serverInputStream.readUTF();
            Integer backPort = serverInputStream.readInt();
            String backIP = serverInputStream.readUTF();

            System.out.println("Connection Directive Front: " + frontPort + " " + frontIP + " Back: " + backPort + " " + backIP);

            Integer nodeServerPort = Registry.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 1);

            //Spawns a thread to connect to front nodes server socket
            FrontNodeSender frontNode = new FrontNodeSender(frontIP, frontPort, nodeServerPort, this);
            new Thread(frontNode).start();
            
            //Accepts back nodes connection.
            Socket backSocket = nodeServer.accept();
            BackNodeReader backNodeReader = new BackNodeReader(backSocket, this);
            new Thread(backNodeReader).start();
            
            //Receive Task Initiate
            //The read call will block until start sequence is initiated.
            messageType = serverInputStream.readInt();
            Integer numberOfMessages = serverInputStream.readInt();
            Message taskInitiate = new Message(messageType, numberOfMessages);
            NodeThread.numberOfMessages = numberOfMessages;

            System.out.println("Received Task Initiate Messages to send: " + numberOfMessages);

            //Notify worker threads to start message passing.
            frontNode.notifyNodeSender();
            backNodeReader.notifyNodeReader();

            waitNode(); //Wait for message sending to complete.

            // Send Task Complete to registry
            Message taskComplete = new Message(6,identifier, ip, port);
            serverOutputStream.writeInt(taskComplete.messageType);
            serverOutputStream.writeInt(taskComplete.identifier);
            serverOutputStream.writeUTF(taskComplete.ipAddress);
            serverOutputStream.writeInt(taskComplete.port);
            serverOutputStream.flush();

            //Receive Traffic Summary Request.
            Message trafficSummaryReq = new Message(serverInputStream.readInt());
            System.out.println(trafficSummaryReq.getType());

            
            serverOutputStream.close();
            serverInputStream.close();
            
        } 
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
}
