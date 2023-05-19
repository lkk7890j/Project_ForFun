package idv.tfp10105.project_forfun.membercenter.myevalution;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.PersonEvaluation;
import idv.tfp10105.project_forfun.membercenter.adapter.PersonnalAdapter;


public class MyTenantFragment extends Fragment {
    private Activity activity;
    private final int memberId;
    private TextView tvMyTenNote,tvMyTenScore;
    private RecyclerView rvMyTen;
    private RatingBar rbMyTen;
    private List<PersonEvaluation> personEvaluations  =new ArrayList<>();
    private final String url = Common.URL +"personalSnapshot";

    public MyTenantFragment(int memberId) {
        this.memberId = memberId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_myevaluationn_tenant, container, false);
        findView(view);
        return view;
    }


    private void findView(View view) {
        tvMyTenNote=view.findViewById(R.id.tvMyTenNote);
        tvMyTenScore=view.findViewById(R.id.tvMyTenScore);
        rvMyTen=view.findViewById(R.id.rvMyTen);
        rbMyTen=view.findViewById(R.id.rbMyTen);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        handleData();
    }

    private void handleData() {
        //連線
        if (RemoteAccess.networkCheck(activity)) {
            JsonObject client = new JsonObject();
            client.addProperty("action", "getAllEvaluation");
            client.addProperty("commentedID",memberId);
            client.addProperty("status","tenantStatus"); //分歧點
            String resp = RemoteAccess.getJsonData(url,client.toString());
            Type listType= new TypeToken<List<PersonEvaluation>>(){}.getType();
            personEvaluations=new Gson().fromJson(resp,listType);
            if(personEvaluations.size()==0){
                rvMyTen.setVisibility(View.GONE);
                tvMyTenNote.setVisibility(View.VISIBLE);
                return;
            }
            rvMyTen.setLayoutManager(new LinearLayoutManager(activity));
            PersonnalAdapter personnalAdapter=new PersonnalAdapter(activity, activity, personEvaluations);
            rvMyTen.setAdapter(personnalAdapter);
            int sum=0;
            for(PersonEvaluation personEvaluation:personEvaluations){
                sum+=personEvaluation.getPersonStar();
            }
            float avg=(float) sum/personEvaluations.size();//平均分數
            tvMyTenScore.setText("房客平均分數:"+(float)new BigDecimal(avg).setScale(1, RoundingMode.UP).doubleValue());
            rbMyTen.setRating(avg);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        handleData();
    }
}