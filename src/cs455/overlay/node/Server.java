package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public static void main(String[] args) throws IOException {
        Integer PORT = 6001;
        Integer NUM_OF_CONNECTIONS = 1;
        ServerSocket serverSocket;

        serverSocket = new ServerSocket(PORT, NUM_OF_CONNECTIONS);

        System.out.println("Created Server Socket... ");

        Socket incomingConnectionSocket = serverSocket.accept();

        System.out.println("Recieved a connection... ");

        DataInputStream inputStream = new DataInputStream(incomingConnectionSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(incomingConnectionSocket.getOutputStream());

        byte[] msgToClient = new String("Is anybody there?").getBytes();
        Integer msgToClientLength = msgToClient.length;

        outputStream.writeInt(msgToClientLength);
        outputStream.write(msgToClient, 0, msgToClientLength);

        System.out.println("Message Sent... ");

        Integer msgLength = 0;
        msgLength = inputStream.readInt();

        System.out.println("Recieved a Message of length: " + msgLength);

        byte[] incomingMessage = new byte[msgLength];
        inputStream.readFully(incomingMessage, 0, msgLength);

        String msg_str = new String(incomingMessage);

        System.out.println("Recieved message: " + msg_str);

        outputStream.close();
        inputStream.close();
        incomingConnectionSocket.close();
        serverSocket.close();

    }
}
