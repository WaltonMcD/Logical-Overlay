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
import java.lang.Math;

public class MessagingNode {
    private ServerSocket server;
    private Integer ohGodPleaseBindToAnOpenPort;
    private ArrayList<MessageNode> connections;
    private MessageNode registry;
    private Integer PORT = 64001;
    private String SERVER_ADDRESS = "saint-paul.cs.colostate.edu";

    public class MessageNode {
        private Integer nodeIdentifier;
        private Integer portNumber;
        private DataInputStream input;
        private DataOutputStream output;
        private Socket socket;

    }

    public MessagingNode(){
        while (true){
            try{
                Integer ohGodPleaseBindToAnOpenPort = (int) Math.floor(Math.random() * 65537);
                server = new ServerSocket(ohGodPleaseBindToAnOpenPort);
                System.out.println("Server Started");
            }
            catch (UnknownHostException i){
                System.out.println(i);
            }
            catch(IOException i){
                System.out.println(i);
            }

            break;
        }

        while (true){
            try{
                Socket socket = new Socket(SERVER_ADDRESS, PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                registry.portNumber = PORT;
                registry.input = input;
                registry.output = output;
                registry.socket = socket;

                registry.output.writeInt(ohGodPleaseBindToAnOpenPort);
                registry.output.writeInt(ohGodPleaseBindToAnOpenPort);

            }
            catch (UnknownHostException i){
                System.out.println(i);
            }
            catch(IOException i){
                System.out.println(i);
            }
        }



    }



}
