package client;

import java.io.IOException;

import common.TaggedConnection;

class ClientReceiver implements Runnable {

    private final WaitingRoom wr;
    private final TaggedConnection tc;

    ClientReceiver(WaitingRoom wr, TaggedConnection tc){
        this.wr = wr;
        this.tc = tc;
    }

    @Override
    public void run() {
        try{
            while(!Thread.interrupted()){
                var frame = this.tc.receive();
                this.wr.addToReceived(frame);
            }
        }
        catch(IOException e){}
    }
}
