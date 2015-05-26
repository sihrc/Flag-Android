package me.sihrc.flag.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.sihrc.flag.models.User;
import me.sihrc.flag.network.parsers.UserParser;
import me.sihrc.flag.utils.Debugger;

/**
 * Created by Chris on 5/4/15.
 */
public class FlagServer {
    static FlagServer instance;

    ImageLoader imageLoader;
    Context context;
    RequestQueue queue;

    // Default Error Listener
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null && error.networkResponse.data != null)
                Log.e("Volley Error", new String(error.networkResponse.data));
            error.printStackTrace();
        }
    };

    public static FlagServer getInstance(Context context) {
        if (instance == null) {
            instance = new FlagServer(context);
        }
        return instance;
    }

    private static HashMap<String, String> headers = new HashMap<String, String>() {{
        put("Content-Type", "application/x-www-form-urlencoded");
    }};

    public FlagServer(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        this.imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            LruCache<String, Bitmap> cache = new LruCache<>(40);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        // Forget Signing
        NukeSSLCerts.nuke();
    }

    public void getNetworkImage(String url, final NetworkCallback<Bitmap> callback){
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (callback != null && response != null && response.getBitmap() != null) {
                    callback.handle(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null)
                    Log.e("Volley Error", new String(error.networkResponse.data));
                error.printStackTrace();
            }
        });
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void makeRequest(int method, final String url, final Map<String, String> body, final Response.Listener<JSONObject> listener) {
        makeRequest(method, url, body, listener, null);
    }

    /**
     * Request Wrapper
     */
    private void makeRequest(int method, final String url, final Map<String, String> body, final Response.Listener<JSONObject> listener, final Response.Listener<String> handledError) {
        Debugger.log(FlagServer.class, "Making request at " + url + "\n with package:\n" + (body != null ? body.toString() : "empty body"));
        queue.add(new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Debugger.log(FlagServer.class, "Request to:" + url + " with body:\n" + (body != null ? body.toString() : "empty") + "\n got response:\n" + response);
                    JSONObject res = new JSONObject(response);
                    if (res.getInt("error") != 0) {
                        Log.w("Server Failed", res.getString("message"));
                        if (handledError != null)
                            handledError.onResponse(res.getString("message"));
                    } else {
                        if (listener != null)
                            listener.onResponse(res.getJSONObject("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return body == null ? new HashMap<String, String>(0) : body;
            }
        });
    }

    public void requestUser(final String phone, final NetworkCallback<User> callback) {
        makeRequest(Request.Method.POST, API.POST_REQ_USER, new HashMap<String, String>() {{
            put("phone", phone);
        }}, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (callback != null)
                        callback.handle(new UserParser().parse(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callback != null)
                    callback.handle(null);
            }
        });
    }

    public void createUser(final String phone, final String name, final NetworkCallback<User> callback) {
        makeRequest(Request.Method.POST, API.POST_NEW_USER, new HashMap<String, String>() {{
            put("name", name);
            put("phone", phone);
        }}, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (callback == null)
                    return;
                try {
                    callback.handle(new UserParser().parse(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
