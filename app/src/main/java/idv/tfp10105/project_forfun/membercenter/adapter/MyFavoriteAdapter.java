package idv.tfp10105.project_forfun.membercenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Favorite;
import idv.tfp10105.project_forfun.common.bean.Publish;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.MyFavoriteHolder> {
    private Context context;
    private Activity activity;
    private List<Publish> publishes;
    private List<String> cityNames;
    private List<Favorite> favorites;
    private final String url= Common.URL+"favoriteController";

    public MyFavoriteAdapter(Context context, Activity activity, List<Publish> publishes, List<String> cityNames, List<Favorite> favorites) {
        this.context = context;
        this.activity = activity;
        this.publishes = publishes;
        this.cityNames = cityNames;
        this.favorites = favorites;
    }

    @Override
    public int getItemCount() {
        return favorites==null?0:favorites.size();
    }
    @NonNull
    @NotNull
    @Override
    public MyFavoriteHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.favoritelist_itemview,parent,false);
        return new MyFavoriteHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyFavoriteAdapter.MyFavoriteHolder holder, int position) {
        Favorite favorite=favorites.get(position);
        Publish publish=publishes.get(position);
        String city=cityNames.get(position);

        if(RemoteAccess.networkCheck(activity)){
            holder.ivFFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite));
            holder.ivFPublish.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
            getImage(holder.ivFPublish,publish.getPublishImg1()==null?"//":publish.getTitleImg());
            holder.tvFPulishName.setText(publish.getTitle());
            holder.tvFPulishArea.setText("地區:"+city);
            holder.tvFPulishPing.setText("坪數:"+publish.getSquare()+"坪");
            holder.tvFPulishMoney.setText("$"+publish.getRent()+"/月");
            holder.itemView.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putInt("publishId", publish.getPublishId());

                Navigation.findNavController(v).navigate(R.id.publishDetailFragment, bundle);
            });
            holder.ivFFavorite.setOnClickListener(v->{
                JsonObject req=new JsonObject();
                req.addProperty("action","remove");
                req.addProperty("removeId",favorite.getFavoriteId());
                JsonObject resp=new Gson().fromJson(RemoteAccess.getJsonData(url,req.toString()),JsonObject.class);
                if(resp.get("pass").getAsBoolean()){
//                  holder.ivFFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cancelfavorite));
                    Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show();
                    favorites.remove(position);
                    cityNames.remove(position);
                    publishes.remove(position);
                    this.notifyDataSetChanged();
                }
            });
        }
        else {
            Toast.makeText(context, "請檢察網路連線", Toast.LENGTH_SHORT).show();
        }

    }



    class MyFavoriteHolder extends RecyclerView.ViewHolder{
        LinearLayout lineralayout;
        ImageView ivFPublish,ivFFavorite;
        TextView tvFPulishName,tvFPulishArea,tvFPulishPing,tvFPulishMoney;
        public MyFavoriteHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivFPublish=itemView.findViewById(R.id.ivFPublish);
            ivFFavorite=itemView.findViewById(R.id.ivFFavorite);
            tvFPulishName=itemView.findViewById(R.id.tvFPulishName);
            tvFPulishName.setSelected(true);
            tvFPulishArea=itemView.findViewById(R.id.tvFPulishArea);
            tvFPulishPing=itemView.findViewById(R.id.tvFPulishPing);
            tvFPulishMoney=itemView.findViewById(R.id.tvFPulishMoney);
            lineralayout=itemView.findViewById(R.id.lineralayout);


        }
    }

    //下載Firebase storage的照片
    public void getImage(final ImageView imageView, final String path) {
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        final int ONE_MEGABYTE = 1024 * 1024 * 6; //設定上限
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String errorMessage = task.getException() == null ? "" : task.getException().getMessage();
                        Toast.makeText(context, "圖片取得錯誤", Toast.LENGTH_SHORT).show();
                        Log.d("顯示Firebase取得圖片的錯誤", errorMessage);

                    }
                });

    }
}
