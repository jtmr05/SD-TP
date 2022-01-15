package client;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import common.*;

import static common.Consts.*;

class WaitingRoom {

    private final Queue<Frame> toSend;
    private final Lock toSendLock;
    private final Condition toSendIsEmpty;

    WaitingRoom(){
        this.toSend = new LinkedList<>();
        this.toSendLock = new ReentrantLock();
        this.toSendIsEmpty = this.toSendLock.newCondition();
    }

    Frame getNextToSend() throws InterruptedException {
        try{
            this.toSendLock.lock();

            while(this.toSend.isEmpty())
                this.toSendIsEmpty.await();

            return this.toSend.poll();
        }
        finally{
            this.toSendLock.unlock();
        }
    }

    void addToSendQueue(MessageType type, String s){
        try{
            this.toSendLock.lock();
            this.toSend.add(new Frame(type, s));
            this.toSendIsEmpty.signalAll();
        }
        finally{
            this.toSendLock.unlock();
        }
    }
}