package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import common.*;

class Client{

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private Client() throws IOException {
        this.socket = new Socket("localhost", Consts.DEFAULT_PORT);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    private void run() throws IOException {
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

        do{
            this.out.writeInt(Consts.LOGIN_REQUEST);
            this.login(systemIn);
            this.out.flush();
        }
        while(this.readTag() == Consts.LOGIN_FAIL);
    }

    private int readTag() throws IOException {
        int tag = this.in.readInt();

        return tag;
    }

    private void login(BufferedReader r) throws IOException {
        String username, password;

        System.out.println("*** Login ***");
        System.out.print("Username: ");
        username = r.readLine();
        System.out.print("Password: ");
        password = r.readLine();

        this.out.writeUTF(username);
        this.out.writeUTF(password);
    }

    private void close() throws IOException {
        this.out.close();
        this.in.close();
        this.socket.close();
    }

    public static void main(String[] args){

        try{
            Client c = new Client();
            c.run();
            c.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}