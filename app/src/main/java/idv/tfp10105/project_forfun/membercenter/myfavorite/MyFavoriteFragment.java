package idv.tfp10105.project_forfun.membercenter.myfavorite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Favorite;
import idv.tfp10105.project_forfun.common.bean.Publish;
import idv.tfp10105.project_forfun.membercenter.adapter.MyFavoriteAdapter;


public class MyFavoriteFragment extends Fragment {
    private Activity activity;
    private RecyclerView rvMyFavorite;
    private TextView tvFavoriteNote;
    private SharedPreferences sharedPreferences;
    private List<Publish> publishes;
    private List<String> cityNames;
    private List<Favorite> favorites;
    private int memberId;
    private final String url= Common.URL+"favoriteController";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences", Context.MODE_PRIVATE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_membercenter_myfavorite, container, false);
       findView(view);
        handleData();
        handleView();
       return view;
    }


    private void handleData() {
        memberId=sharedPreferences.getInt("memberId",-1);
        JsonObject req=new JsonObject();
        req.addProperty("action","getFavorite");
        req.addProperty("memberId",memberId);
        String resp= RemoteAccess.getJsonData(url,req.toString());
        JsonObject jsonObject=new Gson().fromJson(resp,JsonObject.class);
        Type publishList=new TypeToken<List<Publish>>(){}.getType();
        Type cityList=new TypeToken<List<String>>(){}.getType();
        Type favoriteList=new TypeToken<List<Favorite>>(){}.getType();
        publishes=new Gson().fromJson(jsonObject.get("publishList").getAsString(),publishList);
        cityNames=new Gson().fromJson(jsonObject.get("cityName").getAsString(),cityList);
        favorites=new  Gson().fromJson(jsonObject.get("favoriteId").getAsString(),favoriteList);
        if(favorites.size()==0){
            rvMyFavorite.setVisibility(View.GONE);
            tvFavoriteNote.setVisibility(View.VISIBLE);
        }

    }

    private void findView(View view) {
        rvMyFavorite=view.findViewById(R.id.rvMyFavorite);
        tvFavoriteNote=view.findViewById(R.id.tvFavoriteNote);
    }

    private void handleView() {
        rvMyFavorite.setLayoutManager(new LinearLayoutManager(activity));
        rvMyFavorite.setAdapter(new MyFavoriteAdapter(activity,activity,publishes,cityNames,favorites));
    }
}