package idv.tfp10105.project_forfun.membercenter.personnalsanpshot;

import android.app.Activity;
import android.os.Bundle;

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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.bean.PersonEvaluation;
import idv.tfp10105.project_forfun.membercenter.adapter.PersonnalAdapter;

public class TenantstatusFragment extends Fragment {
    private Activity activity;
    private List<PersonEvaluation> personEvaluations  =new ArrayList<>();
    private Member selectUser;
    private RecyclerView rvTen;
    private RatingBar rbTenantScore;
    private TextView tenStatusNote,tvTenantScore;
    private final String url = Common.URL +"personalSnapshot";

    public TenantstatusFragment(Member selectUser) {
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
        View view=inflater.inflate(R.layout.fragment_personalsnapshot_tenantstatus, container, false);
        findView(view);

        handleTenData();
        return view;
    }

    private void findView(View view) {
        rvTen=view.findViewById(R.id.rvTen);
        rbTenantScore=view.findViewById(R.id.rbTenantScore);
        tenStatusNote=view.findViewById(R.id.tenStatusNote);
        tvTenantScore=view.findViewById(R.id.tvTenantScore);
    }

    private void handleTenData() {
        //連線
        if (RemoteAccess.networkCheck(activity)) {
            JsonObject client = new JsonObject();
            client.addProperty("action", "getAllEvaluation");
            client.addProperty("commentedID",selectUser.getMemberId());
            client.addProperty("status","tenantStatus");
            String resp = RemoteAccess.getJsonData(url,client.toString());

                    Type listType= new TypeToken<List<PersonEvaluation>>(){}.getType();
                    personEvaluations=new Gson().fromJson(resp,listType);
                    if(personEvaluations.size()==0){
                         rvTen.setVisibility(View.GONE);
                        tenStatusNote.setVisibility(View.VISIBLE);
                        return;
                    }
                    rvTen.setLayoutManager(new LinearLayoutManager(activity));
                    PersonnalAdapter personnalAdapter=new PersonnalAdapter(activity, activity, personEvaluations);
                    rvTen.setAdapter(personnalAdapter);
                    int sum=0;
                    for(PersonEvaluation personEvaluation:personEvaluations){
                        sum+=personEvaluation.getPersonStar();
                    }
                    float avg=(float) sum/personEvaluations.size();//平均分數
                    tvTenantScore.setText("房客評價平均分:"+(float)new BigDecimal(avg).setScale(1, RoundingMode.UP).doubleValue());
                    rbTenantScore.setRating(avg);


        }
    }
}