package cs455.overlay.node;

import java.io.*;
import java.net.*;

import cs455.overlay.protocols.Message;

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
                    Message dataTraffic = new Message(5, port, 5);
                    frontOutputStream.writeInt(dataTraffic.messageType);
                    frontOutputStream.writeInt(dataTraffic.startNodeId);
                    frontOutputStream.writeInt(dataTraffic.payload);
                    frontOutputStream.flush();
                    System.out.println("Sending traffic to Node: " + dataTraffic.startNodeId + " Payload: " + dataTraffic.payload);
                }
                
                
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
                    Integer messageType = frontInputStream.readInt();
                    Integer startNodeId = frontInputStream.readInt();
                    Integer payload = frontInputStream.readInt();
                    total += payload;

                    Message dataTraffic = new Message(messageType, startNodeId, payload);
                    System.out.println("Receiving data traffic from Node: " + dataTraffic.startNodeId + " Payload: " + dataTraffic.payload);
                    
                }
                

                System.out.println(total);
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
