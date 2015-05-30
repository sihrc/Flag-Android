package me.sihrc.flag.network;

/**
 * Created by Chris on 5/25/15.
 */
public class API {
    private final static String BASE = "http://192.168.1.126:3000/";

    public static String POST_REQ_USER (String phone)  {
        return BASE + "users/" + phone;
    }
    final public static String POST_NEW_USER = BASE + "users/new";
}
