package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.sound.midi.Receiver;

import cs455.overlay.Main;
import cs455.overlay.protocols.Message;
import cs455.overlay.wireformats.RegisterMessageFormat;
import cs455.overlay.wireformats.TaskInitiateFormat;
import cs455.overlay.wireformats.PayloadMessageFormat;

public class RegistryNodeThread extends Thread {
    private final Socket nodeSocket;
    private NewRegistry registry;
    private final DataOutputStream nodeOut;
    private final DataInputStream nodeIn;
    private final String ip;
    private final int port;
    
    public RegistryNodeThread(Socket nodeSocket, NewRegistry registry) throws IOException {
        this.nodeSocket = nodeSocket;
        this.ip = nodeSocket.getInetAddress().getHostName();
        this.port = nodeSocket.getPort();
        this.registry = registry;
        this.nodeOut = new DataOutputStream( new BufferedOutputStream(nodeSocket.getOutputStream()));
        this.nodeIn = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
    }

    public void receiveRegistration(int messageType, int messageSize) throws IOException{
        // Register Request   
        byte[] msg = new byte[messageSize];
        nodeIn.readFully(msg, 0, messageSize);

        RegisterMessageFormat marshalledMsg = new RegisterMessageFormat(msg);
        marshalledMsg.printContents();
    }

    public void sendTaskInitiate(String hostname) throws IOException{
        //Send Task initiate
        TaskInitiateFormat taskInitiate = new TaskInitiateFormat(hostname, registry.getNumberOfMessagesToSend());
        byte[] marshalledMessage = taskInitiate.getBytes();
        nodeOut.writeInt(TaskInitiateFormat.type);
        nodeOut.writeInt(marshalledMessage.length);
        nodeOut.write(marshalledMessage);
        nodeOut.flush();
    }

    public void receivePayload(int messageType, int messageSize) throws IOException{
        // Receive Payload
        byte[] payloadMsg = new byte[messageSize];
        nodeIn.readFully(payloadMsg, 0, messageSize);

        PayloadMessageFormat payloadMsgFormat = new PayloadMessageFormat(payloadMsg);
        payloadMsgFormat.printContents();

        this.registry.updatePayloadTotal(payloadMsgFormat.payload);
    }

    public void deregisterRequest(int messageType, int messageSize) throws IOException{
        byte[] b = new byte[messageSize];
        nodeIn.readFully(b, 0, messageSize);
    }

    @Override
    public void run(){
        try {
            int messageType = this.nodeIn.readInt();
            int messageSize = this.nodeIn.readInt(); 
            
            if(messageType == 0){
                receiveRegistration(messageType, messageSize);

                //Waits for start command to designate number of nodes to send.
                this.waitRegNodeThread(); 
                sendTaskInitiate(this.nodeSocket.getLocalAddress().getHostName());
            }
            else if(messageType == 2){
                receivePayload(messageType, messageSize);
            }
            else if(messageType == 3){
                deregisterRequest(messageType, messageSize);
            }
            
            nodeOut.close();
            nodeIn.close();
            nodeSocket.close();
        }
        catch(IOException | InterruptedException ioe){
            System.out.println("Node: ");
            ioe.printStackTrace();
        }
    }

    public synchronized void waitRegNodeThread() throws InterruptedException{
        this.wait();
    }

    public synchronized void notifyRegNodeThread(){
        this.notify();
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

}
