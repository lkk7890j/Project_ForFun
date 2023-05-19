package idv.tfp10105.project_forfun.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import idv.tfp10105.project_forfun.common.KeyboardUtils;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.TimeUtil;
import idv.tfp10105.project_forfun.common.bean.ChatRoom;
import idv.tfp10105.project_forfun.common.bean.ChatRoomMessage;
import idv.tfp10105.project_forfun.common.bean.Member;

public class ChatMessageFragment extends Fragment {
    private static final String TAG = "chatMessageFragment";
    private static final String TAG2 = "chatMessageFragment2";
    private  Activity activity;
    private CircularImageView memberImg;
    private TextView memberName;
    private EditText edMessage;
    private ImageButton btSend;
    private  RecyclerView rvChatMessage;
    private List<ChatRoomMessage> chatRoomMessages;
    private int chatRoomId, chatroomMemberId1, chatroomMemberId2;
    private SharedPreferences sharedPreferences;
    private  Integer memberId;
    private List<Member> members;
    private FirebaseStorage storage;
    private ChatRoomMessageAdapter chatRoomMessageAdapter;
    public static Handler handler;
    private String url = Common.URL + "MessageController";
    private Member selectUser;
    private ChatRoom chatRoom;
    private int messageReadType;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        storage = FirebaseStorage.getInstance();
        //取bundle資料
        chatRoomId = getArguments() != null ? getArguments().getInt("chatroomId") : null;
        chatroomMemberId1 = getArguments() != null ? getArguments().getInt("chatroomMemberId1") : null;
        chatroomMemberId2 = getArguments() != null ? getArguments().getInt("chatroomMemberId2") : null;
        selectUser = (Member) (getArguments() != null ? getArguments().getSerializable("selectUser") : null);
        chatRoom = (ChatRoom) (getArguments() != null ? getArguments().getSerializable("chatRoom") : null);

        //取偏好設定檔
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        memberId = sharedPreferences.getInt("memberId", -1);


//        sharedPref = activity.getSharedPreferences("selectUser", Context.MODE_PRIVATE);
//        selectUserId = sharedPref.getInt("selectUserId", -1);
//        selectUserHeadShot = sharedPref.getString("selectUserHeadShot","");

        ChatMessageFragment.handler = new Handler(Looper.myLooper(), msg -> {
            Log.d(TAG,"handler");
            if (msg.what == 2) {
                if (chatRoomMessageAdapter == null) {
                    chatRoomMessageAdapter = new ChatRoomMessageAdapter(activity, getChatRoomMessage());
                    return true;
                }
               chatRoomMessages = getChatRoomMessage();
               chatRoomMessageAdapter.updateData(chatRoomMessages);
                //更新數據和定位到最底部
               rvChatMessage.scrollToPosition(chatRoomMessageAdapter.getItemCount()-1);

                return true;
            }
            return true;
        });



        // 每次取得registration token就傳送至server儲存，
        // 因為當MyFCMService.onNewToken()傳送token至server時可能失敗，而導致server沒有token
//        getTokenSendServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        memberName.setText(selectUser.getNameL() + selectUser.getNameF());
        downloadImage(memberImg, selectUser.getHeadshot());
        handleRecycleView();
        chatRoomMessages = getChatRoomMessage();
        showChatRoomMessage(chatRoomMessages);
        handlebtSend();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatMessageFragment.handler = null;
    }

    private void findViews(View view) {
        memberImg = view.findViewById(R.id.chatRoomMemberImg);
        memberName = view.findViewById(R.id.chat_memberName);
        edMessage = view.findViewById(R.id.edit_message);
        btSend = view.findViewById(R.id.bt_message_send);
        rvChatMessage = view.findViewById(R.id.rv_chatMessage);
    }

    private void handleRecycleView() {
        rvChatMessage.setLayoutManager(new LinearLayoutManager(activity));
    }

    private  List<ChatRoomMessage> getChatRoomMessage() {
        List<ChatRoomMessage> chatRoomMessages = new ArrayList<>();
        if (RemoteAccess.networkCheck(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("chatRoomId", chatRoomId);
            jsonObject.addProperty("MemberId", memberId);


            JsonObject jsonIn = new Gson().fromJson(RemoteAccess.getJsonData(url, jsonObject.toString()), JsonObject.class);
            Type listType = new TypeToken<List<ChatRoomMessage>>() {}.getType();


            //解析後端傳回資料
            chatRoomMessages = new Gson().fromJson(jsonIn.get("messageList").getAsString(), listType);

        }else {
            Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
        }

        return chatRoomMessages;

    }



    private void handlebtSend() {
        btSend.setOnClickListener(v -> {
            String chatMSG = edMessage.getText().toString().trim();
            edMessage.setText("");
            //收起鍵盤
            KeyboardUtils.hideKeyboard(activity);
            if (chatMSG.length() <= 0) {
//                Toast.makeText(activity, "Message is invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            if (RemoteAccess.networkCheck(activity)) {

                    ChatRoomMessage chatRoomMessage = new ChatRoomMessage(0, chatRoomId, memberId, chatMSG);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "messageInsert");
                    jsonObject.addProperty("chatMessage", chatMSG);
                    jsonObject.addProperty("receivedMemberId", selectUser.getMemberId());
                    jsonObject.addProperty("MemberId", memberId);
                    jsonObject.addProperty("chatRoomMessage", new Gson().toJson(chatRoomMessage));

                    int count;
                    //執行緒池物件
                    String result = RemoteAccess.getJsonData(url, jsonObject.toString());
                    //新增筆數
                    count = Integer.parseInt(result);
                    //筆數為0
                    if (count == 0) {
//                        Toast.makeText(activity, "新增失敗", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(activity, "新增成功", Toast.LENGTH_SHORT).show();

                        chatRoomMessages = getChatRoomMessage();
                        chatRoomMessageAdapter.updateData(chatRoomMessages);
                        //更新數據和定位到最底部
                        rvChatMessage.scrollToPosition(chatRoomMessageAdapter.getItemCount()-1);

                    }
            }

        });
    }



    private  void showChatRoomMessage(List<ChatRoomMessage> chatRoomMessages) {
        Log.d(TAG2,"chatRoomMessages : " + chatRoomMessages.size());
        if (chatRoomMessages == null || chatRoomMessages.isEmpty()) {
//            Toast.makeText(activity, "沒有訊息", Toast.LENGTH_SHORT).show();
        }
        //取得Adapter
        chatRoomMessageAdapter = new ChatRoomMessageAdapter(activity, getChatRoomMessage());
            rvChatMessage.setAdapter(chatRoomMessageAdapter);
            
    }

    public class ChatRoomMessageAdapter extends RecyclerView.Adapter<ChatRoomMessageAdapter.MyViewHolder> {
        private final LayoutInflater layoutInflater;
        private List<ChatRoomMessage> chatRoomMessages;


        public ChatRoomMessageAdapter(Context context, List<ChatRoomMessage> chatRoomMessages) {
            layoutInflater = LayoutInflater.from(context);
            this.chatRoomMessages = chatRoomMessages;
        }

        public void setAdapter(List<ChatRoomMessage> chatRoomMessages) {
            this.chatRoomMessages = chatRoomMessages;
        }

        @Override
        public int getItemCount() {
            return chatRoomMessages == null ? 0 : chatRoomMessages.size();
        }

        @NonNull
        @Override
        public ChatRoomMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.chat_msg_content_itemview, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatRoomMessageAdapter.MyViewHolder holder, int position) {
            ChatRoomMessage chatRoomMessage = chatRoomMessages.get(position);

            if (memberId.equals(chatRoomMessage.getMemberId())) {
                String readStatus = !chatRoomMessage.getRead() ? "未讀" : "已讀";
                holder.chatRoom_message_ReadStatus_self.setText(readStatus);
                holder.chatRoom_message_context_self.setText(chatRoomMessage.getMsgChat());
                downloadImage(holder.chatRoomMemberImg, selectUser.getHeadshot());
                holder.chatRoom_message_CreatTime_self.setText((TimeUtil.getChatTimeStr(chatRoomMessage.getCreateTime().getTime())));
                holder.otherMessage.setVisibility(View.GONE);
                holder.selfMessage.setVisibility(View.VISIBLE);

            } else {
                downloadImage(holder.chatRoomMemberImg, selectUser.getHeadshot());
                holder.chatRoom_message_context.setText(chatRoomMessage.getMsgChat());
                holder.chatRoom_message_CreatTime.setText((TimeUtil.getChatTimeStr(chatRoomMessage.getCreateTime().getTime())));
                holder.selfMessage.setVisibility(View.GONE);
                holder.otherMessage.setVisibility(View.VISIBLE);

            }

        }

        public void updateData(List<ChatRoomMessage> chatRoomMessages) {
            this.chatRoomMessages = chatRoomMessages;
            chatRoomMessageAdapter.notifyDataSetChanged();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public CircularImageView chatRoomMemberImg;
            public TextView chatRoom_message_context, chatRoom_message_CreatTime, chatRoom_message_context_self, chatRoom_message_CreatTime_self, chatRoom_message_ReadStatus_self;
            public LinearLayout otherMessage;
            public LinearLayout selfMessage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                chatRoom_message_context_self = itemView.findViewById(R.id.chatRoom_message_context_self);
                chatRoom_message_CreatTime_self = itemView.findViewById(R.id.chatRoom_message_CreatTime_self);
                chatRoom_message_ReadStatus_self = itemView.findViewById(R.id.chatRoom_message_ReadStatus_self);
                chatRoomMemberImg = itemView.findViewById(R.id.chatRoomMemberImg);
                chatRoom_message_context = itemView.findViewById(R.id.chatRoom_message_context);
                chatRoom_message_CreatTime = itemView.findViewById(R.id.chatRoom_message_CreatTime);
                selfMessage = itemView.findViewById(R.id.self_message);
                otherMessage = itemView.findViewById(R.id.other_message);
            }
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
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}