package idv.tfp10105.project_forfun.membercenter.Notification;

import android.os.Message;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import idv.tfp10105.project_forfun.MainActivity;
import idv.tfp10105.project_forfun.chatroom.ChatMessageFragment;

public class NotificaitonFCMService extends FirebaseMessagingService{
    //在前景執行時會呼叫(背景時不會)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = "";
        String body = "";
        if (notification != null) {
            title = notification.getTitle();
//           body =  notification.getBody();
//           Log.d("顯示FirebaseService data",remoteMessage.getData().toString());
        }
        Message msg = new Message();
       if(notification==null|| title.equals("新通知")) {
//           Log.d("FirebaseService","新通知");
           //自定義消息代碼
           msg.what = 1;
           //要傳送的物件
//        msg.obj=1;
           if(MainActivity.handler!=null) {
               //主執行緒才能控制元件
               MainActivity.handler.sendMessage(msg);
           }
       }

       else if(title.equals("系統通知")) {
//           Log.d("FirebaseService","系統通知");
           msg.what = 2;
           if(MainActivity.handler!=null) {
               MainActivity.handler.sendMessage(msg);
           }
       }
       else  {

           //自定義消息代碼
           msg.what = 2;
           //要傳送的物件
//        msg.obj=1;
           //主執行緒才能控制元件
           if (ChatMessageFragment.handler != null) {
               ChatMessageFragment.handler.sendMessage(msg);
           }
//           Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
       }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        Log.d("顯示裝置Token",token);

    }
}
