package me.sihrc.flag.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import me.sihrc.flag.MainActivity;

/**
 * Created by Chris on 5/25/15.
 */
public class FlagFragment extends Fragment {
    MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }
}
