package cs455.overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public Server(Integer port) throws IOException{
        
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Created Server Socket... ");
        int count = 0;
        count++;
        Socket incomingConnectionSocket = serverSocket.accept();
        System.out.println("Received a connection. Currently " + count +" client(s) are connected");
        
        DataInputStream inputStream = new DataInputStream(incomingConnectionSocket.getInputStream());
        String input = "";

        while(!input.equals("Exit")){
            Integer dataLength = inputStream.readInt();
            System.out.println("Received a message of length: " + dataLength);

            byte[] data = new byte[dataLength];
            inputStream.readFully(data, 0, dataLength);

            String msg_str = new String(data);
            System.out.println("Received message: " + msg_str);

            input = msg_str;
        }


        inputStream.close();
        incomingConnectionSocket.close();
        serverSocket.close();

    }
}
