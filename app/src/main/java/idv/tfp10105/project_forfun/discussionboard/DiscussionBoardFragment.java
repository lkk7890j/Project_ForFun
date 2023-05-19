package idv.tfp10105.project_forfun.discussionboard;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.discussionboard.disboard.DiscussionBoard_KnowledgeFragment;
import idv.tfp10105.project_forfun.discussionboard.disboard.DiscussionBoard_RentHouseFragment;
import idv.tfp10105.project_forfun.discussionboard.disboard.DiscussionBoard_RentSeekingFragment;


public class DiscussionBoardFragment extends Fragment {
    private Activity activity;
    private List<Fragment> list = new ArrayList<>();
    private String[] title = {"租屋交流", "知識問答", "需求單"};
    private ViewPager2 viewPager;
    private TabLayout disTab;
    private String  bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();

        bundle = getArguments() != null ? (String) getArguments().getSerializable("board"): "";

        View view = inflater.inflate(R.layout.fragment_discussion_board, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewPager.setAdapter(new MyAdapter(activity));
//        viewPager.setOffscreenPageLimit(2);
        TabLayoutMediator tab = new TabLayoutMediator(disTab, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("租屋交流");
                        break;
                    case 1:
                        tab.setText("知識問答");
                        break;
                    case 2:
                        tab.setText("需求單");
                        break;
                }
            }
        });
        tab.attach();
    }


    private void findViews(View view) {
        viewPager = view.findViewById(R.id.dis_viewPage2);
        disTab = view.findViewById(R.id.dis_tabview);
    }

    public class MyAdapter extends FragmentStateAdapter {
        public MyAdapter(@NonNull Activity fragmentActivity) {
            super((FragmentActivity) fragmentActivity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {

            if (bundle.equals("租屋交流")) {
                viewPager.setCurrentItem(0);
            } else if (bundle.equals("知識問答")) {
                viewPager.setCurrentItem(1);
            } else {
                //指定跳轉 viewPager2的 index
                viewPager.setCurrentItem(2);
            }

            switch (position)
            {
                case 0:
                    return new DiscussionBoard_RentHouseFragment();
                case 1:
                    return new DiscussionBoard_KnowledgeFragment();
                default:
                    return new DiscussionBoard_RentSeekingFragment();

            }
        }

    }

}