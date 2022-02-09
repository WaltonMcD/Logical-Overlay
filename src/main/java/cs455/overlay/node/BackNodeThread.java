package cs455.overlay.node;

import java.io.*;
import java.net.*;

public class BackNodeThread implements Runnable {
    public String ip;
    public Integer port;
    public Socket socket;
    
    public BackNodeThread(String ip, Integer port){
        this.ip = ip;
        this.port = port;
        this.socket = new Socket();
    }

    @Override
    public void run(){
        try{
            
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
