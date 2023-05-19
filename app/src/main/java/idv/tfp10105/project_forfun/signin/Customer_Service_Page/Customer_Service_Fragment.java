package idv.tfp10105.project_forfun.signin.Customer_Service_Page;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;


public class Customer_Service_Fragment extends Fragment {
    private Activity activity;
    private Resources resources;

    private EditText edName, edMail, edPhone, edMessage;
    private ImageButton btCallCustomerService, btSendEmail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        activity = getActivity();
        // 取得資源物件
        // 此物件可用來存取資源，和對資源做一些進階操作
        resources = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer__service_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

//
    private void findViews(View view) {
        edName = view.findViewById(R.id.customer_service_ed_name); // 姓名輸入欄位
        edMail = view.findViewById(R.id.customer_service_ed_mail); // 信箱輸入欄位
        edPhone = view.findViewById(R.id.customer_service_ed_phone); // 手機號碼輸入欄位
        edMessage = view.findViewById(R.id.customer_service_ed_message); // 留言輸入欄位
        btSendEmail = view.findViewById(R.id.customer_service_bt_sendEmail); // 按鈕 發送郵件

    }

    private void handleButton() {
        btSendEmail.setOnClickListener(v -> {
            // isEmpty() 是判斷長度的方法
            // 確認edName不可為空
            final String username = String.valueOf(edName.getText());
            if (username.isEmpty()) {
                edName.setError(resources.getString(R.string.textedNameRequired));
                return;
            }
            // 確認edMail不可為空
            final String mail = String.valueOf(edMail.getText());
            if (mail.isEmpty()) {
                edMail.setError(resources.getString(R.string.textedMailRequired));
                return;
            }
            // 確認edPhone不可為空
            final String phone = String.valueOf(edPhone.getText());
            if (phone.isEmpty()) {
                edPhone.setError(resources.getString(R.string.textedPhoneRequired));
                return;
            }
            // 確認edMessage不可為空
            final String message = String.valueOf(edMessage.getText());
            if (message.isEmpty()) {
                edMessage.setError(resources.getString(R.string.textMessageRequired));
                return;
            }

//            //轉Json 字串格式
//            JsonFromCallable jsonfromcallable = new JsonFromCallable(Common.URL,
//                    "{'username':"+ username +" 'Mail':"+ Mail +" 'Phone':"+ Phone +" 'Message':"+ Message +"}");


            if (RemoteAccess.networkCheck(activity)) {
                String url = Common.URL + "CUSTOMER_SERVICE_Servlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "CUSTOMER_SERVICE");
                jsonObject.addProperty("username", username);
                jsonObject.addProperty("mail", mail);
                jsonObject.addProperty("phone", phone);
                jsonObject.addProperty("message", message);
                String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());
                Log.d("HI",jsonIn);
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);
                if (jObject.get("status").getAsBoolean()) {
                    Toast.makeText(activity, "新增成功", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).popBackStack();
                } else {
                    Toast.makeText(activity, "新增失敗", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

