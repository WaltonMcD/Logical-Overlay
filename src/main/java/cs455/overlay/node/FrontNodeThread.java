package cs455.overlay.node;

import java.io.*;
import java.net.*;

// Handles front node socket / message sending and receiving
public class FrontNodeThread implements Runnable{
    public String ip;
    public Integer port;
    public Integer serverPort;

    public FrontNodeThread(String ip, Integer port, Integer serverPort){
        this.ip = ip;
        this.port = port;
        this.serverPort = serverPort;
    }

    @Override
    public void run(){
        try{
            Socket frontSocket = new Socket(ip, serverPort);

            System.out.println("Connected to front node: " + frontSocket.getInetAddress());
        }
        catch(UnknownHostException un){
            un.getMessage();
        }
        catch(IOException ioe){
            ioe.getMessage();
        }
    }
}
