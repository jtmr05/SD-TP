package server;

class Admin extends User {

    Admin(String username, String password){
        super(username, password);
    }

    @Override
    UserType getType(){
        return UserType.ADMIN;
    }
}
