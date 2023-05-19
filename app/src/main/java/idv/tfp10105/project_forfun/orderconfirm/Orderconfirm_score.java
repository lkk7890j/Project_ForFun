package idv.tfp10105.project_forfun.orderconfirm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
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

public class Orderconfirm_score extends Fragment {
    private Activity activity;
    private RatingBar ratingBarP, ratingBarH;
    private ImageButton btConfirm, btCancel; //button
    private TextView tvHOmsg, tvmsg, tvbtConfirmText, tvHouseMsgText, tvHouseTitle, tvTitle, tvCancelText;
    private Bundle bundleOut = new Bundle();
    private int tapNum = -1, orderId = -1, signInId = -1;
    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences, mainsharedPreferences;
    private String url = Common.URL + "Evaluation";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        //宣告 偏號設定檔位置
        //sharedPreferences = activity.getSharedPreferences("OrderSharedPre", Context.MODE_PRIVATE); //自設
        mainsharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE); //共用的
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orderconfirm_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ratingBarP = view.findViewById(R.id.ratingBar_ocrScore_people);
        ratingBarH = view.findViewById(R.id.ratingBar_ocrScore_House);
        btConfirm = view.findViewById(R.id.bt_ocrScore_confirm);
        btCancel = view.findViewById(R.id.bt_ocrScore_cancel);
        tvbtConfirmText = view.findViewById(R.id.tv_ocrScore_confirmText);
        tvTitle = view.findViewById(R.id.tv_ocrScore_peopleTitle);
        tvHouseTitle = view.findViewById(R.id.tv_ocrScore_HouseTitle);
        tvHouseMsgText = view.findViewById(R.id.tv_ocrScore_house_Visibility);
        tvHOmsg = view.findViewById(R.id.tv_ocrScore_HouseMsg);
        tvmsg = view.findViewById(R.id.tv_ocrScore_peopleMsg);
        tvCancelText = view.findViewById(R.id.tv_ocrScore_cancelText);


//        orderId = sharedPreferences.getInt("ORDERID",-1);
//        tapNum = sharedPreferences.getInt("SCORETAB",-1);
        signInId = mainsharedPreferences.getInt("memberId", -1);

        Bundle bundleIn = getArguments();
        tapNum = bundleIn.getInt("SCORE", -1);
        orderId = bundleIn.getInt("ORDERID", -1);

        bundleOut.putInt("OCR", tapNum);

        tvCancelText.setText("");
        btCancel.setVisibility(View.GONE);
        //取消按鈕回上頁
        btCancel.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.orderconfirm_houseSnapshot, bundleOut);
        });

        // 判斷是否已評價過
        switch (tapNum) {
            case 5:
                // 房客
                if (isEvaluationExist(signInId,orderId)) {
                    Navigation.findNavController(view).popBackStack();
                    Toast.makeText(activity, "已完成評價", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    handleTenant();
                    break;
                }

            case 15:
                // 房東
                if (isEvaluationExist(signInId,orderId)) {
                    Navigation.findNavController(view).navigate(R.id.homeFragment);
                    Toast.makeText(activity, "已完成評價", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    handleOwner();
                    break;
                }
            default:
                Navigation.findNavController(view).popBackStack();
                Toast.makeText(activity, "已完成評價", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleTenant() {

        btConfirm.setOnClickListener(v -> {
            //檢查網路連線 person % house 需傳到不同地方存
            if (RemoteAccess.networkCheck(activity)) {

                // save person
                int countStarP = (int) ratingBarP.getRating();
                String msg_P = tvmsg.getText().toString().trim();
                // save house
                int countStarH = (int) ratingBarH.getRating();
                String msg_H = tvHOmsg.getText().toString().trim();


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("TYPECODE", 0); //0->房客 1-> 房東
                jsonObject.addProperty("STARS_P", countStarP);
                jsonObject.addProperty("MSG_P", msg_P);
                jsonObject.addProperty("STARS_H", countStarH);
                jsonObject.addProperty("MSG_H", msg_H);
                jsonObject.addProperty("ORDERID", orderId);
                jsonObject.addProperty("SIGNINID", signInId);
                String jsonIn_H = RemoteAccess.getJsonData(url, jsonObject.toString());

                JsonObject result = gson.fromJson(jsonIn_H, JsonObject.class);
                int resoltcode = result.get("RESULT").getAsInt();


                if (resoltcode == 200) {
                    Navigation.findNavController(v).navigate(R.id.homeFragment);
                } else {
                    Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleOwner() {
        ratingBarH.setVisibility(View.GONE);
        tvHouseTitle.setVisibility(View.GONE);
        tvHouseMsgText.setVisibility(View.GONE);
        tvHOmsg.setVisibility(View.GONE);
        tvTitle.setText("> 給房客評價：");

        btConfirm.setOnClickListener(v -> {

            //檢查網路連線
            if (RemoteAccess.networkCheck(activity)) {
                // take value
                int countStarP = (int) ratingBarP.getRating();
                String msg_HO = tvmsg.getText().toString().trim();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("TYPECODE", 1); //0->房客 1-> 房東
                jsonObject.addProperty("STARS_P", countStarP);
                jsonObject.addProperty("MSG_P", msg_HO);
                jsonObject.addProperty("ORDERID", orderId);
                jsonObject.addProperty("SIGNINID", signInId);
                String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());

                JsonObject result = gson.fromJson(jsonIn, JsonObject.class);
                int resoltcode = result.get("RESULT").getAsInt();

                if (resoltcode == 200) {
                    Navigation.findNavController(v).navigate(R.id.homeFragment);
                } else {
                    Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private boolean isEvaluationExist(int signinId, int orderId) {
        if (RemoteAccess.networkCheck(activity)) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("TYPECODE", 2);
            jsonObject.addProperty("SIGNINID", signinId);
            jsonObject.addProperty("ORDERID", orderId);

            String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());

            JsonObject result = gson.fromJson(jsonIn, JsonObject.class);
            int resoltcode = result.get("RESULT").getAsInt();
            boolean isExist = result.get("EXIST").getAsBoolean();

            if (resoltcode == 200) {
                return isExist == true ? true : false;
            } else {
                return false;
            }
        } else {
            Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}