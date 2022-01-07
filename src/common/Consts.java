package common;

public final class Consts {

    private Consts(){}

    public static final int DEFAULT_PORT = 12345;
    public static final String DEFAULT_HOST = "localhost";


    public enum MessageType {
        LOGIN_REQUEST,
        LOGIN_FAIL,
        LOGIN_SUCESS,
        MENU,
        NOTIF,
        OPTION,
        QUIT;
    }
}
