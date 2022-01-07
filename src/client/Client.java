package client;

import java.io.IOException;
import java.net.Socket;

import common.*;
import static common.Consts.*;

final class Client {

    private Client(){}

    public static void main(String[] args) {

        IO io = new IO();

        try{
            WaitingRoom wr = new WaitingRoom();
            TaggedConnection tc = new TaggedConnection(new Socket(DEFAULT_HOST, DEFAULT_PORT));
            Thread senderThread = new Thread(new ClientSender(wr, tc));
            Thread receiverThread = new Thread(new ClientReceiver(wr, tc));

            senderThread.start();
            receiverThread.start();

            ClientController cc = new ClientController(wr, io);
            cc.start();

            senderThread.interrupt();
            receiverThread.interrupt();

            tc.close();
        }
        catch(IOException  e){
            io.err(e.getMessage());
        }
    }
}