package cs455.overlay.node;

import java.io.*;
import java.net.*;

// Handles front node socket / message sending and receiving
public class FrontNodeThread implements Runnable{
    public String ip;
    public Integer port;
    public Socket socket;

    public FrontNodeThread(String ip, Integer port){
        this.ip = ip;
        this.port = port;
        this.socket = new Socket();
    }

    @Override
    public void run(){
        try{
            Socket socket = new Socket();
            InetAddress inetAddress = InetAddress.getByName(ip);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, port); 
            socket.connect(socketAddress);
        }
        catch(UnknownHostException un){
            un.getMessage();
        }
        catch(IOException ioe){
            ioe.getMessage();
        }
    }
}
