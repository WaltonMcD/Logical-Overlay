package cs455.overlay.node;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.protocols.Message;
import cs455.overlay.wireformats.PayloadMessageFormat;

// Handles front node socket / message sending and receiving
public class NodeThread {
    public static Integer numberOfMessages = 0;
    
    public static class FrontNodeSender implements Runnable {
        public String ip;
        public Integer port;
        public Integer serverPort;
        public Node node;
        public int toPort;
        public String toHost;

        public FrontNodeSender(String ip, Integer port, Integer serverPort, Node node, int toPort, String toHost){
            this.ip = ip;
            this.port = port;
            this.serverPort = serverPort;
            this.node = node;
            this.toPort = toPort;
            this.toHost = toHost;
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

        public long getRandomNumberUsingNextLong() {
            Random random = new Random();
            long num = random.nextLong();
            return num;
        }

        @Override
        public void run(){
            try{
            	
                Socket frontSocket = new Socket(ip, serverPort);
                System.out.println("Connected to node: " + frontSocket.getInetAddress());

                DataOutputStream out = new DataOutputStream( new BufferedOutputStream(frontSocket.getOutputStream()));
   
                //Waiting for task initiate.
                waitNodeSender();
        
                int totalMessages = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    long payload = getRandomNumberUsingNextLong();
                    Message dataTrafficMsg = new Message(5,i,i,payload,this.port, this.ip, this.toPort, this.toHost);
                    dataTrafficMsg.packMessage(out);
                    node.updateSentPayloadTotal(payload);
                    totalMessages++;
                }
                node.numMessagesSent = totalMessages;

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
        public ArrayList<Message> payloads;

        public BackNodeReader(Socket backSocket, Node node){
            this.backSocket = backSocket;
            this.node = node;
            this.payloads = new ArrayList<Message>();
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
                DataInputStream nodeIn = new DataInputStream(new BufferedInputStream(backSocket.getInputStream()));
                DataOutputStream nodeOut = new DataOutputStream(new BufferedOutputStream(backSocket.getOutputStream()));

                //Waiting for task initiate.
                waitNodeReader();

                long total = 0;
                Integer messagesReceived = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Message traffic = new Message();
                    traffic.unpackMessage(nodeIn);
                    payloads.add(traffic);
                    node.updateReceivedPayloadTotal(traffic.getPayload());
                    messagesReceived++;
                    total += traffic.getPayload();
                }
                node.numMessagesReceived = messagesReceived;
                node.payloadReceivedTotal = total;
                node.notifyNode();
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
