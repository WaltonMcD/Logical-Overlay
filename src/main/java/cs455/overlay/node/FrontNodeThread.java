package cs455.overlay.node;

import java.io.*;
import java.net.*;

// Handles front node socket / message sending and receiving
public class FrontNodeThread {
    public static Integer numberOfMessages;

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

        public synchronized void waitNodeSender(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        public synchronized void notifyNodeSender(){
            notify();
        }

        @Override
        public void run(){
            try{
                System.out.println(ip);
                Socket frontSocket = new Socket(ip, serverPort);
                System.out.println("Connected to node: " + frontSocket.getInetAddress());

                DataOutputStream frontOutputStream = new DataOutputStream( new BufferedOutputStream(frontSocket.getOutputStream()));

                waitNodeSender();

                for(int i = 0; i < numberOfMessages; i++){
                    frontOutputStream.writeInt(5);
                }
                
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

    public static class FrontNodeReader implements Runnable {
        public Socket frontSocket;

        public FrontNodeReader(Socket frontSocket){
            this.frontSocket = frontSocket;
        }

        public synchronized void waitNodeReader(){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        public synchronized void notifyNodeReader(){
            notify();
        }

        @Override
        public void run(){
            try{
                DataInputStream frontInputStream = new DataInputStream(new BufferedInputStream(frontSocket.getInputStream()));

                waitNodeReader();

                Integer total = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Integer num = frontInputStream.readInt();
                    total += num;
                }
                

                System.out.println(total);
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
