package me.sihrc.flag.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.sihrc.flag.R;
import me.sihrc.flag.animation.AnimationHelper;
import me.sihrc.flag.handlers.UserHandler;
import me.sihrc.flag.models.User;
import me.sihrc.flag.network.FlagServer;
import me.sihrc.flag.network.NetworkCallback;

/**
 * Created by Chris on 5/25/15.
 */
public class SplashFragment extends FlagFragment {
    // Banner
    View banner;
    int bannerYorig;

    // Input Name
    String phone;
    EditText inputName;
    View displayName;

    // Loading Text
    TextView loadingText;
    Runnable loadingRunnable;
    int dotCount = 0;


    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get telephone number
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TelephonyManager tMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                phone = tMgr.getLine1Number();
                FlagServer.getInstance(activity).requestUser(phone, new NetworkCallback<User>() {
                    @Override
                    public void handle(User object) {
                        if (object == null) {
                            hideLoadingText();
                            bannerYorig = (int) banner.getY();
                            // Ask for User Information
                            AnimationHelper.linearAnimation(banner, 0, (int) -banner.getY() + 30, 1000, new Runnable() {
                                @Override
                                public void run() {
                                    AnimationHelper.fadeAnimation(inputName, 0f, 1f, 1000, null);
                                    AnimationHelper.fadeAnimation(displayName, 0f, 1f, 1000, null);
                                }
                            });
                        } else {
                            UserHandler.getInstance(activity).setUser(object);
                            // Show Map Fragment
                            activity.showFragment(MapFragment.newInstance());
                        }
                    }
                });

            }
        }, 2000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);

        banner = rootView.findViewById(R.id.splash_banner);
        loadingText = (TextView) rootView.findViewById(R.id.splash_loading_text);
        inputName = (EditText) rootView.findViewById(R.id.splash_input_name);
        displayName = rootView.findViewById(R.id.splash_name_layout);

        rootView.findViewById(R.id.splash_submit_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                FlagServer.getInstance(activity).createUser(name, phone, new NetworkCallback<User>() {
                    @Override
                    public void handle(User object) {
                        UserHandler.getInstance(activity).setUser(object);
                        // Show Map Fragment
                        activity.showFragment(MapFragment.newInstance());
                    }
                });

                AnimationHelper.fadeAnimation(displayName, 1f, 0f, 200, new Runnable() {
                    @Override
                    public void run() {
                        AnimationHelper.linearAnimation(banner, 0, bannerYorig - 30, 1000, new Runnable() {
                            @Override
                            public void run() {
                                showLoadingText();
                            }
                        });
                    }
                });
            }
        });

        showLoadingText();

        return rootView;
    }

    private void showLoadingText() {
        if (loadingText == null)
            return;

        loadingText.setVisibility(View.VISIBLE);
        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                loadingText.setText("Loading\n" + new String(new char[dotCount++ % 4]).replace("\0", "o "));
                loadingText.postDelayed(this, 500);
            }
        };
        loadingText.postDelayed(loadingRunnable, 500);
    }

    private void hideLoadingText() {
        if (loadingRunnable != null)
            loadingText.removeCallbacks(loadingRunnable);

        if (loadingText != null) {
            loadingText.setVisibility(View.INVISIBLE);
        }
    }
}
