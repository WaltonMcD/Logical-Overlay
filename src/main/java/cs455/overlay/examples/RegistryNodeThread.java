package cs455.overlay.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.wireformats.RegisterMessageFormat;
import cs455.overlay.wireformats.DoneMessageFormat;
import cs455.overlay.wireformats.PayloadMessageFormat;

public class RegistryNodeThread extends Thread {
    private final Socket nodeSocket;
    private NewRegistry registry;
    private final DataOutputStream nodeOut;
    private final DataInputStream nodeIn;
    public final String ip;
    public final int port;
    
    public RegistryNodeThread(Socket nodeSocket, NewRegistry registry) throws IOException {
        this.nodeSocket = nodeSocket;
        this.ip = nodeSocket.getInetAddress().getHostName();
        this.port = nodeSocket.getPort();
        this.registry = registry;
        this.nodeOut = new DataOutputStream( new BufferedOutputStream(nodeSocket.getOutputStream()));
        this.nodeIn = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
    }

    public void sendTaskInitiate() throws IOException{
        nodeOut.writeInt(1);
        nodeOut.writeInt(registry.getNumberOfMessagesToSend());
        nodeOut.flush();
        
    }

    public void readRegistrationRequest(int messageSize) throws IOException{
        byte[] reqMsg = new byte[messageSize];
        nodeIn.readFully(reqMsg, 0, messageSize);

        RegisterMessageFormat marshalledMsg = new RegisterMessageFormat(reqMsg);
        marshalledMsg.printContents();
    }

    public void readPayload(int messageSize) throws IOException{
        byte[] payloadMsg = new byte[messageSize];
        nodeIn.readFully(payloadMsg, 0, messageSize);

        PayloadMessageFormat payloadMsgFormat = new PayloadMessageFormat(payloadMsg);
        this.registry.updatePayloadTotal(payloadMsgFormat.payload);
        payloadMsgFormat.printContents();
    }

    public void readDeregistrationRequest(int messageSize) throws IOException{
        byte[] deReq = new byte[messageSize];
        nodeIn.readFully(deReq, 0, messageSize);

        DoneMessageFormat deregistrationReq = new DoneMessageFormat(deReq);
        deregistrationReq.printContents();
    }

    @Override
    public void run(){
        try {
            int messageType = -1;
            int messageSize = -1;

            while(messageType != 3){
                messageType = this.nodeIn.readInt();
                messageSize = this.nodeIn.readInt();

                if(messageType == 0){
                    readRegistrationRequest(messageSize);

                    //waitRegistryNodeThread();

                    sendTaskInitiate();
                }
                else if(messageType == 2){
                    readPayload(messageSize);
                }
                else if(messageType == 3){
                    readDeregistrationRequest(messageSize);
                }
            }
            
            nodeOut.close();
            nodeIn.close();
            nodeSocket.close();
        }
        catch(IOException ioe){
            System.out.println("Node: ");
            ioe.printStackTrace();
        } 
    }

    public synchronized void waitRegistryNodeThread(){
        try {
            this.wait();
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyRegistryNodeThread(){
        this.notify();
    }
}
