package client;

import common.Frame;
import utils.Menu;

import static common.Consts.MessageType;

class ClientController{

    private final WaitingRoom wr;
    private final IO io;
    private Long clientId;

    ClientController(WaitingRoom wr, IO io){
        this(wr, io, null);
    }

    ClientController(WaitingRoom wr, IO io, Long clientId){
        this.wr = wr;
        this.io = io;
        this.clientId = clientId;
    }

    void start(){
        this.login();

        try{
            while(true){
                Frame f = this.wr.getNextReceived();

                switch(f.tag){

                    case MENU -> {

                        Menu m = Menu.deserialize(f.getDataAsString());
                        int i;

                        if(this.io.setMainMenu(m))           //true if it was set
                            i = this.io.runMainMenu();
                        else
                            i = this.io.runMenu(m);

                        this.wr.addToSendQueue(MessageType.QUIT, this.clientId.toString() + "\0" + i);
                    }

                    case NOTIF -> {}

                    default -> {}
                }
            }
        }
        catch(InterruptedException e){
            this.io.err(e.getMessage());
        }
    }

    private void login(){

        try{
            while(this.clientId == null){

                var credentials = this.io.login();
                StringBuilder sb = new StringBuilder(credentials.getA()).
                                       append("\0").
                                       append(credentials.getB());
                String s = sb.toString();

                this.wr.addToSendQueue(MessageType.LOGIN_REQUEST, s);

                Frame f = this.wr.getNextReceived();
                s = f.getDataAsString();

                if(f.tag == MessageType.LOGIN_SUCESS)
                    this.clientId = Long.parseLong(s);
                else if(f.tag == MessageType.LOGIN_FAIL)
                    this.io.err(s);
            }
        }
        catch(InterruptedException e){
            this.io.err(e.getMessage());
        }
    }
}

/**
 * LOGIN_REQUEST + username + '\0' + password  //cliente
 * LOGIN_FAIL + err_msg                        //server
 * LOGIN_SUCESS + sessionId                    //server
 * MENU + menu_string                          //server
 * NOTIF + message                             //server
 * QUIT                                        //server
 * OPTION + client_id "\0" + option            //cliente
 */