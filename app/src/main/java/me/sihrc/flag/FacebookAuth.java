package me.sihrc.flag;

/**
 * Created by Chris on 5/25/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

/**
 * Created by Chris on 4/26/15.
 */
public abstract class FacebookAuth extends FragmentActivity {
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker;

    boolean shouldLoginAuth = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the requestCode is facebook then shouldLoginAuth is true
        shouldLoginAuth = requestCode == FacebookSdk.getCallbackRequestCodeOffset();
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                if (profile2 != null) {
                    loggedIn(profile2);
                } else {
                    loggedOut();
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                Profile.fetchProfileForCurrentAccessToken();
            }
        };
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        getProfileInfo();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e("Facebook Login", "Login Cancelled");
                        loggedOut();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("Facebook Login", "Failed to Login " + exception.toString());
                        loggedOut();
                    }
                });
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        profileTracker.startTracking();
        accessTokenTracker.startTracking();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        getProfileInfo();
        shouldLoginAuth = true;
    }

    public abstract void loggedIn(Profile profile);

    public abstract void loggedOut();

    private void getProfileInfo() {
        if (!shouldLoginAuth)
            return;
        Profile gottenProfile = Profile.getCurrentProfile();
        if (Profile.getCurrentProfile() != null && AccessToken.getCurrentAccessToken() != null) {
            loggedIn(gottenProfile);
        } else {
            AccessToken.getCurrentAccessToken();
            Profile.fetchProfileForCurrentAccessToken();
            loggedOut();
        }
    }
}