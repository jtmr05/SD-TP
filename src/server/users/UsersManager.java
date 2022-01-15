package server.users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static common.Consts.UserType;

public class UsersManager {

    private static final String OBJ_STREAM_PATH;

    private Map<String, User> users;
    private Lock lock;

    static{
        OBJ_STREAM_PATH = System.getenv("HOME") + "/Documents/SD-TP-app-data/object";
    }

    private UsersManager(){
        this.users = new HashMap<>();
        User u = new User("admin", "admin", UserType.ADMIN);
        this.users.put(u.getUsername(), u);
        this.lock = new ReentrantLock();
    }

    private UsersManager(Map<String, User> users){
        this.users = users;
        this.lock = new ReentrantLock();
    }

    public User login(String username, String password){
        User u;
        try{
            this.lock.lock();
            u = this.users.get(username);
            if(u != null && !u.getPassword().equals(password))
                u = null;

            return u;
        }
        finally{
            this.lock.unlock();
        }
    }

    public void registerUser(User u){
        try{
            this.lock.lock();
            this.users.put(u.getUsername(), u);
        }
        finally{
            this.lock.unlock();
        }
    }

    public void saveState() throws IOException {
        try{
            this.lock.lock();
            var out = new ObjectOutputStream(new FileOutputStream(new File(OBJ_STREAM_PATH)));
            out.writeObject(this.users);
            out.flush();
            out.close();
        }
        finally{
            this.lock.unlock();
        }
    }

    private static UsersManager loadState(){
        UsersManager um = null;

        try{
            var in = new ObjectInputStream(new FileInputStream(new File(OBJ_STREAM_PATH)));
            Map<String, User> map = (Map<String, User>) in.readObject();
            um = new UsersManager(map);
            in.close();
        }
        catch(IOException | ClassNotFoundException e){
            um = (um == null) ? new UsersManager() : um;
        }

        return um;
    }

    private static UsersManager singleton = null;

    public static UsersManager getInstance(){
        if(singleton == null)
            singleton = loadState();

        return singleton;
    }
}

