package me.sihrc.flag.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import me.sihrc.flag.network.FlagServer;

/**
 * Created by Chris on 5/4/15.
 */
public class Debugger {
    static Debugger instance;

    public static Debugger getInstance() {
        if (instance == null)
            instance = new Debugger();
        return instance;
    }

    // Enable Debugging
    public static Map<Class, Boolean> tags = new HashMap<Class, Boolean>() {{
        put(FlagServer.class, false);
    }};

    public static void log(Class tag, String msg) {
        if (tags.get(tag)) {
            Log.d("BoolioDebug", tag.getSimpleName() + ":\n" + msg);
        }
    }

    public static void i(String msg) {
        Log.i("DebugDebug", msg);
    }
}
