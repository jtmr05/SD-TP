package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client{

    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args){
        try{
            Socket socket = new Socket("localhost", DEFAULT_PORT);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            String username, password;

            System.out.println("*** Login ***");
            System.out.print("Username: ");
            username = systemIn.readLine();
            System.out.print("Password: ");
            password = systemIn.readLine();

            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}