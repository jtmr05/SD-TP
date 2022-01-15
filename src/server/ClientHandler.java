package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import common.Consts;
import static common.Consts.UserType;
import server.users.UsersManager;
import utils.Menu;
import server.users.User;


public class ClientHandler implements Runnable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final UsersManager registeredUsers;

    ClientHandler(Socket s, UsersManager users) throws IOException {
        this.socket = s;
        this.registeredUsers = users;
        this.in = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void run(){

        try{
            User u = null;
            Menu m;
            do{
                //if(this.in.readInt() != Consts.LOGIN_REQUEST)
                //    this.in.readAllBytes();

                String username, password;
                username = this.in.readUTF();
                password = this.in.readUTF();

                u = this.registeredUsers.login(username, password);

                if(u == null)
                    //this.out.writeInt(Consts.LOGIN_FAIL);
                //else{
                    if(u.getType() == UserType.ADMIN)
                        m = new Menu("*** Admin Menu ***", new String[]{"Login"});
                    else
                        m = new Menu(new String[]{"Make a reservation"});

                    //this.out.writeInt(Consts.MENU);
                    //m.serialize(this.out);
                    this.out.flush();
                //}
            }
            while(u == null);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
