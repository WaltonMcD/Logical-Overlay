package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import cs455.overlay.Main;
import cs455.overlay.protocols.Message;
import cs455.overlay.wireformats.DoneMessageFormat;
import cs455.overlay.wireformats.RegisterMessageFormat;
import cs455.overlay.wireformats.TaskInitiateFormat;
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

    public void run() {
        Socket regSocket;
        try {
            regSocket = new Socket(this.hostIp, this.hostPort);
            DataOutputStream rout = new DataOutputStream(regSocket.getOutputStream());
            DataInputStream rin = new DataInputStream(regSocket.getInputStream());

            //Send Register Request
            RegisterMessageFormat registrationRequest = new RegisterMessageFormat(this.identifier, regSocket.getLocalPort());
            byte[] marshalledMsg = registrationRequest.getBytes();
            rout.writeInt(RegisterMessageFormat.type);
            rout.writeInt(marshalledMsg.length);
            rout.write(marshalledMsg);
            rout.flush();
            
            //Receive Task Initiate
            int messageType = rin.readInt();
            int messageSize = rin.readInt();
            byte[] taskMsg = new byte[messageSize];
            rin.readFully(taskMsg, 0, messageSize);
            
            TaskInitiateFormat taskMsgFormat = new TaskInitiateFormat(taskMsg);
            taskMsgFormat.printContents();

            // //Send Messages
            // Random random = new Random();
            // for (int i = 0; i < this.numOfMessagesToSend; i++) {
            //     long payload = random.nextLong();
            //     PayloadMessageFormat msg = new PayloadMessageFormat(i, i, payload, -1, this.identifier, this.hostPort, this.hostIp);
            //     marshalledMsg = msg.getBytes();
            //     rout.writeInt(PayloadMessageFormat.type);
            //     rout.writeInt(marshalledMsg.length);
            //     rout.write(marshalledMsg);
            //     rout.flush();

            //     this.totalSentMessages++;
            //     this.totalSentPayload += payload;
            // }

            // //Send DeRegistration
            // DoneMessageFormat msg = new DoneMessageFormat(this.identifier, -1);
            // marshalledMsg = msg.getBytes();
            // rout.writeInt(DoneMessageFormat.type);
            // rout.writeInt(marshalledMsg.length);
            // rout.write(marshalledMsg);
            // rout.flush();

            rout.close();
            regSocket.close();

        } 
        catch (UnknownHostException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setNumOfMessagesToSend(int numOfMessagesToSend) {
        this.numOfMessagesToSend = numOfMessagesToSend;
    }
}
