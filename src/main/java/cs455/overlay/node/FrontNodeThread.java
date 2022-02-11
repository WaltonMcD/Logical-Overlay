package cs455.overlay.node;

import java.io.*;
import java.net.*;

// Handles front node socket / message sending and receiving
public class FrontNodeThread {

    public FrontNodeThread(){
        // need default constructor to construct inner classes
    }
    
    public static class FrontNodeSender implements Runnable {
        public String ip;
        public Integer port;
        public Integer serverPort;

        public FrontNodeSender(String ip, Integer port, Integer serverPort){
            this.ip = ip;
            this.port = port;
            this.serverPort = serverPort;
        }

        @Override
        public void run(){
            try{
                Socket frontSocket = new Socket(ip, serverPort);
                System.out.println("Connected to front node: " + frontSocket.getInetAddress());

                DataOutputStream frontOutputStream = new DataOutputStream( new BufferedOutputStream(frontSocket.getOutputStream()));

                frontOutputStream.writeUTF("Hello from front node.");
                frontOutputStream.flush();
            }
            catch(UnknownHostException un){
                un.getMessage();
            }
            catch(IOException ioe){
                ioe.getMessage();
            }
        }
    }
}
