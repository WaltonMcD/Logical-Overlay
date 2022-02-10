package cs455.overlay.node;

import java.io.*;
import java.net.*;

public class BackNodeThread {

    public BackNodeThread(){
        // need default constructor to construct inner classes
    }
    
    public static class BackNodeSender implements Runnable {
        public String ip;
        public Integer port;
        public Integer serverPort;
        
        public BackNodeSender(String ip, Integer port, Integer serverPort){
            this.ip = ip;
            this.port = port;
            this.serverPort = serverPort;
        }

        @Override
        public void run(){
            try{
                Socket backSocket = new Socket(ip, serverPort);
                System.out.println("Connected to back node: " + backSocket.getInetAddress());

                DataOutputStream backOutputStream = new DataOutputStream( new BufferedOutputStream(backSocket.getOutputStream()));

                backOutputStream.writeUTF("Hello back node.");
                backOutputStream.flush();
            }
            catch(UnknownHostException un){
                un.getMessage();
            }
            catch(IOException ioe){
                ioe.getMessage();
            }
        }
    }

    public static class BackNodeReceiver implements Runnable {
        public final Socket backNodeSock;

        public BackNodeReceiver(Socket backNodeSock){
            this.backNodeSock = backNodeSock;
        }

        @Override
        public void run(){
            try{
                DataInputStream backInputStream = new DataInputStream(new BufferedInputStream(backNodeSock.getInputStream()));
                String msg = backInputStream.readUTF();

                System.out.println(msg);
            }
            catch(IOException ioe){
                ioe.getMessage();
            }
        }
    }
}
