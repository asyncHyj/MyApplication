package com.hyj.demo.indicator;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyj.demo.BaseFragment;

public class VPSimpleFragment extends BaseFragment {
    private static final String BUNDLE_TITLE = "title";

    private String mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mTitle = bundle.getString(BUNDLE_TITLE);
        }

        TextView tv = new TextView(getContext());
        tv.setText(mTitle);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * 实例化一个Fragme对象
     *
     * @param title
     * @return
     */
    public static VPSimpleFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);

        VPSimpleFragment fragment = new VPSimpleFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
}
