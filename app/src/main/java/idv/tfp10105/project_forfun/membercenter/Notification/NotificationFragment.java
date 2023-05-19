package idv.tfp10105.project_forfun.membercenter.Notification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Notification;
import idv.tfp10105.project_forfun.membercenter.adapter.NotificationAdapter;


public class NotificationFragment extends Fragment {
    private Activity activity;
    private RecyclerView rvNotification;
    private TextView tvNotificationNote;
    private SharedPreferences sharedPreferences;
    private final String url= Common.URL+"NotificationController";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences", Context.MODE_PRIVATE);
        rvNotification=view.findViewById(R.id.rvNotification);
        tvNotificationNote=view.findViewById(R.id.tvNotificationNote);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(RemoteAccess.networkCheck(activity)) {
            int memberId = sharedPreferences.getInt("memberId", -1);
            if (memberId != -1) {
                rvNotification.setLayoutManager(new LinearLayoutManager(activity));
                JsonObject req = new JsonObject();
                req.addProperty("action", "getNotification");
                req.addProperty("memberId", memberId);
                //取得通知
                JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()),JsonObject.class);
                Type notificationList=new TypeToken<List<Notification>>(){}.getType();
                Type customerList=new TypeToken<List<String>>(){}.getType();
                Type appointmentOwnerIdList=new TypeToken<List<Integer>>(){}.getType();
                List<Notification>notifications=new Gson().fromJson(resp.get("Notifications").getAsString(),notificationList);
                List<String>customersHeadShot=new Gson().fromJson(resp.get("CustomersHeadShot").getAsString(),customerList);
                List<Integer>ownerId=new Gson().fromJson(resp.get("ownerId").getAsString(),appointmentOwnerIdList);
                if(notifications.size()==0){
                    tvNotificationNote.setVisibility(View.VISIBLE);
                    rvNotification.setVisibility(View.GONE);
                }
                rvNotification.setAdapter(new NotificationAdapter(activity,notifications,customersHeadShot,ownerId));
                //將通知狀態改成已讀
                    req.addProperty("action", "updateReaded");
                    req.addProperty("memberId", memberId);
                    resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
                    if (!resp.get("result").getAsBoolean()) {
                        Toast.makeText(activity, "狀態異常,請重新進入", Toast.LENGTH_SHORT).show();
                        tvNotificationNote.setVisibility(View.VISIBLE);
                        tvNotificationNote.setText("狀態異常");
                        rvNotification.setVisibility(View.GONE);
                    }
            }
        }
        else{
            Toast.makeText(activity, "請檢察網路狀態", Toast.LENGTH_SHORT).show();
        }
    }
}