package com.example.mybasevideoview.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mybasevideoview.ButterTestActivity;
import com.example.mybasevideoview.MainPlayerActivity;
import com.example.mybasevideoview.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class XslMainFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.start_players_btn)
    public Button btnStartPlayers;

    @BindView(R.id.test_retrofit)
    public Button btnTestRetrofit;

    @BindView(R.id.decor_view_btn)
    public Button btnDecorView;

    GuideViewUtil guideViewUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_xsl_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.start_players_btn, R.id.test_retrofit, R.id.decor_view_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_players_btn:
                Intent intent = new Intent(getContext(), MainPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.test_retrofit:
                Intent intent1 = new Intent(getContext(), ButterTestActivity.class);
                startActivity(intent1);
                break;
            case R.id.decor_view_btn:
                guideViewUtil = new GuideViewUtil(this.getActivity(), 0);
                guideViewUtil.showGuide();
                break;
        }
    }
}
