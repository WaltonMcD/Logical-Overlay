package cs455.overlay.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.Random;

import cs455.overlay.wireformats.DoneMessageFormat;
import cs455.overlay.wireformats.RegisterMessageFormat;
import cs455.overlay.wireformats.PayloadMessageFormat;

public class MessagerNode extends Thread{
    private String identifier;
    private final int  hostPort;
    public final String hostIp;
    
    private int numOfMessagesToSend;
    private long totalSentMessages;
    private long totalSentPayload;

    public MessagerNode(String hostIp, int hostPort, String identifier) throws IOException {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.identifier = identifier;
    }

    public void ReceiveTaskInitiate(DataInputStream rin) throws IOException{
        int messageType = rin.readInt();
        int numberOfMessages = rin.readInt();
        setNumOfMessagesToSend(numberOfMessages);
    }

    public void sendRegisterRequest(DataOutputStream rout, int port) throws IOException{
        RegisterMessageFormat registrationRequest = new RegisterMessageFormat(this.identifier, port);
        byte[] marshalledMsg = registrationRequest.getBytes();
        rout.writeInt(registrationRequest.type);
        rout.writeInt(marshalledMsg.length);
        rout.write(marshalledMsg);
        rout.flush();
    }

    public void sendPayload(DataOutputStream rout, int fromPort) throws IOException{
        Random random = new Random();
        for (int i = 0; i < this.numOfMessagesToSend; i++) {
            long payload = random.nextLong();
            PayloadMessageFormat msg = new PayloadMessageFormat(i, i, payload, fromPort, this.identifier, this.hostPort, this.hostIp);
            byte[] marshalledMsg = msg.getBytes();
            rout.writeInt(PayloadMessageFormat.type);
            rout.writeInt(marshalledMsg.length);
            rout.write(marshalledMsg);
            rout.flush();

            this.totalSentMessages++;
            this.totalSentPayload += payload;
        }
    }

    public void sendDeregistrationRequest(DataOutputStream rout, int fromPort) throws IOException{
        DoneMessageFormat msg = new DoneMessageFormat(this.identifier, fromPort);
        byte[] marshalledMsg = msg.getBytes();
        rout.writeInt(msg.type);
        rout.writeInt(marshalledMsg.length);
        rout.write(marshalledMsg);
        rout.flush();
    }
    
    @Override
    public void run() {
        Socket regSocket;
        try {
            regSocket = new Socket(this.hostIp, this.hostPort);
            DataOutputStream rout = new DataOutputStream(new BufferedOutputStream(regSocket.getOutputStream()));
            DataInputStream rin = new DataInputStream(new BufferedInputStream(regSocket.getInputStream()));

            sendRegisterRequest(rout, regSocket.getLocalPort());

            ReceiveTaskInitiate(rin);
            
            sendPayload(rout, regSocket.getLocalPort());

            sendDeregistrationRequest(rout, regSocket.getLocalPort());

            rout.close();
            regSocket.close();

            System.out.println("Node: "+ this.identifier +" sent "+ this.totalSentMessages +" messages, summing to: "+ this.totalSentPayload);

        } 
        catch (IOException e) {
            e.printStackTrace();
        } 

    }

    public void setNumOfMessagesToSend(int numOfMessagesToSend) {
        this.numOfMessagesToSend = numOfMessagesToSend;
    }
}
