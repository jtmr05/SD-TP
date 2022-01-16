package client;

import java.io.IOException;

import common.TaggedConnection;

class ClientSender implements Runnable {

    private final WaitingRoom wr;
    private final TaggedConnection tc;

    ClientSender(WaitingRoom wr, TaggedConnection tc){
        this.wr = wr;
        this.tc = tc;
    }

    @Override
    public void run() {
        try{
            while(!Thread.interrupted()){
                var frame = this.wr.getNextToSend();
                this.tc.send(frame);
            }
        }
        catch(InterruptedException | IOException e){
            e.printStackTrace();
        }
    }
}
