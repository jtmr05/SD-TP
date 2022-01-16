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
        GET,
        NEW,
        CLOSE,
        QUIT;
    }

    public enum UserType {
        REGULAR,
        ADMIN;
    }
}

/**
 * LOGIN_REQUEST + username + '\0' + password            //client
 * LOGIN_FAIL + err_msg                                  //server
 * LOGIN_SUCESS + sessionId                              //server
 *
 * ===//===
 *
 * USER_TYPE + usertype                                  //server
 * FLIGHTS_LIST + flight1 + '\0' + flight2 + ...         //server
 * NOTIF + message                                       //server
 *
 * MAKE, CANCEL, CLOSE, NEW + id + '\0' + string         //client
 *
 * QUIT + client_id                                      //client
 */