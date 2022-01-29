package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Registry {
	private ServerSocket server;
    private ArrayList<MessageNode> messageNodes;
    private Integer PORT = 64001;

    public class MessageNode {
        private Integer nodeIdentifier;
        private Integer portNumber;
        private DataInputStream input;
        private DataOutputStream output;
        private Socket socket;

    }

	public Registry(int port){
        // this.PORT;
        Integer messageNodeCount = 0;

        try{
            server = new ServerSocket(PORT);
            System.out.println("Server Started");
        }
        catch(IOException i){
            System.out.println(i);
        }

        while (true){
            try{
                MessageNode newNode = new MessageNode();

                newNode.socket = server.accept();
                System.out.println("Client Accepted");

                newNode.input = new DataInputStream(new BufferedInputStream(newNode.socket.getInputStream()));
                newNode.output = new DataOutputStream(new BufferedOutputStream(newNode.socket.getOutputStream()));
                
                newNode.nodeIdentifier = newNode.input.readInt(); 
                newNode.portNumber = newNode.input.readInt();
                
                messageNodes.add(newNode);
                
                messageNodeCount += 1;
                if (messageNodeCount > 10){
                    break;
                }
            }
            catch (UnknownHostException i){
                System.out.println(i);
            }
            catch(IOException i){
                System.out.println(i);
            }
        }

        // socket.close();
        // put in for loop 
        // input.close();
        // output.close():
	}

}