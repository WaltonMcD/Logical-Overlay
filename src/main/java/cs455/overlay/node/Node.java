package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import cs455.overlay.Registry;
import cs455.overlay.protocols.Message;
import cs455.overlay.node.FrontNodeThread.FrontNodeReceiver;
import cs455.overlay.node.FrontNodeThread.FrontNodeSender;
import cs455.overlay.node.BackNodeThread.BackNodeReceiver;
import cs455.overlay.node.BackNodeThread.BackNodeSender;


public class Node implements Runnable {
    public Integer identifier;
    public Socket socketToServer;
    public Integer port;
    public String ip;

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
            this.identifier = identifier;
            String additionalInfo = serverInputStream.readUTF();

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
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 2);

            FrontNodeSender frontNode = new FrontNodeSender(frontIP, frontPort, nodeServerPort);
            new Thread(frontNode).start();
            
            BackNodeSender backNode = new BackNodeSender(backIP, backPort, nodeServerPort);
            new Thread(backNode).start();
            
            Socket frontSocket = nodeServer.accept();
            Socket backSocket = nodeServer.accept();

            FrontNodeReceiver frontNodeReceiver = new FrontNodeReceiver(frontSocket);
            BackNodeReceiver backNodeReceiver = new BackNodeReceiver(backSocket);
            new Thread(frontNodeReceiver).start();
            new Thread(backNodeReceiver).start();

            //Receive Task Initiate
            messageType = serverInputStream.readInt();
            Integer numberOfMessages = serverInputStream.readInt();
            Message taskInitiate = new Message(messageType, numberOfMessages);

            System.out.println("Starting task to send " + taskInitiate.messagesToSend + " messages");
    
            serverOutputStream.close();
            serverInputStream.close();

        } 
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
