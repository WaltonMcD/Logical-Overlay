package cs455.overlay.routing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.Node;
import cs455.overlay.protocols.Message;

public class RegistryThread extends Thread{
    public Socket nodeSocket;
    public Integer identifier;
    public Registry registry;
    public int numOfConnections;
    public Integer numberOfMessages;

    public RegistryThread(Socket nodeSocket, Registry registry, int numOfConnections) {
        this.nodeSocket = nodeSocket;
        this.identifier = nodeSocket.getPort();
        this.registry = registry;
        this.numOfConnections = numOfConnections;
    }

    public synchronized void waitRegThread(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyRegThread(){
        notify();
    }

    @Override
    public void run(){
        try{
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(nodeSocket.getOutputStream()));
        

            // Receive Registration Request
            Message registrationRequestMsg = new Message();
            registrationRequestMsg.unpackMessage(inputStream);
            registry.nodesList.add(new Node(registrationRequestMsg.getIpAddress(), registrationRequestMsg.getPort(), identifier));
            registry.notifyRegistry();

            // Send Registration Response
            Message registrationResponseMsg = new Message(2, 200, identifier, "\'Welcome\'");
            registrationResponseMsg.packMessage(outputStream);

            // Send Connection Directive
            if(registry.directives.size() != numOfConnections){
                waitRegThread();
            }

            Message connectionDirectiveMsg = null;
            for(int i = 0; i < registry.directives.size(); i++){
                if(registry.directives.get(i).getIdentifier().equals(this.identifier)){
                    connectionDirectiveMsg = new Message(3, registry.directives.get(i).getFrontNodePort(), registry.directives.get(i).getFrontNodeIp(), 
                                                            registry.directives.get(i).getBackNodePort(), registry.directives.get(i).getBackNodeIp(), this.numOfConnections);
                    connectionDirectiveMsg.packMessage(outputStream);
                }
            }

            this.waitRegThread();

            while(numberOfMessages == null){
                if(numberOfMessages != null){
                    break;
                }
            }

            // Send Task Initiate
            Message taskInitiateMsg = new Message(4, numberOfMessages);
            taskInitiateMsg.packMessage(outputStream);

            //Receive Task Complete
            Message taskCompleteMsg = new Message();
            taskCompleteMsg.unpackMessage(inputStream);
            registry.completedTasks.add(taskCompleteMsg);

            //Send Traffic Summary Request.
            Integer trafficSummReqType = 7;
            Message trafficSummReqMsg = new Message(trafficSummReqType, this.nodeSocket.getLocalAddress().getHostName());
            trafficSummReqMsg.packMessage(outputStream);

            Thread.sleep(200);

            //Receive Traffic Summary
            Message trafficSummary = new Message();
            trafficSummary.unpackMessage(inputStream);
            registry.trafficSummaryMessages.add(trafficSummary);

            Message deregistration = new Message();
            deregistration.unpackMessage(inputStream);
            
            inputStream.close();
            outputStream.close();
            nodeSocket.close();
        }
        catch(IOException | InterruptedException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    public void setNumberOfMessages(Integer number) {
        this.numberOfMessages = number;
    }
}
