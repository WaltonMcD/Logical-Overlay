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
        public Node node;

        public FrontNodeSender(String ip, Integer port, Integer serverPort, Node node){
            this.frontIp = ip;
            this.port = port;
            this.serverPort = serverPort;
            this.node = node;
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

                //Waiting for task initiate.
                waitNodeSender();

                int total = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Message dataTraffic = new Message(5, port, getRandomNumberUsingNextInt());
                    total += dataTraffic.getPayload();
                    System.out.println("Sending traffic to Node: " + dataTraffic.getStartNodeId() + " Payload: " + dataTraffic.getPayload());
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
        public Node node;

        public BackNodeReader(Socket backSocket, Node node){
            this.backSocket = backSocket;
            this.node = node;
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

                //Waiting for task initiate.
                waitNodeReader();

                Integer total = 0;
                for(int i = 0; i < numberOfMessages; i++){
                	Message dataTraffic = new Message();
                	dataTraffic.unpackMessage(backInputStream);
                    total += dataTraffic.getPayload();

                    System.out.println("Receiving data traffic from Node: " + dataTraffic.getStartNodeId());
                    
                }
                
                System.out.println("Received a total payload: " + total);
                node.payloadReceivedTotal = total;

            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
