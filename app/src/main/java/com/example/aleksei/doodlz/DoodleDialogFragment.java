package com.example.aleksei.doodlz;

import android.app.Activity;
import android.app.DialogFragment;

public class DoodleDialogFragment extends DialogFragment {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setDialogOnScreen(false);
    }

    private void setDialogOnScreen(boolean visible) {
        MainActivityFragment fragment = getDoodleFragment();
        if (fragment != null) {
            fragment.setDialogOnScreen(visible);
        }
    }

    protected MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }
}
