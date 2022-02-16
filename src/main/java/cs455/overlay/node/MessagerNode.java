package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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

    public MessagerNode(String hostIp, int hostPort, String identifier, int numberOfMessages) throws IOException {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.identifier = identifier;
        this.numOfMessagesToSend = numberOfMessages;
    }

    public void sendRegisterRequest(DataOutputStream rout, int port) throws IOException{
        RegisterMessageFormat registrationRequest = new RegisterMessageFormat(this.identifier, port);
        byte[] marshalledMsg = registrationRequest.getBytes();
        rout.writeInt(RegisterMessageFormat.type);
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
        rout.writeInt(DoneMessageFormat.type);
        rout.writeInt(marshalledMsg.length);
        rout.write(marshalledMsg);
        rout.flush();
    }
    
    @Override
    public void run() {
        Socket regSocket;
        try {
            regSocket = new Socket(this.hostIp, this.hostPort);
            DataOutputStream rout = new DataOutputStream(regSocket.getOutputStream());
            DataInputStream rin = new DataInputStream(new BufferedInputStream(regSocket.getInputStream()));

            sendRegisterRequest(rout, regSocket.getLocalPort());
            
            sendPayload(rout, regSocket.getLocalPort());

            sendDeregistrationRequest(rout, regSocket.getLocalPort());

            rout.close();
            regSocket.close();

            System.out.printf("Node: "+ this.identifier +" sent "+ this.totalSentMessages +" messages, summing to: "+ this.totalSentPayload);

        } 
        catch (IOException e) {
            e.printStackTrace();
        } 

    }
}
