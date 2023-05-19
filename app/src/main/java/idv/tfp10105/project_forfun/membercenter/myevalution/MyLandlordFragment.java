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


public class MyLandlordFragment extends Fragment {
    private Activity activity;
    private final int memberId;
    private TextView tvMylandScore,tvMylandNote;
    private RecyclerView rvMyland;
    private RatingBar rbMyland;
    private List<PersonEvaluation> personEvaluations  =new ArrayList<>();
    private final String url = Common.URL +"personalSnapshot";

    public MyLandlordFragment(int memberId) {
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
        View view=inflater.inflate(R.layout.fragment_myevaluationn_landlord, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        handleData();
    }

    private void findView(View view) {
        tvMylandScore=view.findViewById(R.id.tvMylandScore);
        rvMyland=view.findViewById(R.id.rvMyland);
        rbMyland=view.findViewById(R.id.rbMyland);
        tvMylandNote=view.findViewById(R.id.tvMylandNote);
    }

    private void handleData() {
        //連線
        if (RemoteAccess.networkCheck(activity)) {
            JsonObject client = new JsonObject();
            client.addProperty("action", "getAllEvaluation");
            client.addProperty("commentedID",memberId);
            client.addProperty("status","landlordStatus"); //分歧點
            String resp = RemoteAccess.getJsonData(url,client.toString());
            Type listType= new TypeToken<List<PersonEvaluation>>(){}.getType();
            personEvaluations=new Gson().fromJson(resp,listType);
            if(personEvaluations.size()==0){
                rvMyland.setVisibility(View.GONE);
                tvMylandNote.setVisibility(View.VISIBLE);
                return;
            }
            rvMyland.setLayoutManager(new LinearLayoutManager(activity));
            PersonnalAdapter personnalAdapter=new PersonnalAdapter(activity, activity, personEvaluations);
            rvMyland.setAdapter(personnalAdapter);
            int sum=0;
            for(PersonEvaluation personEvaluation:personEvaluations){
                sum+=personEvaluation.getPersonStar();
            }
            float avg=(float) sum/personEvaluations.size();//平均分數
            tvMylandScore.setText("房東平均分數:"+(float)new BigDecimal(avg).setScale(1, RoundingMode.UP).doubleValue());
            rbMyland.setRating(avg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        handleData();
    }
}