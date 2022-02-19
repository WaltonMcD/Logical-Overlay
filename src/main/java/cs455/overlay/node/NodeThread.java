package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import cs455.overlay.protocols.Message;


// Handles front node socket / message sending and receiving
public class NodeThread {
    
    public static class FrontNodeSender implements Runnable {
        public ArrayList<Message> payloads = new ArrayList<Message>();

        public Integer numberOfMessages = 0;
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

                out.close();
                frontSocket.close();

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
        public Integer numberOfMessages = 0;
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
                DataInputStream nodeIn = new DataInputStream(new BufferedInputStream(backSocket.getInputStream()));
                DataOutputStream nodeOut = new DataOutputStream(new BufferedOutputStream(backSocket.getOutputStream()));

                //Waiting for task initiate.
                waitNodeReader();

                long total = 0;
                Integer messagesReceived = 0;
                for(int i = 0; i < numberOfMessages; i++){
                    Message traffic = new Message();
                    traffic.unpackMessage(nodeIn);
                    node.updateReceivedPayloadTotal(traffic.getPayload());
                    messagesReceived++;
                    total += traffic.getPayload();
                }
                node.numMessagesReceived = messagesReceived;
                node.payloadReceivedTotal = total;

                nodeIn.close();
                nodeOut.close();
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
    }
}
