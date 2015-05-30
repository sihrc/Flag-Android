package me.sihrc.flag.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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
    TextView banner;
    View submit;
    int originalY;
    final int TOP_MARGIN = 100;
    final int MINIMUM_NAME_LENGTH = 4;

    // Input Name
    String phone;
    EditText displayName;

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
                            showLoadingText(false);
                            originalY = (int) banner.getY();
                            // Ask for User Information
                            AnimationHelper.linearAnimation(banner, 0, (int) -banner.getY() + TOP_MARGIN, 1000, new Runnable() {
                                @Override
                                public void run() {
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

        // Setup Text and Font Faces
        banner = (TextView) rootView.findViewById(R.id.splash_banner);
        loadingText = (TextView) rootView.findViewById(R.id.splash_loading_text);
        displayName = (EditText) rootView.findViewById(R.id.splash_input_name);

        Typeface timeBurnerFont = Typeface.createFromAsset(activity.getAssets(), "timeburner_regular.ttf");
        banner.setTypeface(timeBurnerFont);
        loadingText.setTypeface(timeBurnerFont);
        displayName.setTypeface(timeBurnerFont);


        submit = rootView.findViewById(R.id.splash_submit_name);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = displayName.getText().toString();
                submit.setEnabled(false);
                showDoneButton(false);
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
                        AnimationHelper.linearAnimation(banner, 0, originalY - TOP_MARGIN, 1000, new Runnable() {
                            @Override
                            public void run() {
                                showLoadingText(true);
                            }
                        });
                    }
                });
            }
        });

        displayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showDoneButton(charSequence.length() > MINIMUM_NAME_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        showLoadingText(true);

        return rootView;
    }

    private void showDoneButton(boolean show) {
        if (show) {
            AnimationHelper.linearAnimation(submit, 0, 500, 0, 0, 1000, new Runnable() {
                @Override
                public void run() {
                    submit.setVisibility(View.VISIBLE);
                }
            });
        } else {
            AnimationHelper.linearAnimation(submit, 0, 0, 0, 500, 1000, new Runnable() {
                @Override
                public void run() {
                    submit.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showLoadingText(boolean show) {
        if (loadingText == null)
            return;
        if (show) {
            loadingText.setVisibility(View.VISIBLE);
            loadingRunnable = new Runnable() {
                @Override
                public void run() {
                    loadingText.setText("Loading\n" + new String(new char[dotCount++ % 4]).replace("\0", " . "));
                    loadingText.postDelayed(this, 500);
                }
            };
            loadingText.postDelayed(loadingRunnable, 500);
        } else {
            if (loadingRunnable != null)
                loadingText.removeCallbacks(loadingRunnable);

            if (loadingText != null) {
                loadingText.setVisibility(View.INVISIBLE);
            }
        }
    }
}
