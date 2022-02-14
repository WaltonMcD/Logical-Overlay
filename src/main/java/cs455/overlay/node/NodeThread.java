package cs455.overlay.node;

import java.io.*;
import java.net.*;
import java.util.Random;

import cs455.overlay.protocols.Message;

// Handles front node socket / message sending and receiving
public class NodeThread {
    public static Integer numberOfMessages = 5;

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
                //e.printStackTrace();
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
                
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " detected interruption, exiting NodeThread frontSocket 1-1...");
                    frontOutputStream.close();
                    frontSocket.close();
                    return;
                }

                //Waiting for task initiate.
                waitNodeSender();
                
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " detected interruption, exiting NodeThread frontSocket 1-2...");
                    frontOutputStream.close();
                    frontSocket.close();
                    return;
                }

                int total = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Message dataTrafficMsg = new Message(5, port, getRandomNumberUsingNextInt());
                    dataTrafficMsg.packMessage(frontOutputStream);
                    total += dataTrafficMsg.getPayload();
                    System.out.println("Sending traffic to Node: " + dataTrafficMsg.getStartNodeId() + " Payload: " + dataTrafficMsg.getPayload());
                }
                node.numMessagesSent = numberOfMessages;
                node.payloadSentTotal = total;

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
                
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " detected interruption, exiting BackNodeReader...");
                    backInputStream.close();
                    backSocket.close();
                    return;
                }

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
                node.numMessagesReceived = numberOfMessages;
                node.payloadReceivedTotal = total;
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            node.notifyNode();
        }
    }
}
