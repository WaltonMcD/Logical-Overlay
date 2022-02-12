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
            registrationRequest.packMessage(serverOutputStream);

            //Receive Register Response
            Message registrationResponse = new Message();
            registrationResponse.unpackMessage(serverInputStream);
            this.identifier = registrationResponse.getIdentifier();

            System.out.println(registrationResponse.getType() + " Received From Node: " + this.identifier + 
            					" Status Code: " + registrationResponse.getStatusCode() + 
            					"\nAdditional Info: " + registrationResponse.getAdditionalInfo());
            
            //Receive Connection Directive
            Message recvConnDirMsg = new Message();
            recvConnDirMsg.unpackMessage(serverInputStream);

            System.out.println("Connection Directive Front: " + recvConnDirMsg.getFrontNodePort() + " " + 
            					recvConnDirMsg.getFrontNodeIp() + " Back: " + recvConnDirMsg.getBackNodePort() + " " + 
            					recvConnDirMsg.getBackNodeIp());

            //Start server socket for neighbor nodes to connect to.
            Integer nodeServerPort = Registry.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 1);

            //Start thread to connect to front nodes server socket.
            FrontNodeSender frontNode = new FrontNodeSender(recvConnDirMsg.getFrontNodeIp(), recvConnDirMsg.getFrontNodePort(), nodeServerPort, this);
            new Thread(frontNode).start();
            
            //Accept back nodes connection.
            Socket backSocket = nodeServer.accept();
            BackNodeReader backNodeReader = new BackNodeReader(backSocket, this);
            new Thread(backNodeReader).start();
            
            //Receive Task Initiate
            Message recvTaskInitMsg = new Message();
            NodeThread.numberOfMessages = recvTaskInitMsg.getMessagesToSend();

            //Notify worker threads of task initiate.
            frontNode.notifyNodeSender();
            backNodeReader.notifyNodeReader();

            // Send Task Complete to registry
            Message taskCompleteMsg = new Message(6, identifier, ip, port);
            taskCompleteMsg.packMessage(serverOutputStream);
    
            serverOutputStream.close();
            serverInputStream.close();

        } 
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
}
