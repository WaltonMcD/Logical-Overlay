package cs455.overlay.node;

import java.io.*;
import java.net.*;
import java.util.Random;

import cs455.overlay.protocols.Message;

// Handles front node socket / message sending and receiving
public class NodeThread {
    public static Integer numberOfMessages;

    public NodeThread(){
        // need default constructor to construct inner classes
    }
    
    public static class FrontNodeSender implements Runnable {
        public String frontIp;
        public Integer port;
        public Integer serverPort;

        public FrontNodeSender(String ip, Integer port, Integer serverPort){
            this.frontIp = ip;
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

        public Integer getRandomNumberUsingNextInt() {
            Random random = new Random();
            int num = random.nextInt();
            return num;
        }

        @Override
        public void run(){
            try{
                Socket frontSocket = new Socket(frontIp, serverPort);
                System.out.println("Connected to node: " + frontSocket.getInetAddress());

                DataOutputStream frontOutputStream = new DataOutputStream( new BufferedOutputStream(frontSocket.getOutputStream()));

                waitNodeSender();

                for(int i = 0; i < numberOfMessages; i++){
                    Message dataTraffic = new Message(5, port, getRandomNumberUsingNextInt());
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

    public static class BackNodeReader implements Runnable {
        public Socket backSocket;

        public BackNodeReader(Socket backSocket){
            this.backSocket = backSocket;
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
                DataInputStream backInputStream = new DataInputStream(new BufferedInputStream(backSocket.getInputStream()));

                waitNodeReader();

                Integer total = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Integer messageType = backInputStream.readInt();
                    Integer startNodeId = backInputStream.readInt();
                    Integer payload = backInputStream.readInt();
                    total += payload;

                    Message dataTraffic = new Message(messageType, startNodeId, payload);
                    System.out.println("Receiving data traffic from Node: " + dataTraffic.startNodeId);
                    
                }
                
                System.out.println("Received a total payload: " + total);
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
