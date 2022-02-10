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
import cs455.overlay.node.FrontNodeThread;
import cs455.overlay.node.BackNodeThread;

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
            DataOutputStream outputStream = new DataOutputStream( new BufferedOutputStream(socketToServer.getOutputStream()));
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socketToServer.getInputStream()));


            //Send Register Request
            Integer messageType = 0;
            Message registrationRequest = new Message(messageType, ip, port);
            outputStream.writeInt(registrationRequest.messageType);
            outputStream.writeUTF(registrationRequest.ipAddress); 
            outputStream.writeInt(registrationRequest.port); 
            outputStream.flush();

            
            //Receive Register Response
            messageType = inputStream.readInt();
            Integer statusCode = inputStream.readInt();
            Integer identifier = inputStream.readInt();
            this.identifier = identifier;
            String additionalInfo = inputStream.readUTF();

            Message registrationResponse = new Message(messageType, statusCode, identifier, additionalInfo);
            System.out.println(registrationResponse.getType() + " Received From Node: " + this.identifier + " Status Code: " + registrationResponse.statusCode + 
            "\nAdditional Info: " + registrationResponse.additionalInfo);
            

            //Receive Connection Directive
            messageType = inputStream.readInt();
            Integer frontPort = inputStream.readInt();
            String frontIP = inputStream.readUTF();
            Integer backPort = inputStream.readInt();
            String backIP = inputStream.readUTF();

            System.out.println("Connection Directive Front: " + frontPort + " " + frontIP + " Back: " + backPort + " " + backIP);

            Integer nodeServerPort = Registry.serverPort + 1;
            ServerSocket nodeServer = new ServerSocket((nodeServerPort), 2);

            FrontNodeThread frontNode = new FrontNodeThread(frontIP, frontPort, nodeServerPort);
            new Thread(frontNode).start();
            Socket frontSocket = nodeServer.accept();

            BackNodeThread backNode = new BackNodeThread(backIP, backPort, nodeServerPort);
            new Thread(backNode).start();
            Socket backSocket = nodeServer.accept();
    
            outputStream.close();
            inputStream.close();

        } 
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
