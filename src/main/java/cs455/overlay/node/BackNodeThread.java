package cs455.overlay.node;

import java.io.*;
import java.net.*;

public class BackNodeThread implements Runnable {
    public String ip;
    public Integer port;
    public Integer serverPort;
    
    public BackNodeThread(String ip, Integer port, Integer serverPort){
        this.ip = ip;
        this.port = port;
        this.serverPort = serverPort;
    }

    @Override
    public void run(){
        try{
            Socket backSocket = new Socket(ip, serverPort);

            System.out.println("Connected to back node: " + backSocket.getInetAddress());
        }
        catch(UnknownHostException un){
            un.getMessage();
        }
        catch(IOException ioe){
            ioe.getMessage();
        }
    }
}
