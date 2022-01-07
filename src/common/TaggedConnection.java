package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.*;

import common.Consts.MessageType;

public class TaggedConnection implements Closeable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Lock readLock;
    private final Lock writeLock;

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.writeLock = new ReentrantLock();
        this.readLock = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        try{
            this.writeLock.lock();

            var data = frame.getData();

            this.out.writeInt(frame.tag.ordinal());
            this.out.writeInt(data.length);
            this.out.write(data);
            this.out.flush();
        }
        finally{
            this.writeLock.unlock();
        }
    }

    public void send(MessageType tag, byte[] data) throws IOException {
        this.send(new Frame(tag, data));
    }

    public void send(MessageType tag, String str) throws IOException {
        this.send(new Frame(tag, str));
    }

    public Frame receive() throws IOException {
        try{
            this.readLock.lock();

            int tag = this.in.readInt();
            byte[] data = new byte[this.in.readInt()];
            this.in.readFully(data);

            return new Frame(MessageType.values()[tag], data);
        }
        finally{
            this.readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}