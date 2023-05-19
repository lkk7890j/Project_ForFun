package idv.tfp10105.project_forfun.discussionboard.disboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.KeyboardUtils;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.TimeUtil;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.bean.Post;
import idv.tfp10105.project_forfun.common.bean.Posthome;
import idv.tfp10105.project_forfun.discussionboard.ItemDecoration;

public class DiscussionBoard_RentSeekingFragment extends Fragment {
    private final static String TAG = "TAG_RentSeekingFragment";
    private RecyclerView rv_seeking;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseStorage storage;
    //外部列表 第一頁 列表
    private List<Post> posts;
    private SearchView searchView;
    private FloatingActionButton bt_Add;
    private SharedPreferences sharedPreferences;
    private String name, headshot;
    private String signin_name, signin_headshot;
    private Post post;
    private List<Member> members;
    private Member member;
    private int memberId;
    private ImageButton dis_bt_needHistory;
    private List<Posthome> posthomeList;
    private boolean searchPost = false;
    private String newText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        storage = FirebaseStorage.getInstance();
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        signin_name = sharedPreferences.getString("name", "");
        signin_headshot = sharedPreferences.getString("headshot", "");
        memberId = sharedPreferences.getInt("memberId", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discussion_board_rent_seeking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleRecyclerView();
        handleBtAdd();
        handleSearchView();
        handleSwipeRefresh();
//        handleDis_bt_needHistory();
    }

    @Override
    public void onStart() {
        super.onStart();

        posts = getPosts();
        members = getMembers();

        posthomeList = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Posthome posthome = new Posthome();
            posthome.setPost(posts.get(i));
            posthome.setMember(members.get(i));
            posthomeList.add(posthome);
        }
        showPosts(posthomeList);
    }


    private void findViews(View view) {
        searchView = view.findViewById(R.id.searchView_dis);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_seek);
        rv_seeking = view.findViewById(R.id.rv_seeking);
        bt_Add = view.findViewById(R.id.dis_bt_Add);
        dis_bt_needHistory = view.findViewById(R.id.dis_bt_needHistory);
    }

    private void handleSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //開始動畫
            swipeRefreshLayout.setRefreshing(true);
            //重新載入recycleView
            showPosts(posthomeList);
            //結束動畫
            swipeRefreshLayout.setRefreshing(false);

        });
    }

    private void handleSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                KeyboardUtils.hideKeyboard(activity);
                //如果輸入條件為空字串,就顯示原始資料,否則就顯示收尋後結果
                if (newText.isEmpty()) {
                    showPosts(posthomeList);
                } else {
                    List<Posthome> searchPosts = new ArrayList<>();
                    //搜尋原始資料內有無包含關鍵字（不區分大小寫）
                    for (Posthome posthome : posthomeList) {
                        if (posthome.getPost().getPostTitle().toUpperCase().contains(newText.toUpperCase())) {
                            searchPosts.add(posthome);
                        }
                    }
                    showPosts(searchPosts);

                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }


    private void handleBtAdd() {
        //跳轉至新增頁面
        // 遊客不可使用

        bt_Add.setOnClickListener(v -> {
            int role = sharedPreferences.getInt("role", -1);
            if (role == 3) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("無法發文");
                dialog.setMessage("請先註冊為房客");
                dialog.setPositiveButton("確定", null);

                Window window = dialog.show().getWindow();
                // 修改按鈕顏色
                Button btnOK = window.findViewById(android.R.id.button1);
                btnOK.setTextColor(getResources().getColor(R.color.black));

                return;

            } else {
                Navigation.findNavController(v)
                        .navigate(R.id.action_discussionBoardFragment_to_discussionInsertFragment);
            }
        });

    }

    private void handleRecyclerView() {

        rv_seeking.setLayoutManager(new LinearLayoutManager(activity));
        rv_seeking.addItemDecoration(new ItemDecoration(30, activity));//30代表30dp
    }

    // 抓資料
    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "DiscussionBoardController";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("boardId", "需求單");
            jsonObject.addProperty("action", "getAll");

            JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()), JsonObject.class);
            Type listPost = new TypeToken<List<Post>>() {
            }.getType();


            //解析後端傳回資料
            posts = new Gson().fromJson(jsonIn.get("postList").getAsString(), listPost);

        } else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }

        return posts;
    }

//    private void handleDis_bt_needHistory() {
//        dis_bt_needHistory.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("post", post);
//            bundle.putSerializable("member",);
//            Navigation.findNavController(v).navigate(R.id.action_discussionBoardFragment_to_discussionBoard_RentSeeking_ListFragment, bundle);
//        });
//    }

    private Member getMemberByOwnerId(int ownerId) {
        Member membershot = null;

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "/memberCenterPersonalInformation";
            JsonObject request = new JsonObject();
            request.addProperty("action", "getMember");
            request.addProperty("member_id", ownerId);

            String jsonResule = RemoteAccess.getJsonData(url, new Gson().toJson(request));

            membershot = new Gson().fromJson(jsonResule, Member.class);
        }

        return membershot;
    }

    // 抓po文者資料
    private List<Member> getMembers() {

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "DiscussionBoardController";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("boardId", "需求單");
            jsonObject.addProperty("action", "getAll");
            JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()), JsonObject.class);
            Type listMember = new TypeToken<List<Member>>() {
            }.getType();

            //解析後端傳回資料
            members = new Gson().fromJson(jsonIn.get("memberList").getAsString(), listMember);

        } else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(activity, "members : " + members, Toast.LENGTH_SHORT).show();

        return members;
    }

    private void showPosts(List<Posthome> posthomeList) {

        if (posthomeList == null || posthomeList.isEmpty()) {
//            Toast.makeText(activity, "沒有貼文", Toast.LENGTH_SHORT).show();
        }
        //取得Adapter
        SeekAdapter seekAdapter = (SeekAdapter) rv_seeking.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (seekAdapter == null) {
            rv_seeking.setAdapter(new SeekAdapter(activity, posthomeList));
        } else {

            //更新Adapter資料,重刷
            seekAdapter.setAdapter(posthomeList);

            //重新執行RecyclerView 三方法
            seekAdapter.notifyDataSetChanged();
        }
    }


    public class SeekAdapter extends RecyclerView.Adapter<SeekAdapter.MyViewHolder> {
        private final LayoutInflater layoutInflater;
        private final int imageSize;
        //內部列表（搜尋後）
        private List<Post> posts;
        private List<Member> members;
        private List<Posthome> posthomeList;


        public SeekAdapter(Context context, List<Posthome> posthomeList) {
            layoutInflater = LayoutInflater.from(context);
            this.posthomeList = posthomeList;
            posts = new ArrayList<>();
            members = new ArrayList<>();
            for (int i = 0; i < posthomeList.size(); i++) {
                posts.add(posthomeList.get(i).getPost());
                members.add(posthomeList.get(i).getMember());
            }
            //螢幕寬度除以四當圖片尺寸
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        // 重刷RecyclerView畫面
        public void setAdapter(List<Posthome> posthomeList) {
            this.posthomeList = posthomeList;
            posts.clear();
            members.clear();
            for (int i = 0; i < posthomeList.size(); i++) {
                posts.add(posthomeList.get(i).getPost());
                members.add(posthomeList.get(i).getMember());
            }
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView disPostName, disPostTitle, disPostContext, disPostTime;
            ImageButton disPostBtMore;
            ImageView disPostImg;
            CircularImageView disPostMemberImg;

            MyViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                disPostTitle = itemView.findViewById(R.id.post_item_title_text);
                disPostName = itemView.findViewById(R.id.post_item_memberName_text);
                disPostContext = itemView.findViewById(R.id.post_item_context_text);
                disPostImg = itemView.findViewById(R.id.post_item_house_img);
                disPostBtMore = itemView.findViewById(R.id.post_item_bt_more);
                disPostMemberImg = itemView.findViewById(R.id.post_item_bt_memberhead);
                disPostTime = itemView.findViewById(R.id.post_item_time_text);
            }
        }

        @Override
        public int getItemCount() {
            return posts == null ? 0 : posts.size();
        }


        @NotNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.discussion_itemview, null, false);
            return new MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull @NotNull DiscussionBoard_RentSeekingFragment.SeekAdapter.MyViewHolder holder, int position) {
            searchView.getQuery().length();
            Log.d(TAG, "searchView" + searchView.getQuery().length());
            final Post post = posts.get(position);
            Member member2 = members.get(position);


            holder.disPostTitle.setText(post.getPostTitle());
            //TODO
            holder.disPostName.setText(member2.getNameL() + member2.getNameF());
            holder.disPostContext.setText(post.getPostContext());
            holder.disPostTime.setText("時間："+TimeUtil.getChatTimeStr(post.getCreateTime().getTime()));
            showImage(holder.disPostMemberImg, member2.getHeadshot());

            holder.disPostMemberImg.setOnClickListener(v -> {
                Member memberPersonal = getMemberByOwnerId(post.getPosterId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectUser", memberPersonal);
                Navigation.findNavController(v).navigate(R.id.personalSnapshotFragment, bundle);

            });

            String url = Common.URL + "DiscussionBoardController";
            int postId = post.getPostId();
            String imagePath = post.getPostImg();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getImage");
            jsonObject.addProperty("imagePath", imagePath);
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("imageSize", imageSize);
            String jsonImg = RemoteAccess.getJsonData(url, jsonObject.toString());

            //設定點擊事件
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("name", member2.getNameL() + member2.getNameF());
                bundle.putString("headshot", member2.getHeadshot());
                bundle.putString("boardId", post.getBoardId());
                bundle.putSerializable("post", post);
                //轉至詳細頁面
                Navigation.findNavController(v).navigate(R.id.action_discussionBoardFragment_to_discussionDetailFragment, bundle);
            });

            if (jsonImg != null) {
                showImage(holder.disPostImg, imagePath);
            } else {
                holder.disPostImg.setImageResource(R.drawable.no_image);
            }

            //如在收尋狀態下不要顯示MoreButton
            if (searchView.getQuery().length() == 0) {
                holder.disPostBtMore.setVisibility(View.VISIBLE);
            } else {
                holder.disPostBtMore.setVisibility(View.INVISIBLE);
            }

            holder.disPostBtMore.setOnClickListener(v -> {

                // 遊客不可用檢舉等...
                int role = sharedPreferences.getInt("role", -1);
                if (role == 3) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle("無法操作");
                    dialog.setMessage("請先註冊為房客");
                    dialog.setPositiveButton("確定", null);

                    Window window = dialog.show().getWindow();
                    // 修改按鈕顏色
                    Button btnOK = window.findViewById(android.R.id.button1);
                    btnOK.setTextColor(getResources().getColor(R.color.black));

                    return;
                } else {
                    //選單
                    PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);

                    if (memberId == post.getPosterId()) {

                        Log.d("posterId", ":" + post.getPosterId());
                        Log.d("memberId", ":" + memberId);

                        popupMenu.getMenu().getItem(0).setVisible(true);
                        popupMenu.getMenu().getItem(1).setVisible(true);
                        popupMenu.getMenu().getItem(2).setVisible(false);
                    } else {
                        popupMenu.getMenu().getItem(0).setVisible(false);
                        popupMenu.getMenu().getItem(1).setVisible(false);
                        popupMenu.getMenu().getItem(2).setVisible(true);
                    }

                    popupMenu.setOnMenuItemClickListener(item -> {

                        int itemId = item.getItemId();
                        //新增
                        if (itemId == R.id.update) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("post", post);
                            bundle.putString("name", member2.getNameL() + member2.getNameF());
                            bundle.putString("headshot", member2.getHeadshot());
                            Navigation.findNavController(v).navigate(R.id.action_discussionBoardFragment_to_discussionUpdateFragment, bundle);

                            //刪除
                        } else if (itemId == R.id.delete) {

                            if (RemoteAccess.networkCheck(activity)) {
                                JsonObject jsonDelete = new JsonObject();
                                jsonDelete.addProperty("action", "postDelete");
                                jsonDelete.addProperty("postId", post.getPostId());
                                int count;
                                String result = RemoteAccess.getJsonData(url, jsonDelete.toString());
                                count = Integer.parseInt(result);
                                if (count == 0) {
                                    Toast.makeText(activity, "刪除失敗", Toast.LENGTH_SHORT).show();
                                } else {
                                    posts.remove(post);
                                    members.remove(member2);
                                    SeekAdapter.this.notifyDataSetChanged();

                                    // 外面posts也必須移除選取的post
                                    int index = 0;
                                  for (int i = 0; i < posthomeList.size(); i++) {
                                      if (posthomeList.get(i).getPost() == post) {
                                          index = i;
                                          break;
                                      }
                                  }
                                    posthomeList.remove(index);
                                    DiscussionBoard_RentSeekingFragment.this.posts.remove(post);
                                    DiscussionBoard_RentSeekingFragment.this.members.remove(member2);
//                                storage.getReference().child(post.getPostImg()).delete()
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                Log.d(TAG, "照片已刪除");
//                                            } else {
//                                                String message = task.getException() == null ? "照片刪除失敗" + ": " + post.getPostImg() :
//                                                        task.getException().getMessage() + ": " + post.getPostImg();
//                                                Log.e(TAG, message);
//                                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });

                                    Toast.makeText(activity, "刪除成功", Toast.LENGTH_SHORT).show();
                                }

                            }
                            //檢舉
                        } else if (itemId == R.id.report) {
                            Bundle bundle = new Bundle();
                            // 檢舉者
                            bundle.putInt("WHISTLEBLOWER_ID", memberId);
                            //被檢舉者
                            bundle.putInt("REPORTED_ID", post.getPosterId());
                            //檢舉貼文
                            bundle.putInt("POST_ID", post.getPostId());
                            //檢舉項目
                            bundle.putInt("ITEM", 0);

                            Navigation.findNavController(v).navigate(R.id.reportFragment, bundle);

                        } else {

                            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    });
                    popupMenu.show();
                }


            });

            dis_bt_needHistory.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                bundle.putString("name", member2.getNameL() + member2.getNameF());
                bundle.putString("headshot", member2.getHeadshot());
                bundle.putString("boardId", post.getBoardId());

                Navigation.findNavController(v).navigate(R.id.action_discussionBoardFragment_to_discussionBoard_RentSeeking_ListFragment, bundle);
            });
        }

    }


    // 下載Firebase storage的照片並顯示在ImageView上
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
//                        Log.e(TAG, message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
