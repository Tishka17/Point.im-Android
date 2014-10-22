package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SinglePostFragment extends Fragment {
    private static final String ARG_POST = "post";

    private String mPost;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post Post ID.
     * @return A new instance of fragment SinglePostFragment.
     */
    public static SinglePostFragment newInstance(String post) {
        SinglePostFragment fragment = new SinglePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    public SinglePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = getArguments().getString(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_post, container, false);
        ((TextView)rootView.findViewById(R.id.text)).setText(mPost);
        return rootView;
    }
}
