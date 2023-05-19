package idv.tfp10105.project_forfun.orderconfirm;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.OtherPay;

import static android.app.Activity.RESULT_OK;

public class Orderconfirm_otherpay extends Fragment {
    private TextView tvAccount, tvMsg, tvConfirmText, tvCancelText, tvTitle;
    private ImageView imgPic;
    private ImageButton btConfirm, btCancel;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private Activity activity;
    private int signInId, tapNum;
    private FirebaseStorage storage;
    // BottomSheet
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Button btTakePic_sheet, btPick_sheet, btCancel_sheet;
    private Uri contentUri;
    private Bitmap bitmap;


    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::takePictureResult);

    ActivityResultLauncher<Intent> pickPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::pickPictureResult);

    ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::cropPictureResult);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE); //共用的
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orderconfirm_otherpay, container, false);
        // BottomSheet
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        ViewGroup parent = (ViewGroup) bottomSheetView.getParent();
        parent.setBackgroundResource(android.R.color.transparent);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAccount = view.findViewById(R.id.tv_ocrOtherPay_account);
        tvCancelText = view.findViewById(R.id.tv_ocrOtherPay_canselText);
        tvConfirmText = view.findViewById(R.id.tv_ocrOtherPay_confirmText);
        tvMsg = view.findViewById(R.id.tv_ocrOtherPay_notes);
        imgPic = view.findViewById(R.id.img_ocrOtherPay_pic);
        btCancel = view.findViewById(R.id.bt_ocrOtherPay_cansel);
        btConfirm = view.findViewById(R.id.bt_ocrOtherPay_confirm);
        tvTitle = view.findViewById(R.id.tv_ocrOtherPay_Title);

        //bottomsheet
        btTakePic_sheet = bottomSheetView.findViewById(R.id.btTakepic);
        btPick_sheet = bottomSheetView.findViewById(R.id.btPickpic);
        btCancel_sheet = bottomSheetView.findViewById(R.id.btCancel);

        signInId = sharedPreferences.getInt("memberId", -1);

        Bundle bundle = getArguments();
        tapNum = bundle.getInt("OCR",-1);

        switch (tapNum) {
            case 15:
                handleViews();
                btConfirm.setVisibility(View.GONE);
                tvConfirmText.setText("");
                break;
            case 7: //房客預覽
                handlePreViews();
                break;
            case 17: //房東預覽 同 房客預覽
                handlePreViews();
                break;
            case 8: //下定前預覽 跳轉tappay
                handleTopayPreViews();
                break;

            default:
                Toast.makeText(activity, "查無內容", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
                break;
        }
    }

    private void handlePreViews() {
        Bundle bundle = getArguments();
        int otherpayId = bundle.getInt("OTHERPAYID");

        tvTitle.setText("付款明細");
        tvAccount.setEnabled(false);
        tvMsg.setEnabled(false);

        btConfirm.setVisibility(View.GONE);
        tvConfirmText.setText("");
        //取消按鈕
        btCancel.setVisibility(View.GONE);
        tvCancelText.setText("");
//        btCancel.setOnClickListener(v -> {
//            Bundle bundleOut = new Bundle();
//            bundleOut.putInt("OCR", tapNum);
//            Navigation.findNavController(v).navigate(R.id.orderconfirm_houseSnapshot, bundleOut);
//        });

        //抓值
        OtherPay otherPay = getotherpay(otherpayId);
        tvAccount.setText(String.valueOf(otherPay.getOtherpayMoney()));
        tvMsg.setText(otherPay.getOtherpayNote());
        String imgpath = otherPay.getSuggestImg();
        setImgFromFireStorage(imgpath,imgPic);
    }

    private void handleTopayPreViews() {
        Bundle bundle = getArguments();
        int otherpayId = bundle.getInt("OTHERPAYID");

        tvTitle.setText("付款明細");
        tvAccount.setEnabled(false);
        tvMsg.setEnabled(false);

        //去Tappay 付款
        btConfirm.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), TappayActivity.class);
            intent.putExtra("ORDERID", otherpayId);
            intent.putExtra("TAB", 2);
            startActivity(intent);
            Navigation.findNavController(v).popBackStack(R.id.orderconfirm_otherpay,true);
        });

        //取消按鈕
        btCancel.setVisibility(View.GONE);
        tvCancelText.setText("");

        //抓值
        OtherPay otherPay = getotherpay(otherpayId);
        tvAccount.setText(String.valueOf(otherPay.getOtherpayMoney()));
        tvMsg.setText(otherPay.getOtherpayNote());
        String imgpath = otherPay.getSuggestImg();
        setImgFromFireStorage(imgpath,imgPic);
    }

    private void handleViews() {
        tvTitle.setVisibility(View.GONE);
        btConfirm.setVisibility(View.GONE);
        tvConfirmText.setText("");

        imgPic.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });

        btConfirm.setOnClickListener(v -> {

            //檢查網路連線
            if (RemoteAccess.networkCheck(activity)) {
                String url = Common.URL + "OtherPay";

                // take value
                String msg = tvMsg.getText().toString().trim();
                String acc = tvAccount.getText().toString().trim();
                //轉型態int
                int account = Integer.parseInt(acc);
                Bundle bundle = getArguments();
                int agreementID = bundle.getInt("AGREEMENTID");

                // 上傳圖片給firebase，路徑存回db

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                byte[] imgbyte = out.toByteArray();
                String imgPath = getImgPath(imgbyte);

                // 存回db
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("ACCOUNT", account);
                jsonObject.addProperty("NOTE", msg);
                jsonObject.addProperty("SIGNINID", signInId);
                jsonObject.addProperty("AGREEMENTID", agreementID);
                jsonObject.addProperty("RESULTCODE", 1);
                jsonObject.addProperty("IMGPATH", imgPath);

                String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());

                JsonObject result = gson.fromJson(jsonIn, JsonObject.class);
                int resoltcode = result.get("RESULT").getAsInt();

                if (resoltcode == 200) {
                    Navigation.findNavController(v).navigate(R.id.homeFragment);
                    Toast.makeText(activity, "新增成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            }

        });

        // bottomsheet
        //相簿
        btPick_sheet.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPictureLauncher.launch(intent);
            bottomSheetDialog.dismiss();
        });
        //拍照
        btTakePic_sheet.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            file = new File(file, "tmp.jpg");
            contentUri = FileProvider.getUriForFile(
                    activity, activity.getPackageName() + ".fileProvider", file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            try {
                takePictureLauncher.launch(intent);
                bottomSheetDialog.dismiss();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "找不到相機應用程式", Toast.LENGTH_SHORT).show();
            }
        });
        //取消
        btCancel_sheet.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        //取消按鈕
        btCancel.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

    }


    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "tmp_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        Intent cropIntent = UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .getIntent(activity);
        cropPictureLauncher.launch(cropIntent);
    }

    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
    }

    private void cropPictureResult(ActivityResult result) {
        if (result.getData() != null) {
            Uri resultUri = UCrop.getOutput(result.getData());

            try {
                //設定圖片用
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    bitmap = BitmapFactory.decodeStream(
                            activity.getContentResolver().openInputStream(resultUri));
                } else {
                    ImageDecoder.Source source =
                            ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
                imgPic.setImageBitmap(bitmap);
                btConfirm.setVisibility(View.VISIBLE);
                tvConfirmText.setText("確認");

            } catch (IOException e) {
                Log.e("顯示cropPictureResult的錯誤", e.toString());
            }
        }
    }

    //上傳Firebase storage的照片
    private String getImgPath(byte[] imgbyte) {

        // 取得storage根目錄位置
        StorageReference rootRef = storage.getReference();
        Bundle bundle = getArguments();
        String agreement_Id = bundle.get("AGREEMENTID").toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //  回傳資料庫的路徑
        final String imagePath = getString(R.string.app_name) + "/OtherPay/" + agreement_Id + "/" + timeStamp;
        // 建立當下目錄的子路徑
        final StorageReference imageRef = rootRef.child(imagePath);

        imageRef.putBytes(imgbyte)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("顯示Firebase上傳圖片的狀態", "上傳成功");
                    } else {
                        String errorMessage = task.getException() == null ? "" : task.getException().getMessage();
                        Log.d("顯示Firebase上傳圖片的錯誤", errorMessage);
                    }
                });
        return imageRef.getPath();
    }

    //拿otherpay By otherpayId
    private OtherPay getotherpay(int otherpayId){
        OtherPay otherPay = new OtherPay();
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "OtherPay";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("OTHERPAYID", otherpayId);
            jsonObject.addProperty("RESULTCODE", 4);

            String jsonIn = RemoteAccess.getJsonData(url, jsonObject.toString());
            JsonObject object = gson.fromJson(jsonIn, JsonObject.class);

            String tmp = object.get("OTHERPAY").getAsString();
            otherPay = gson.fromJson(tmp,OtherPay.class);

            int resoltcode = object.get("RESULT").getAsInt();
            if (resoltcode == 200) {
                return otherPay;
            }else{
                Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //下載firebase 照片
    private void setImgFromFireStorage(final String imgPath, final ImageView showImg) {
        StorageReference imgRef = storage.getReference().child(imgPath);
        final int ONE_MEGBYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGBYTE).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                byte[] bytes = task.getResult();
                Bitmap bitmapPc = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                showImg.setImageBitmap(bitmapPc);
            } else {
                String message = task.getException() == null ?
                        "ImgDownloadFail" + ": " + imgPath :
                        task.getException().getMessage() + ": " + imgPath;
                //Log.e("updateFragment", message);
            }
        });
    }

}