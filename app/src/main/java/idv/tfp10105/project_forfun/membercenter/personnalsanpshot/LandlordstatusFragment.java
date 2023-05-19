package idv.tfp10105.project_forfun.membercenter.personnalsanpshot;

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
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.bean.PersonEvaluation;
import idv.tfp10105.project_forfun.membercenter.adapter.PersonnalAdapter;


public class LandlordstatusFragment extends Fragment {
    private Activity activity;
    private List<PersonEvaluation> personEvaluations  =new ArrayList<>();
    private Member selectUser;
    private RecyclerView rvLand;
    private RatingBar rbLandlordScore;
    private TextView landStatusNote,tvLandlordScore;
    private final String url = Common.URL +"personalSnapshot";
    public LandlordstatusFragment(Member selectUser) {
        this.selectUser = selectUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_personalsnapshot_landlordstatus, container, false);
        findView(view);
        return view;
    }

    private void findView(View view) {
        rvLand=view.findViewById(R.id.rvLand);
        rbLandlordScore=view.findViewById(R.id.rbLandlordScore);
        landStatusNote=view.findViewById(R.id.landStatusNote);
        tvLandlordScore=view.findViewById(R.id.tvLandlordScore);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleLanData();
    }

    private void handleLanData() {
        //連線
        if (RemoteAccess.networkCheck(activity)) {
            JsonObject client = new JsonObject();
            client.addProperty("action", "getAllEvaluation");
            client.addProperty("commentedID",selectUser.getMemberId());
            client.addProperty("status","landlordStatus"); //分歧點
            String resp = RemoteAccess.getJsonData(url,client.toString());
                Type listType= new TypeToken<List<PersonEvaluation>>(){}.getType();
                personEvaluations=new Gson().fromJson(resp,listType);
                if(personEvaluations.size()==0){
                    rvLand.setVisibility(View.GONE);
                    landStatusNote.setVisibility(View.VISIBLE);
                    return;
                }
                rvLand.setLayoutManager(new LinearLayoutManager(activity));
                PersonnalAdapter personnalAdapter=new PersonnalAdapter(activity, activity, personEvaluations);
                rvLand.setAdapter(personnalAdapter);
                int sum=0;
                for(PersonEvaluation personEvaluation:personEvaluations){
                    sum+=personEvaluation.getPersonStar();
                }
                float avg=(float) sum/personEvaluations.size();//平均分數
                tvLandlordScore.setText("房東平均分數:"+(float)new BigDecimal(avg).setScale(1, RoundingMode.UP).doubleValue());
                rbLandlordScore.setRating(avg);



        }
    }
}