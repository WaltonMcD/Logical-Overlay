package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

    @Override
    public void run(){
        int messageType = 0;
        int messageSize = 0;

        try {
            
            // Register Request
            messageType = this.nodeIn.readInt();
            messageSize = this.nodeIn.readInt();    
            byte[] msg = new byte[messageSize];
            nodeIn.readFully(msg, 0, messageSize);

            RegisterMessageFormat marshalledMsg = new RegisterMessageFormat(msg);
            marshalledMsg.printContents();

            this.waitRegNodeThread();
               
            //Send Task initiate
            TaskInitiateFormat taskInitiate = new TaskInitiateFormat(this.ip, registry.getNumberOfMessagesToSend());
            byte[] marshalledMessage = taskInitiate.getBytes();
            nodeOut.writeInt(TaskInitiateFormat.type);
            nodeOut.writeInt(marshalledMessage.length);
            nodeOut.write(marshalledMessage);
            nodeOut.flush();


            // // Receive Payload
            // messageType = this.nodeIn.readInt();
            // messageSize = this.nodeIn.readInt();
            // byte[] payloadMsg = new byte[messageSize];
            // nodeIn.readFully(payloadMsg, 0, messageSize);

            // PayloadMessageFormat payloadMsgFormat = new PayloadMessageFormat(msg);
            // payloadMsgFormat.printContents();

            // this.registry.updatePayloadTotal(payloadMsgFormat.payload);
            

            // // Deregister
            // byte[] b = new byte[messageSize];
            // nodeIn.readFully(b, 0, messageSize);
            

          
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
