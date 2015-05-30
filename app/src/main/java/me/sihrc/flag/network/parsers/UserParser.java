package me.sihrc.flag.network.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import me.sihrc.flag.models.User;

/**
 * Created by Chris on 5/25/15.
 */
public class UserParser extends Parser<User> {
    @Override
    public User parse() throws JSONException {
        User user = new User();

        user.name = getString("name");
        user.phone = getString("phone");

        try {
            user.lat = Float.parseFloat(getString("lat"));
            user.lon = Float.parseFloat(getString("lon"));
        } catch (NumberFormatException | NullPointerException ignored) {}

        return user;
    }

    @Override
    public JSONObject toJSON(User object) throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", object.name);
        jsonObject.put("phone", object.phone);
        jsonObject.put("lat", object.lat);
        jsonObject.put("lon", object.lon);

        return jsonObject;
    }
}
