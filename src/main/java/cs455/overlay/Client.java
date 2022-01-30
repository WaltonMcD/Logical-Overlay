package cs455.overlay;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;

public class Client extends Thread {
    private Integer identifier;
    private Socket socketToServer;
    
    public Client(Socket socketToServer, Integer identifier) {
        this.identifier = identifier;
        this.socketToServer = socketToServer;
        System.out.println("Connection Created With Client: " + identifier); 
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your message: ");
        try {
            DataOutputStream outputStream = new DataOutputStream(socketToServer.getOutputStream());
            String line = "";

            while(!line.equals("Exit")){
                line = input.nextLine();
                int dataLength = line.length();
                outputStream.writeInt(dataLength);
                outputStream.write(line.getBytes(), 0, dataLength);
                outputStream.flush();
            }
            input.close();
            outputStream.close();
            socketToServer.close();

        } catch (IOException ioe) {
            ioe.getMessage();
        }
        

        
    }
}
