package idv.tfp10105.project_forfun.discussionboard.disboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Post;
import idv.tfp10105.project_forfun.discussionboard.ItemDecoration;

public class DiscussionBoard_RentSeeking_ListFragment extends Fragment {
    private final static String TAG = "TAG_RentSeekingFragmentList";
    private Post post;
    private RecyclerView rv_rentseekinglist;
    private Activity activity;
    private FirebaseStorage storage;
    private List<Post> posts;
    private SharedPreferences sharedPreferences;
    private int memberId;
    private String name, headshot, boardId, signin_name, signin_headshot;
    private TextView historyRentSeekText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        storage = FirebaseStorage.getInstance();
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences", Context.MODE_PRIVATE);
        memberId = sharedPreferences.getInt("memberId" , -1);
        post = (Post) (getArguments() != null ? getArguments().getSerializable("post") : null);
        name = getArguments() != null ? getArguments().getString("name") : null;
        headshot = getArguments() != null ? getArguments().getString("headshot") : null;
        boardId = getArguments() != null ? getArguments().getString("boardId") : null;
        signin_name = sharedPreferences.getString("name", "");
        signin_headshot = sharedPreferences.getString("headshot", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discussion_board__rent_seeking__list, container, false);
        historyRentSeekText = view.findViewById(R.id.historyRentSeekText);
        rv_rentseekinglist = view.findViewById(R.id.rv_rentseekinglist);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleRecycleView();
        posts = getPosts();
        showPosts(posts);
    }


    // 抓資料
    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "DiscussionBoardController";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getRentSeekList");
            jsonObject.addProperty("boardId","需求單");
            jsonObject.addProperty("memberId", memberId);

            JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()),JsonObject.class);
            Type listPost = new TypeToken<List<Post>>() {}.getType();


            //解析後端傳回資料
            posts = new Gson().fromJson(jsonIn.get("rentSeekList").getAsString(),listPost);

        } else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(activity, "posts : " + posts, Toast.LENGTH_SHORT).show();
        return posts;
    }



    private void showPosts(List<Post> posts) {
        if (posts == null || posts.isEmpty())  {
//            Toast.makeText(activity, "沒有貼文", Toast.LENGTH_SHORT).show();
            historyRentSeekText.setVisibility(View.VISIBLE);
            rv_rentseekinglist.setVisibility(View.INVISIBLE);
        }
        //取得Adapter
        SeekListAdapter seekListAdapter = (SeekListAdapter) rv_rentseekinglist.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (seekListAdapter == null) {
            rv_rentseekinglist.setAdapter(new SeekListAdapter(activity, posts));
        } else {
            //更新Adapter資料,重刷
            seekListAdapter.setAdapter(posts);

            //重新執行RecyclerView 三方法
            seekListAdapter.notifyDataSetChanged();
        }
    }

    private void handleRecycleView() {
        rv_rentseekinglist.setLayoutManager(new LinearLayoutManager(activity));
        rv_rentseekinglist.addItemDecoration(new ItemDecoration(10, activity));
    }


    public class SeekListAdapter extends RecyclerView.Adapter<SeekListAdapter.MyViewHolder> {
        private final LayoutInflater layoutInflater;
        private List<Post> posts;
        
        public SeekListAdapter(Context context, List<Post> posts) {
            layoutInflater = LayoutInflater.from(context);
            this.posts = posts;
        }

        // 重刷RecyclerView畫面
        public void setAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @Override
        public int getItemCount() {
            return posts == null ? 0 : posts.size();
        }

        @NonNull
        @Override
        public SeekListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.rentseeking_itemview, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SeekListAdapter.MyViewHolder holder, int position) {
            Post post2 = posts.get(position);
            Log.d("12333","post2: " + post2.getPostImg());
            showImage(holder.rentSeekImg, post2.getPostImg());
            holder.rentSeekingPostTitle.setText(post2.getPostTitle());
            holder.rentSeekingPostContext.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("name", signin_name);
                bundle.putString("headshot", signin_headshot);
                bundle.putString("boardId", boardId);
                bundle.putSerializable("post", post2);
                Navigation.findNavController(v).navigate(R.id.discussionDetailFragment,bundle);
            });
        }



        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView rentSeekImg;
            TextView rentSeekingPostTitle, rentSeekingPostContext;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                rentSeekImg = itemView.findViewById(R.id.rentSeekImg);
                rentSeekingPostTitle = itemView.findViewById(R.id.rentSeekingPostTitle);
                rentSeekingPostContext = itemView.findViewById(R.id.rentSeekingPostContext);

            }
        }
    }


    // 下載Firebase storage的照片並顯示在ImageView上
    @SuppressLint("LongLogTag")
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024 * 10;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                "下載失敗" + ": " + path : task.getException().getMessage() + ": " + path;
                        imageView.setImageResource(R.drawable.no_image);
                        Log.e(TAG, message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}