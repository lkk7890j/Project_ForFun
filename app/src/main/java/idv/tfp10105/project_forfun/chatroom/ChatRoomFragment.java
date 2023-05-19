package idv.tfp10105.project_forfun.chatroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.ChatRoom;
import idv.tfp10105.project_forfun.common.bean.ChatRoomMessage;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.discussionboard.ItemDecoration;

public class ChatRoomFragment extends Fragment {
    private static final String TAG = "ChatRoomFragment";
    private Activity activity;
    public RecyclerView rv_chat;
    public List<ChatRoom> chatRooms;
    public ChatRoom chatRoom;
    private SharedPreferences sharedPreferences, sharedPreferf;
    private int selectUserId;
    private String selectUserHeadShot, selectUserName, selectmember;
    private FirebaseStorage storage;
    private int memberId;
    private Member member;
    private Boolean choseMember;
    private TextView chatRoomText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        storage = FirebaseStorage.getInstance();



        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        memberId = sharedPreferences.getInt("memberId", -1);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 遊客不可用私訊
        int role = sharedPreferences.getInt("role", -1);
        if (role == 3) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("無法操作");
            dialog.setMessage("請先註冊為房客");
            dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Navigation.findNavController(view).navigate(R.id.memberCenterFragment);
                }
            });

            Window window = dialog.show().getWindow();
            // 修改按鈕顏色
            Button btnOK = window.findViewById(android.R.id.button1);
            btnOK.setTextColor(getResources().getColor(R.color.black));
            return;
        }
        chatRoomText = view.findViewById(R.id.chatRoomText);
        rv_chat = view.findViewById(R.id.rv_chat);
        chatRooms = getChatRooms();
        showChatRooms(chatRooms);
        handleRecyclerView();

    }


    // 抓資料
    public List<ChatRoom> getChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "ChatRoomController";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("receivedMemberId", memberId);
            JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()), JsonObject.class);
            Type listType = new TypeToken<List<ChatRoom>>() {
            }.getType();

            //解析後端傳回資料
            chatRooms = new Gson().fromJson(jsonIn.get("equalMember").getAsString(), listType);
        } else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(activity, "chatRooms : " + chatRooms, Toast.LENGTH_SHORT).show();
        return chatRooms;
    }


    // 抓資料
    private void showChatRooms(List<ChatRoom> chatRooms) {
        if (chatRooms == null || chatRooms.isEmpty()) {
            chatRoomText.setVisibility(View.VISIBLE);
            rv_chat.setVisibility(View.INVISIBLE);
        }
        //取得Adapter
        ChatRoomAdapter chatRoomAdapter = (ChatRoomAdapter) rv_chat.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (chatRoomAdapter == null) {
            rv_chat.setAdapter(new ChatRoomAdapter(activity, chatRooms));
        } else {
            //更新Adapter資料,重刷
            chatRoomAdapter.setAdapter(chatRooms);

            //重新執行RecyclerView 三方法
            chatRoomAdapter.notifyDataSetChanged();
        }
    }

    private void handleRecyclerView() {
        rv_chat.setLayoutManager(new LinearLayoutManager(activity));
        rv_chat.setAdapter(new ChatRoomAdapter(activity, chatRooms));
        rv_chat.addItemDecoration(new ItemDecoration(20, activity));
    }

    private Member getMember(ChatRoom chatRoom) {

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "ChatRoomController";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberId");
            if (memberId == chatRoom.getMemberId1()) {
                jsonObject.addProperty("MemberId2", chatRoom.getMemberId2());
            } else {
                jsonObject.addProperty("MemberId2", chatRoom.getMemberId1());
            }
            String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());

            //解析後端傳回資料
            member = new Gson().fromJson(jsonIn, Member.class);
        } else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(activity, "chatRooms : " + chatRooms, Toast.LENGTH_SHORT).show();
        return member;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {
        private final LayoutInflater layoutInflater;
        private List<ChatRoom> chatRooms;



        public ChatRoomAdapter(Context context, List<ChatRoom> chatRooms) {
            layoutInflater = LayoutInflater.from(context);
            this.chatRooms = chatRooms;
        }

        //重刷RecyclerView畫面
        public void setAdapter(List<ChatRoom> chatRooms) {
            this.chatRooms = chatRooms;
        }


        @Override
        public int getItemCount() {
            return chatRooms == null ? 0 : chatRooms.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = layoutInflater.inflate(R.layout.chat_room_itemview, parent, false);
            return new MyViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ChatRoom chatRoom2 = chatRooms.get(position);
            downloadImage(holder.chatRoomMemberImg, getMember(chatRoom2).getHeadshot());
            holder.chatRoom_memberName.setText(getMember(chatRoom2).getNameL() + getMember(chatRoom2).getNameF());

            holder.chatRoomItemView.setOnLongClickListener(v -> {
                //實例化AlertDialog.Builder物件
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                alertDialog.setTitle("是否刪除聊天室");
                // 設定確定按鈕-顯示文字及點擊監聽器
                alertDialog.setPositiveButton("確定", (dialog, which) -> {

                    if (RemoteAccess.networkCheck(activity)) {
                        String url = Common.URL + "ChatRoomController";
                        JsonObject jsonDelete = new JsonObject();
                        jsonDelete.addProperty("action", "chatRoomDelete");
                        jsonDelete.addProperty("chatRoomId", chatRoom2.getChatroomId());
                        int count;
                        String result = RemoteAccess.getJsonData(url, jsonDelete.toString());
                        count = Integer.parseInt(result);
                        if (count == 0) {
                            Toast.makeText(activity, "刪除失敗", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "刪除成功", Toast.LENGTH_SHORT).show();
                            chatRooms.remove(chatRoom2);
                            ChatRoomAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
                alertDialog.setNegativeButton("取消", (dialog, which) ->
                {
                    dialog.cancel();
                });

                // 設定是否可點擊對話框以外之處離開對話框
                alertDialog.setCancelable(false);
                // 顯示對話框
                Window window = alertDialog.show().getWindow();
                //設定對話框顏色
                Button btSure = window.findViewById(android.R.id.button1);
                Button btCancel = window.findViewById(android.R.id.button2);
                btSure.setTextColor(getResources().getColor(R.color.black));
                btCancel.setTextColor(getResources().getColor(R.color.black));

                return true;
            });

            holder.chatRoomItemView.setOnClickListener(v -> {

                member = getMember(chatRoom2);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectUser", member);
                bundle.putSerializable("chatRoom", chatRoom2);
                bundle.putInt("chatroomId", chatRoom2.getChatroomId());
                ;
                Navigation.findNavController(v).navigate(R.id.chatMessageFragment, bundle);


            });
            //判斷是否有Message
            List<ChatRoomMessage> chatRoomMessages = new ArrayList<>();
            if (RemoteAccess.networkCheck(activity)) {
                String url = Common.URL + "MessageController";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getMessage");
                jsonObject.addProperty("chatRoomId", chatRoom2.getChatroomId());
                jsonObject.addProperty("MemberId", memberId);


                JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()), JsonObject.class);
                Type listType = new TypeToken<List<ChatRoomMessage>>() {
                }.getType();


                //解析後端傳回資料
                chatRoomMessages = new Gson().fromJson(jsonIn.get("messageList").getAsString(), listType);
                if (chatRoomMessages.isEmpty()) {
                    holder.img_chatroom_circle.setVisibility(View.GONE);
                    holder.chat_room_number_circle_text.setText("");
                } else {
                    holder.chat_room_number_circle_text.setText(String.valueOf(chatRoomMessages.size()));
                }

            } else {
                Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
            }


        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircularImageView chatRoomMemberImg;
            TextView chatRoom_memberName;
            LinearLayout chatRoomItemView;
            ImageView img_chatroom_circle;
            TextView chat_room_number_circle_text;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                chatRoomMemberImg = itemView.findViewById(R.id.chatRoomMemberImg);
                chatRoom_memberName = itemView.findViewById(R.id.chatRoom_memberName);
                chatRoomItemView = itemView.findViewById(R.id.chatRoomItemView);
                chat_room_number_circle_text = itemView.findViewById(R.id.chat_room_number_circle_text);
                img_chatroom_circle = itemView.findViewById(R.id.img_chatroom_circle);

            }
        }


        // 下載Firebase storage的照片並顯示在ImageView上
        private void downloadImage(final ImageView imageView, final String path) {
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
                                    "Image download Failed" + ": " + path : task.getException().getMessage() + ": " + path;
                            imageView.setImageResource(R.drawable.no_image);
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}