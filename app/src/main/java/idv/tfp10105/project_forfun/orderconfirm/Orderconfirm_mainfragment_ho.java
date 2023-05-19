package idv.tfp10105.project_forfun.orderconfirm;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.orderconfirm.ocf_houseOwner.OcrHOAdapter;

public class Orderconfirm_mainfragment_ho extends Fragment {
    private Activity activity;
    private View view;
    private String bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle=getArguments()==null?"":getArguments().getString("postion");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orderconfirm_mainho, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton imageButton;

        //跳轉至刊登物件
        imageButton = view.findViewById(R.id.bt_orderconfirm_mainHO_Publishing);
        imageButton.setOnClickListener(v->{
            Navigation.findNavController(v).navigate(R.id.action_orderconfirm_mainfragment_ho_to_ocrHO_Publishing);
        });

        //switch
        Switch switch2 = view.findViewById(R.id.simpleSwitch_hosewOwner);
        switch2.setOnClickListener(v->{
            Navigation.findNavController(v).navigate(R.id.action_orderconfirm_mainfragment_ho_to_orderconfirm_mainfragment);
            Navigation.findNavController(view).popBackStack(R.id.orderconfirm_mainfragment_ho,true);
            switch2.toggle();
        });



        TabLayout tabLayout = view.findViewById(R.id.orderconfirm_main_HO_tabs);
        ViewPager2 viewPager2 = view.findViewById(R.id.orderconfirm_main_HO_viewpage2);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        OcrHOAdapter adapter = new OcrHOAdapter(fm,getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("待預約"));
        tabLayout.addTab(tabLayout.newTab().setText("待下訂"));
        tabLayout.addTab(tabLayout.newTab().setText("待簽約"));
        tabLayout.addTab(tabLayout.newTab().setText("待付款"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        tabLayout.addTab(tabLayout.newTab().setText("已取消"));
        tabLayout.addTab(tabLayout.newTab().setText("已收款"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                if(bundle.equals("待下訂")){
                    viewPager2.setCurrentItem(1,true);
                    bundle="";
                }
                if(bundle.equals("房東去開啟新合約")){
                    viewPager2.setCurrentItem(2,true);
                    bundle="";
                }
            }
        });


    }
}