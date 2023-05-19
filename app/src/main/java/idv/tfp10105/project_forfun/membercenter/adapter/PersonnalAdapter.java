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
import android.widget.RatingBar;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.bean.PersonEvaluation;

public class PersonnalAdapter extends RecyclerView.Adapter<PersonnalAdapter.PersonnalViewHolder>{
    private final String url = Common.URL +"personalSnapshot";
    private final Context context;
    private final Activity activity;
    private final List<PersonEvaluation> list;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",Locale.TAIWAN);


    public PersonnalAdapter(Context context, Activity activity, List<PersonEvaluation> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public PersonnalViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.personalsnapshot_itemview,parent,false);
        return new PersonnalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PersonnalAdapter.PersonnalViewHolder holder, int position) {
        PersonEvaluation personEvaluation=list.get(position);
        //設定顯示資料
        if(RemoteAccess.networkCheck(activity)){
            JsonObject clientreq=new JsonObject();
            clientreq.addProperty("action","getCommenter");
            clientreq.addProperty("commenterID",personEvaluation.getCommentedBy());
            String resp=RemoteAccess.getJsonData(url,clientreq.toString());
            Member member=new Gson().fromJson(resp,Member.class);
            getImage(holder.ivCommentByPS,member.getHeadshot());//設定頭像
            String name=member.getNameL()+member.getNameF();
            holder.tvCommentByPS.setText(name);//設定名字
            holder.ratingPS.setRating(personEvaluation.getPersonStar());//設定星數
            holder.tvCommentPS.setText(personEvaluation.getPersonComment());//設定評論內容
            holder.tvCommentTimePS.setText(sdf.format(personEvaluation.getCreateTime()));//設定評論建立時間
            holder.linearLayoutPS.setOnClickListener(v->{
                Bundle bundle=new Bundle();
                bundle.putSerializable("SelectUser",member);
                Navigation.findNavController(v)
                        .navigate(R.id.personalSnapshotFragment,bundle);
            });
        }
        else{
            Toast.makeText(context, "請檢察網路連線", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

      class PersonnalViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutPS;
        ImageView ivCommentByPS;
        TextView tvCommentByPS;
        TextView tvCommentPS;
        TextView tvCommentTimePS;
        RatingBar ratingPS;

        PersonnalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCommentByPS=itemView.findViewById(R.id.ivCommentByPS);
            tvCommentByPS=itemView.findViewById(R.id.tvCommentByPS);
            tvCommentPS=itemView.findViewById(R.id.tvCommentPS);
            ratingPS=itemView.findViewById(R.id.ratingPS);
            tvCommentTimePS=itemView.findViewById(R.id.tvCommentTimePS);
            linearLayoutPS=itemView.findViewById(R.id.linearLayoutPS);

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
