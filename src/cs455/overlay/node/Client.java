package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String SERVER_ADDRESS = "juneau.cs.colostate.edu";
        Integer PORT = 6001;
        
        Socket socketToServer = new Socket(SERVER_ADDRESS, PORT);
        DataInputStream inputStream = new DataInputStream(socketToServer.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socketToServer.getOutputStream());

        System.out.println("Conncetion Created... ");

        Integer msgLength = 0;
        msgLength = inputStream.readInt();

        System.out.println("Recieved a message of length: " + msgLength);

        byte[] incomingMessage = new byte[msgLength];
        inputStream.readFully(incomingMessage, 0, msgLength);

        String msg_str = new String(incomingMessage);

        System.out.println("Recieved message: " + msg_str);

        byte[] msgToServer = new String("Hello!").getBytes();
        Integer msgToServerLength = msgToServer.length;

        outputStream.writeInt(msgToServerLength);
        outputStream.write(msgToServer, 0, msgToServerLength);

        inputStream.close();
        outputStream.close();
        socketToServer.close();
    }
}
