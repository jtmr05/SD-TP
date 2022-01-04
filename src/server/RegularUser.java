package server;

class RegularUser extends User {

    RegularUser(String username, String password){
        super(username, password);
    }

    @Override
    UserType getType(){
        return UserType.REGULAR;
    }
}
