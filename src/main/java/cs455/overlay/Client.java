package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.lang.Math;

import cs455.overlay.TCPReciever;
import cs455.overlay.TCPSender;

public class Client{
    private Socket socketToServer;
    private Integer identifier;
    
    public Client(String address, Integer port) throws IOException {
        try{
            Random random = new Random();
            this.identifier = random.nextInt();

            this.socketToServer = new Socket(address, port);
            System.out.println("Connection Created With Client: " + identifier);

            TCPReciever inputStream = new TCPReciever(socketToServer);
            TCPSender outputStream = new TCPSender(socketToServer);

            inputStream.run();

            byte[] msgToServer = new String("Hello!").getBytes();
            outputStream.sendData(msgToServer);

            inputStream.close();
            outputStream.close();
            this.socketToServer.close();
        } catch (UnknownHostException un){
            System.out.println(un.getMessage());
        }
    }
}
