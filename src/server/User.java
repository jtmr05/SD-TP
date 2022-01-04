package server;

abstract class User{

    private final String username;
    private final String password;

    User(String username, String password){
        this.username = username;
        this.password = password;
    }

    String getUsername(){
        return this.username;
    }

    String getPassword(){
        return this.password;
    }

    abstract UserType getType();
}
