package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.lang.Math;

import cs455.overlay.TCPReciever;
import cs455.overlay.TCPSender;

public class Client{
    private Socket socketToServer;
    private Integer identifier;
    private Scanner input;
    
    public Client(String address, Integer port) throws IOException {
        try{
            Random random = new Random();
            this.identifier = port;

            this.input = new Scanner(System.in);
            this.socketToServer = new Socket(address, port);
            System.out.println("Connection Created With Client: " + identifier);

            TCPSender outputStream = new TCPSender(socketToServer);

            String line = "";
            while(!line.equals("Exit")){
                line = this.input.nextLine();
                outputStream.sendData(line.getBytes());
            }

            outputStream.close();
            this.socketToServer.close();
        } catch (UnknownHostException un){
            System.out.println(un.getMessage());
        }
    }
}
