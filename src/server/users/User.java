package server.users;

import java.io.Serializable;

public class User implements Serializable {

    private final String username;
    private final String password;
    private final UserType type;

    User(String username, String password, String type){
        this.username = username;
        this.password = password;
        this.type = Enum.valueOf(UserType.class, type);
    }

    User(String username, String password, UserType type){
        this.username = username;
        this.password = password;
        this.type = type;
    }

    String getUsername(){
        return this.username;
    }

    String getPassword(){
        return this.password;
    }

    public UserType getType(){
        return this.type;
    }
}
