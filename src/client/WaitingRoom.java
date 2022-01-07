package client;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import common.*;

import static common.Consts.*;

class WaitingRoom {

    private final Queue<Frame> received;
    private final Queue<Frame> toSend;
    private final Lock receivedLock;
    private final Lock toSendLock;
    private final Condition receivedIsEmpty;
    private final Condition toSendIsEmpty;
    private Long clientId;

    WaitingRoom(){
        this.received = new LinkedList<>();
        this.toSend = new LinkedList<>();
        this.receivedLock = new ReentrantLock();
        this.toSendLock = new ReentrantLock();
        this.receivedIsEmpty = this.receivedLock.newCondition();
        this.toSendIsEmpty = this.toSendLock.newCondition();
        this.clientId = null;
    }

    Frame getNextReceived() throws InterruptedException{
        try{
            this.receivedLock.lock();

            while(this.received.isEmpty())
                this.receivedIsEmpty.await();

            return this.received.poll();
        }
        finally{
            this.receivedLock.unlock();
        }
    }

    void addToReceived(Frame elem){
        try{
            this.receivedLock.lock();
            this.received.add(elem);
            this.receivedIsEmpty.signalAll();
        }
        finally{
            this.receivedLock.unlock();
        }
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

    void setClientId(long id){
        if(this.clientId == null)
            this.clientId = id;
    }

    Long getClientId(){
        return this.clientId;
    }
}