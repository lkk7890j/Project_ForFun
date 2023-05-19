package idv.tfp10105.project_forfun.signin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import idv.tfp10105.project_forfun.MainActivity;
import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Member;


public class SignInFragment extends Fragment {
    private Activity activity;

    private TextView tvSendTheVerificationCode,tvResendCode, tvTourist,tvCode,tvTest;
    private EditText etPhone,etVerificationCode;
    private ImageView imageView;//快速登入
    private ImageButton btSignIn, btRegistered, btAssist;
    private String verificationId,phone;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private final String url = Common.URL + "signInController";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences",Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_signin, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TapPay Activity 跳轉過來（TappayActivity 430行），經過此頁跳轉回到首頁
        String navigate_TAPPAY = sharedPreferences.getString("TappayActivity","");
        if(navigate_TAPPAY.equals("ToHomeFragment")){
            //跳轉清除暫存
            sharedPreferences.edit().remove("TappayActivity");
//            Navigation.findNavController(view).navigate(R.id.homeFragment);
        }
        findViews(view);
        handleClick();

    }

    private void findViews(View view) {
        etPhone = view.findViewById(R.id.ed_phone); // 手機號碼輸入欄位
        etVerificationCode = view.findViewById(R.id.ed_Verification_code); // 驗證碼輸入欄位
        btSignIn = view.findViewById(R.id.signin_bt_Sign_in); // 按鈕 會員登入
        btSignIn.getBackground().mutate().setAlpha(125);
        btSignIn.setEnabled(false);
        btRegistered = view.findViewById(R.id.signin_bt_registered); // 按鈕 註冊
        btAssist = view.findViewById(R.id.signin_assist); // 按鈕 協助（右上角的問號）
        tvSendTheVerificationCode = view.findViewById(R.id.signin_tv_Send_the_verification_code); // 弱按鈕 發送驗證碼
        tvTourist = view.findViewById(R.id.signin_tv_tourist); // 弱按鈕 遊客登入
        tvResendCode= view.findViewById(R.id.tvResendCode);
        tvCode= view.findViewById(R.id.tvCode); // 驗證碼標題
        imageView= view.findViewById(R.id.imageView); // 快速登入
        tvTest= view.findViewById(R.id.tvTest); // 快速登入選電話
    }

    @Override
    public void onStart() {
        super.onStart();
        // 檢查電話號碼是否驗證成功過
        FirebaseUser user = auth.getCurrentUser();
        int memberId=sharedPreferences.getInt("memberId", -1);
        //判斷是否第一次開啟
        if (sharedPreferences.getBoolean("firstOpen", true)) {
            Navigation.findNavController(btSignIn)
                    .navigate(R.id.signin_Guided_Tour_Fragment);
            sharedPreferences.edit().putBoolean("firstOpen", false).apply();
        }
        //登入狀態
        else if (user != null||memberId!=-1) {
            //檢查帳號
            if (memberId > 0) {
                //重新開啟app時更新資料
                MainActivity.updateInfo(memberId,sharedPreferences);
                //先判斷是否超過一小時
//                if (new Date().getTime() - sharedPreferences.getLong("lastlogin", new Date().getTime()) > 60 * 60 * 1000) {
//                    JsonObject req = new JsonObject();
//                    req.addProperty("action", "clearToken");
//                    req.addProperty("memberId", sharedPreferences.getInt("memberId", -1));
//                    if(RemoteAccess.getJsonData(url, req.toString()).equals("error")) {
//                        Toast.makeText(activity, "請檢查伺服器連線狀態", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    Toast.makeText(activity, "離上次登入超過ㄧ小時", Toast.LENGTH_SHORT).show();
//                    auth.signOut();
//                    sharedPreferences.edit().clear().apply();
//                    sharedPreferences.edit()
//                            .putBoolean("firstOpen", false)
//                            .apply();
//                    return;
//                }
                //判斷帳號權限
                    JsonObject req = new JsonObject();
                    req.addProperty("action", "checkType");
                    req.addProperty("memberId", sharedPreferences.getInt("memberId", -1));
                    if(RemoteAccess.getJsonData(url, req.toString()).equals("error")) {
                        Toast.makeText(activity, "請檢查伺服器連線狀態", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //停權狀態
                    else if(Integer.parseInt(RemoteAccess.getJsonData(url,req.toString()))==0){
                        sharedPreferences.edit().clear().apply();
                        sharedPreferences.edit()
                                .putBoolean("firstOpen",false)
                                .apply();
                        auth.signOut();
                        Toast.makeText(activity, "帳號已被停權,請聯絡客服", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //帳號正常
                    //更新偏好設定檔
                    Navigation.findNavController(btSignIn)
                                .navigate(R.id.homeFragment);

                }
            }

    }

    private void handleClick() {
        //點選驗證手機號碼
        tvSendTheVerificationCode.setOnClickListener(v->{
            phone=etPhone.getText().toString().trim();
            if(phone.isEmpty()){
                etPhone.setError("電話號碼不可為空");
                return;
            }
            else if(phone.length()!=10){
                etPhone.setError("手機號碼格式錯誤");
                return;
            }
            tvCode.setVisibility(View.VISIBLE);
            etVerificationCode.setVisibility(View.VISIBLE);
            tvResendCode.setVisibility(View.VISIBLE);
            requestVerificationCode("+886" + phone);
        });
        //重新發送
        tvResendCode.setOnClickListener(v->{
            phone=etPhone.getText().toString().trim();
            if(phone.isEmpty()){
                etPhone.setError("電話號碼不可為空");
                return;
            }
            else if(phone.length()!=10){
                etPhone.setError("手機號碼格式錯誤");
                return;
            }

            resendVerificationCode("+886" + phone, resendToken);
        });
        //驗證登入
        btSignIn.setOnClickListener(v->{
            String verificationCode = etVerificationCode.getText().toString().trim();
            if (verificationCode.isEmpty()) {
                etVerificationCode.setError("驗證碼不可為空");
                return;
            }
            // 將應用程式收到的驗證識別代號(verificationId)與user輸入的簡訊驗證碼(verificationCode)送至Firebase
            verifyIDAndCode(verificationId, verificationCode);
        });

        //自動填入
        imageView.setOnClickListener(v -> {
            Toast.makeText(activity, "自動填入", Toast.LENGTH_SHORT).show();
            etPhone.setText("0921371162");;
            etVerificationCode.setText("123456");
        });


        //快速登入
        imageView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("略過電話驗證");
            String[] phones = {"0922877662","0921371162", "0924545884", "0952894963", "0960917393", "0929458421","0921526256","0930362802","0930553563","0916366024"};
            builder.setSingleChoiceItems(phones,-1,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            etPhone.setText(phones[which]);
                            phone = etPhone.getText().toString().trim();
                            etVerificationCode.setText("123456");
                            JsonObject req = new JsonObject();
                            req.addProperty("action", "checkRole");
                            req.addProperty("phone", phones[which]);
                            String resp = RemoteAccess.getJsonData(url, req.toString());
                            if (resp.equals("error")) {
                                Toast.makeText(activity, "與伺服器連線錯誤", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(resp) == 1) {
                                Toast.makeText(activity, "房客", Toast.LENGTH_SHORT).show();

                            } else if (Integer.parseInt(resp) == 2) {
                                Toast.makeText(activity, "房東", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "資料庫中沒有此手機號碼或是權限不符合", Toast.LENGTH_SHORT).show();
                            }
//                            dialog.dismiss();
                        }
                    });
            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    phoneSure();
                }
            });
            builder.setNegativeButton("取消", null);
            Window window = builder.show().getWindow();
            Button btSure = window.findViewById(android.R.id.button1);
            Button btCancel = window.findViewById(android.R.id.button2);
            btSure.setTextColor(getResources().getColor(R.color.black));
            btCancel.setTextColor(getResources().getColor(R.color.black));
            return true;
        });

        //快速登入2
        tvTest.setOnClickListener(v->{

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("選擇電話號碼");
            String[] phones = {"0922877662", "0924545884", "0952894963", "0960917393", "0929458421","0921526256","0930362802","0930553563","0916366024"};
            builder.setSingleChoiceItems(phones,-1,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    etPhone.setText(phones[which]);
                    etVerificationCode.setText("123456");
                    JsonObject req=new JsonObject();
                    req.addProperty("action","checkRole");
                    req.addProperty("phone",phones[which]);
                    String resp=RemoteAccess.getJsonData(url,req.toString());
                    if(resp.equals("error")){
                        Toast.makeText(activity, "與伺服器連線錯誤", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(resp)==1){
                        Toast.makeText(activity, "房客", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(resp)==2){
                        Toast.makeText(activity, "房東", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(activity, "資料庫中沒有此手機號碼", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", null);
            Window window = builder.show().getWindow();
            Button btSure = window.findViewById(android.R.id.button1);
            Button btCancel = window.findViewById(android.R.id.button2);
            btSure.setTextColor(getResources().getColor(R.color.black));
            btCancel.setTextColor(getResources().getColor(R.color.black));
        });

        btAssist.setOnClickListener(v->{
            Navigation.findNavController(v)
                    .navigate(R.id.customerServiceFragment);
        });

        btRegistered.setOnClickListener(v->{
            Navigation.findNavController(v)
                    .navigate(R.id.registIntroductionFragment);
        });
        //遊客
        tvTourist.setOnClickListener(v->{
            sharedPreferences.edit()
                .putInt("role",3)
                    .apply();
            Navigation.findNavController(v)
                    .navigate(R.id.homeFragment);
        });
    }

    private void requestVerificationCode(String phone) {
        auth.setLanguageCode("zh-Hant");
        PhoneAuthOptions phoneAuthOptions =
                PhoneAuthOptions.newBuilder(auth)
                        // 電話號碼，驗證碼寄送的電話號碼
                        .setPhoneNumber(phone)
                        // 驗證碼失效時間，設為60秒代表即使多次請求驗證碼，過了60秒才會發送第2次
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        // 監控電話驗證的狀態
                        .setCallbacks(verifyCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void resendVerificationCode(String phone,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions phoneAuthOptions =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(verifyCallbacks)
                        // 驗證碼發送後，verifyCallbacks.onCodeSent()會傳來token，
                        // user要求重傳驗證碼必須提供token
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void verifyIDAndCode(String verificationId, String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
        firebaseAuthWithPhoneNumber(credential);
    }

    private void firebaseAuthWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        //驗證碼驗證成功的動作
                        phoneSure();
                    } else {
                        Exception exception = task.getException();
                        //驗證錯誤或輸入錯誤
                        String message = exception == null ? "Sign in fail." : exception.getMessage();
                        Log.d("顯示驗證碼驗證錯誤",message);
                        // user輸入的驗證碼與簡訊傳來的不同會產生錯誤
                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            etVerificationCode.setError("驗證碼輸入錯誤");

                        }
                    }
                });
    }

    private void phoneSure() {
        if(RemoteAccess.networkCheck(activity)) {
            JsonObject req=new JsonObject();
            req.addProperty("action","singIn");
            req.addProperty("phone",phone);
            String resp=RemoteAccess.getJsonData(url,req.toString());
            if(resp.equals("error")){
                Toast.makeText(activity, "請檢查伺服器狀態", Toast.LENGTH_SHORT).show();
             return;
            }
            JsonObject respJson=new Gson().fromJson(resp,JsonObject.class);
            int pass=respJson.get("pass").getAsInt();
            if(pass==0) {
                Member member=new Gson().fromJson(respJson.get("imformation").getAsString(),Member.class);
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String token = task.getResult();
                            member.setToken(token);
                            sharedPreferences.edit()
                                    .putString("token",token)
                                    .apply();
//                            Log.d("顯示裝置的token", token);
                            req.addProperty("action","updateToken");
                            req.addProperty("member",new Gson().toJson(member));
                            RemoteAccess.getJsonData(url,req.toString());//不接回覆
                        }
                    }
                });

                String citizen=member.getCitizen()==null?"": member.getCitizen();
                sharedPreferences.edit()
                        .putInt("memberId",member.getMemberId())
                        .putInt("role", member.getRole())
                        .putString("name",member.getNameL()+member.getNameF())
                        .putInt("phone",member.getPhone())
                        .putString("headshot",member.getHeadshot())
                        .putInt("gender",member.getGender())
                        .putString("id", member.getId())
                        .putString("birthday",new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(member.getBirthady()))
                        .putString("address",member.getAddress())
                        .putString("mail", member.getMail())
                        .putInt("type", member.getType())
                        .putString("token",member.getToken())
                        .putString("idImgf", member.getIdImgf())
                        .putString("idImgd",member.getIdImgf())
                        .putString("citizen",citizen)
                        .putLong("lastlogin",new Date().getTime())//登入時間
                        .apply();
                handleNotificationCount(member.getMemberId());
                Navigation.findNavController(btSignIn)
                        .navigate(R.id.homeFragment);
            }
            else if(pass==1) {
                Toast.makeText(activity, "帳號已被停權,請聯絡客服", Toast.LENGTH_SHORT).show();
            }
            else if(pass==2){
                Toast.makeText(activity, "手機號碼錯誤", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(activity, "網路不可用", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleNotificationCount(int memberId) {
        TextView tvNotification;
        CircularImageView ivCircle;
        tvNotification = activity.findViewById(R.id.tvNotification);
        ivCircle = activity.findViewById(R.id.ivCircle);
        final String url = Common.URL + "NotificationController";
        JsonObject req = new JsonObject();
        //如果不是遊客
        if (memberId != -1) {
            //對伺服器發請求
            req.addProperty("action", "getNotificationCouunt");
            req.addProperty("memberId", memberId);
            String resq = RemoteAccess.getJsonData(url, req.toString());
            if (!resq.equals("error")) {
                MainActivity.notify = Integer.parseInt(resq);
            }
            if (MainActivity.notify > 0) {
                tvNotification.setText(MainActivity.notify + "");
            }
            else{
                tvNotification.setVisibility(View.INVISIBLE);
                ivCircle.setVisibility(View.INVISIBLE);
                tvNotification.setText(0+"");
            }
        }

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks verifyCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /** This callback will be invoked in two situations:
         1 - Instant verification. In some cases the phone number can be instantly
         verified without needing to send or enter a verification code.
         2 - Auto-retrieval. On some devices Google Play services can automatically
         detect the incoming verification SMS and perform verification without
         user action. */
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            Log.d("顯示驗證碼驗證完成", "onVerificationCompleted: " + credential);
        }

        /**
         * 發送驗證碼填入的電話號碼格式錯誤，或是使用模擬器發送都會產生發送錯誤，
         * 使用模擬器發送會產生下列執行錯誤訊息：
         * App validation failed. Is app running on a physical device?
         */
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e("顯示驗證碼錯誤", "onVerificationFailed: " + e.getMessage());

        }

        /**
         * The SMS verification code has been sent to the provided phone number,
         * we now need to ask the user to enter the code and then construct a credential
         * by combining the code with a verification ID.
         */
        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d("顯示驗證碼Id", "onCodeSent: " + id);
            verificationId = id;
            resendToken = token;
            btSignIn.getBackground().mutate().setAlpha(255);
            btSignIn.setEnabled(true);

        }

    };

}


