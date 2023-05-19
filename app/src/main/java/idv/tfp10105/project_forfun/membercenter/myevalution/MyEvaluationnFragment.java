package idv.tfp10105.project_forfun.membercenter.myevalution;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.membercenter.adapter.PersonnalVPAdapter;

public class MyEvaluationnFragment extends Fragment {
    private Activity activity;
    private ViewPager2 vpMyEvalution;
    private TabLayout tlMyEvalution;
    private final List<Fragment> tabList = new ArrayList<>();
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myevaluationn, container, false);
        findView(view);
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        return view;
    }

    private void findView(View view) {
        vpMyEvalution = view.findViewById(R.id.vpMyEvalution);
        tlMyEvalution = view.findViewById(R.id.tlMyEvalution);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int memberId = sharedPreferences.getInt("memberId", -1);
        int role = sharedPreferences.getInt("role", -1);
        if (tabList.size() != 2) {
            //Fragment放入list
            tabList.add(new MyTenantFragment(memberId));//房客
//            if (role == 2) {
            tabList.add(new MyLandlordFragment(memberId));//房東
//            }
        }
        //list放入Adapter
        PersonnalVPAdapter myAdapter = new PersonnalVPAdapter(this, tabList);
        vpMyEvalution.setAdapter(myAdapter);
        vpMyEvalution.setOffscreenPageLimit(tabList.size() - 1);//預加載
        //TabLayout和ViewPager的綁定
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tlMyEvalution, vpMyEvalution, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                //設定tab名稱
                if (position == 0) {
                    tab.setText("房客身分的評價");
                }
                if (position == 1) {
                    tab.setText("房東身分的評價");
                }
            }
        });
        tabLayoutMediator.attach();
    }
}