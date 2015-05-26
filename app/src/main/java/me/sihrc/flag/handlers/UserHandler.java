package me.sihrc.flag.handlers;

import android.content.Context;

import me.sihrc.flag.models.User;

/**
 * Created by Chris on 5/25/15.
 */
public class UserHandler {
    static UserHandler instance;

    Context context;
    User user;

    public static UserHandler getInstance(Context context) {
        if (instance == null)
            instance = new UserHandler(context);
        return instance;
    }

    public UserHandler(Context context) {
        this.context = context;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
