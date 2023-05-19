package idv.tfp10105.project_forfun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Member;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private Toolbar toolbar;
    private TextView tvNotification;
    private CircularImageView ivCircle;
    private SharedPreferences sharedPreferences;
    public static int notify = 0;
    private ImageButton btBell;//actionbar 中的ImageButton
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleView();
        sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        // Android 11 /R 之后创建 Handler 构造函数 Handler(Handler.Callback callback) 变更为 new Handler(Looper.myLooper(), callback)
        // Handler 允许我们发送延时消息，如果在延时消息未处理完，而此时 Handler 所在的 Activity 被关闭，但因为上述 Handler 用法则可能会导致内存泄漏。那么，在延时消息未处理完时，Handler 无法释放外部类 MainActivity 的对象，从而导致内存泄漏产生。
        // https://shoewann0402.github.io/2020/03/09/android-R-about-handler-change/
        handler = new Handler(Looper.myLooper(), msg -> {
            if (msg.what == 1) {
                handleNotificationCount();
            }
            else if (msg.what == 2) {
                int memberId=sharedPreferences.getInt("memberId",-1);
                if(memberId!=-1) {
                    //更新用戶資料
                    updateInfo(memberId,sharedPreferences);
                }
            }
            return true;
        });
        //開一個新執行緒控制小鈴鐺通知
//        handleNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleAccess();
        handleNotificationCount();
        //若非登入狀態就不執行
        int memberId=sharedPreferences.getInt("memberId",-1);
        if(memberId>0) {
            updateInfo(memberId,sharedPreferences);
        }

    }


    private void handleView() {
        navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        if (getSupportActionBar() != null) {
            btBell = findViewById(R.id.btBell);              //取得元件
            tvNotification = findViewById(R.id.tvNotification);
            ivCircle = findViewById(R.id.ivCircle);
            //點擊通知
            btBell.setOnClickListener(v -> {
                tvNotification.setVisibility(View.GONE);
                ivCircle.setVisibility(View.GONE);
                notify = 0;
                navController.navigate(R.id.notificationFragment);

            });
            //navController監聽器
            //設置actionbar title及顯示狀態
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                //設定標題
                toolbar.setTitle(Objects.requireNonNull(navController.getCurrentDestination()).getLabel());

                //顯示bottomNav的頁面(
                if (navController.getCurrentDestination().getId() == R.id.homeFragment ||
                        navController.getCurrentDestination().getId() == R.id.memberCenterFragment ||
                        navController.getCurrentDestination().getId() == R.id.discussionBoardFragment ||
                        navController.getCurrentDestination().getId() == R.id.publishFragment||
                        navController.getCurrentDestination().getId() == R.id.chatRoomFragment
                ) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                }

                //隱藏actionbar的頁面
                if (navController.getCurrentDestination().getId() == R.id.signinInFragment ||
                        navController.getCurrentDestination().getId() == R.id.registIntroductionFragment ||
                        navController.getCurrentDestination().getId() == R.id.registerFragment ||
                        navController.getCurrentDestination().getId() == R.id.signin_Guided_Tour_Fragment
                ) {
                    getSupportActionBar().hide();
                }
                //客服頁面
//                else if (navController.getCurrentDestination().getId() == R.id.customerServiceFragment) {
//                    int memberId=sharedPreferences.getInt(" memberId", -1);
//                    if (memberId== -1) {
//                        getSupportActionBar().hide();
//                    }
//                    else {
//                        Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
//                        getSupportActionBar().show();
//                    }
//                }
                else {
                    getSupportActionBar().show();
                }

                //設定顯示返回鍵及通知按鈕
                //通知頁面
                if (navController.getCurrentDestination().getId() == R.id.notificationFragment||
                        navController.getCurrentDestination().getId() == R.id.customerServiceFragment) {
                    getSupportActionBar().show();
                    btBell.setVisibility(View.INVISIBLE);//隱藏通知按鈕
                    tvNotification.setVisibility(View.INVISIBLE);//隱藏通知數量
                    ivCircle.setVisibility(View.INVISIBLE); //隱藏通知紅圈
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);//顯示返回鍵

                } else if (navController.getCurrentDestination().getId() == R.id.meberCenterPersonalInformationFragment ||
                        navController.getCurrentDestination().getId() == R.id.myFavoriteFragment ||
                        navController.getCurrentDestination().getId() == R.id.orderconfirm_mainfragment_ho ||
                        navController.getCurrentDestination().getId() == R.id.orderconfirm_mainfragment ||
                        navController.getCurrentDestination().getId() == R.id.myEvaluationnFragment ||
                        navController.getCurrentDestination().getId() == R.id.ocrHO_Publishing ||
                        navController.getCurrentDestination().getId() == R.id.publishDetailFragment ||
                        navController.getCurrentDestination().getId() == R.id.appointmentFragment ||
                        navController.getCurrentDestination().getId() == R.id.personalSnapshotFragment||
                        navController.getCurrentDestination().getId() == R.id.chatMessageFragment||
                        navController.getCurrentDestination().getId() == R.id.discussionDetailFragment||
                        navController.getCurrentDestination().getId() == R.id.orderconfirm_houseSnapshot||
                        navController.getCurrentDestination().getId() == R.id.orderconfirm_agreement||
                        navController.getCurrentDestination().getId() == R.id.discussionBoard_RentSeeking_ListFragment||
                        navController.getCurrentDestination().getId() == R.id.discussionInsertFragment ||
                        navController.getCurrentDestination().getId() == R.id.discussionUpdateFragment
                ) {
                    btBell.setVisibility(View.VISIBLE);// 顯示通知按鈕
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); //顯示返回鍵
                }
                //不需加返回鍵
                else {
                        btBell.setVisibility(View.VISIBLE); // 顯示鈴鐺
                        if (notify != 0) {
                            tvNotification.setVisibility(View.VISIBLE); //顯示通知數量
                            ivCircle.setVisibility(View.VISIBLE); //顯示通知紅圈

                        }
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                });
            }
        }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            //返回鍵
            if (item.getItemId() == android.R.id.home) {
                //popBackStack(getCurrentDestination().getId(), true);
                navController.popBackStack();
            }
            return true;
        }

        private void handleAccess () {
            //需使用者允許的權限
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO};
            //未同意的權限
            Set<String> permissionsRequest = new HashSet<>();
            //過濾權限狀態
            for (String permission : permissions) {
                int result = ContextCompat.checkSelfPermission(this, permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest.add(permission);
                }
            }
            //詢問權限 (跳出對話框)
            if (permissionsRequest.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissionsRequest.toArray(new String[permissionsRequest.size()]),
                        0);
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 0) {
                for (int index = 0; index < grantResults.length; index++) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        // 已同意權限的後續處理
                    } else {
                        Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show();
                        //讓使用者跳轉到應用程式設定開啟權限
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                    }
                }
            }
        }


        private void handleNotification () {
            //新建一個執行緒-子執行緒無法直接使用UI執行緒因此須依靠Handler
            //參考自https://codertw.com/android-%E9%96%8B%E7%99%BC/22997/#outline__1
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int memberId;
                    final String url = Common.URL + "NotificationController";
                    JsonObject req = new JsonObject();
                    while (true) {
                        memberId = sharedPreferences.getInt("memberId", -1);
                        //如果不是遊客
                        if (memberId != -1) {
                            //對伺服器發請求
                            req.addProperty("action", "getNotificationCouunt");
                            req.addProperty("memberId", memberId);
                            String resq = RemoteAccess.getJsonData(url, req.toString());
                            if (!resq.equals("error")) {
                                notify = Integer.parseInt(resq);
                            }
                            if (notify > 0) {
                                //修改UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNotification.setVisibility(View.VISIBLE);
                                        ivCircle.setVisibility(View.VISIBLE);
                                        tvNotification.setText(notify + "");

                                    }
                                });
                            }
                        }
                        //等待防止連續發請求
                        try {
                            //1000為1秒
                            Thread.sleep(10 * 1000);
                            Log.d("顯示通知服務", "通知服務執行中");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("顯示通知服務", "通知服務已停止");
                            return;
                        }
                    }
                }
            }).start();
        }

        public void handleNotificationCount () {
            int memberId;
            final String url = Common.URL + "NotificationController";
            JsonObject req = new JsonObject();
            memberId = sharedPreferences.getInt("memberId", -1);
            //如果不是遊客
            if (memberId != -1) {
                //對伺服器發請求
                req.addProperty("action", "getNotificationCouunt");
                req.addProperty("memberId", memberId);
                String resq = RemoteAccess.getJsonData(url, req.toString());
                if (!resq.equals("error")) {
                    notify = Integer.parseInt(resq);
                }
                if (notify > 0) {
                    tvNotification.setText(notify + "");
                    if (navController.getCurrentDestination().getId() != R.id.notificationFragment) {
                        tvNotification.setVisibility(View.VISIBLE);
                        ivCircle.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNotification.setVisibility(View.INVISIBLE);
                    ivCircle.setVisibility(View.INVISIBLE);
                    tvNotification.setText(0 + "");
                }
            }

        }

        public static void updateInfo(int memberId,SharedPreferences sharedPreferences){
           String url = Common.URL + "signInController";
            JsonObject req = new JsonObject();
            req.addProperty("action","updateInfo");
            req.addProperty("memberId",memberId);//X
            Member member=new Gson().fromJson(RemoteAccess.getJsonData(url,req.toString()),Member.class);
            sharedPreferences.edit()
                    .putInt("role", member.getRole())
                    .putInt("phone",member.getPhone())
                    .putInt("type", member.getType())
                    .apply();
        }
    }