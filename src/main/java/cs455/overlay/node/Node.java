package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import cs455.overlay.protocols.Message;

public class Node extends Thread {
    public Integer identifier;
    public Socket socketToServer;
    private String ipAddress;
    private Integer port;
    private String ip;
    private Node frontNode;
    private Node backNode;

    public Node(String ipAddress, Integer port, Integer identifier){
        this.ip = ipAddress;
        this.port = port;
        this.identifier = identifier;
    }
    
    public Node(Socket socketToServer) {
        this.socketToServer = socketToServer;
        InetAddress ipAddress = socketToServer.getInetAddress();
        this.ip = ipAddress.getHostAddress();
        this.port = socketToServer.getLocalPort();
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
            
            outputStream.close();

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }   
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setBackNode(Node backNode) {
        this.backNode = backNode;
    }

    public void setFrontNode(Node frontNode) {
        this.frontNode = frontNode;
    }
}
