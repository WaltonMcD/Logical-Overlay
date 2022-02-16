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

    @Override
    public void run(){
        try {
            int messageType = 0;
            int messageSize = 0;
            while(messageType != 3){
                messageType = this.nodeIn.readInt();
                messageSize = this.nodeIn.readInt();

                if(messageType == 0){
                    byte[] msg = new byte[messageSize];
                    nodeIn.readFully(msg, 0, messageSize);

                    RegisterMessageFormat marshalledMsg = new RegisterMessageFormat(msg);
                    marshalledMsg.printContents();
                }
                else if(messageType == 2){
                    byte[] payloadMsg = new byte[messageSize];
                    nodeIn.readFully(payloadMsg, 0, messageSize);

                    PayloadMessageFormat payloadMsgFormat = new PayloadMessageFormat(payloadMsg);
                    this.registry.updatePayloadTotal(payloadMsgFormat.payload);
                    //payloadMsgFormat.printContents();
                }
                else if(messageType == 3){
                    byte[] b = new byte[messageSize];
                    nodeIn.readFully(b, 0, messageSize);
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

}
