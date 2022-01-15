package common;

public final class Consts {

    private Consts(){}

    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "localhost";


    public enum MessageType {
        LOGIN_REQUEST,
        LOGIN_FAIL,
        LOGIN_SUCESS,


        FLIGHTS_LIST,
        USER_TYPE,
        NOTIF,


        MAKE,
        CANCEL,
        NEW,
        CLOSE,
        QUIT;
    }

    public enum UserType {
        REGULAR,
        ADMIN;
    }
}