package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Map<String, User> registeredUsers;

    ClientHandler(Socket s, Map<String, User> users){
        this.socket = s;
        this.registeredUsers = users;
    }

    @Override
    public void run(){
        try{
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());

            String username, password;
            username = in.readUTF();
            password = in.readUTF();

            //talvez definir uma classe Mensagem definindo o que queremos dizer ao cliente
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
