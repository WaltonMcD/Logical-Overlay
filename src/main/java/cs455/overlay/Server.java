package cs455.overlay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.TCPReciever;
import cs455.overlay.TCPSender;

public class Server {
    
    public Server(Integer port) throws IOException{
        
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Created Server Socket... ");
        int count = 0;
        count++;
        Socket incomingConnectionSocket = serverSocket.accept();
        System.out.println("Received a connection. Currently " + count +"client connected");
        

        TCPReciever inputStream = new TCPReciever(incomingConnectionSocket);
        TCPSender outputStream = new TCPSender(incomingConnectionSocket);

        byte[] msgToClient = new String("Is anybody there?").getBytes();
        outputStream.sendData(msgToClient);

        System.out.println("Message Sent... ");

        inputStream.run();

        outputStream.close();
        inputStream.close();
        incomingConnectionSocket.close();
        serverSocket.close();

    }
}
