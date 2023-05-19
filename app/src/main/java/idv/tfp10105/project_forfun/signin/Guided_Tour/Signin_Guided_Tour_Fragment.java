package idv.tfp10105.project_forfun.signin.Guided_Tour;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;

import idv.tfp10105.project_forfun.R;


public class Signin_Guided_Tour_Fragment extends Fragment {

//    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentStateAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     return inflater.inflate(R.layout.fragment_signin_guided_tour, container, false);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        findViews(view);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);

//        tabLayout.addTab(tabLayout.newTab().setText("P.1"));
//        tabLayout.addTab(tabLayout.newTab().setText("P.2"));
//        tabLayout.addTab(tabLayout.newTab().setText("P.3"));

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                pager2.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//
//        });

//        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                tabLayout.selectTab(tabLayout.getTabAt(position));
//            }
//        });
    }

    private void findViews(View view) {
//        tabLayout = view.findViewById(R.id.signin_tab_layout); //
        pager2 = view.findViewById(R.id.signin_view_pager2); //


    }
}