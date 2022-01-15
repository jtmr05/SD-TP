package client;

import java.io.IOException;
import java.net.Socket;

import common.*;
import utils.IO;

import static common.Consts.*;

final class Client {

    private Client(){}

    public static void main(String[] args){

        IO io = new IO();

        try{
            WaitingRoom wr = new WaitingRoom();
            TaggedConnection tc = new TaggedConnection(new Socket(DEFAULT_HOST, DEFAULT_PORT));
            Thread senderThread = new Thread(new ClientSender(wr, tc));

            senderThread.start();

            ClientController cc = new ClientController(wr, io, tc);
            cc.start();

            senderThread.interrupt();

            tc.close();
        }
        catch(IOException  e){
            io.err(e.getMessage());
        }
    }
}