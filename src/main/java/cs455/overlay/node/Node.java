package cs455.overlay.node;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;

public class Node extends Thread {
    public Integer identifier;
    public Socket socketToServer;
    
    public Node(Socket socketToServer) {
        this.identifier = socketToServer.getLocalPort();
        this.socketToServer = socketToServer;
        System.out.println("Connection Created With Node: " + identifier); 
    }

    public void run() {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Enter your message: ");

            DataOutputStream outputStream = new DataOutputStream(socketToServer.getOutputStream());
            String line = "";

            while(!line.equals("exit-overlay")){
                line = input.nextLine();
                int dataLength = line.length();
                outputStream.writeInt(dataLength);
                outputStream.write(line.getBytes(), 0, dataLength);
                outputStream.flush();
            }
            System.out.println("Closing Connection with Server... ");
            input.close();
            outputStream.close();
            socketToServer.close();

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }   
    }
}
