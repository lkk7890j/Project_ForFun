package idv.tfp10105.project_forfun.membercenter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.RemoteAccess;

import static android.app.Activity.RESULT_OK;


public class MemberCenterPersonalInformationFragment extends Fragment {
    private Activity activity;
    private Uri contentUri;
    private Member member;
    //BottomSheet的元件
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Button btPickpic,btTakepic,btCancel;
    private ImageButton btPIEdit, btPIApply;
    private EditText etNameL,etNameF, etId, etBirthday, etPhone, etMail,etAddress;
    private ImageView ivHeadshot, ivIdPicF, ivIdPicB, ivGoodPeople;
    private TextView tvGoodPeople,tvGoodPeopleNote,tvRole,GPNote,HSNote;
    private RadioButton rbMan, rbWoman;
    private ScrollView scrollView;
    //判斷點擊哪個按鈕
    private int btPIEditClick = 0; // 0->編輯個人資料 1->完成(編輯資料) 2->完成(申請房東)
    private int btPIApplyClick = 0;  // 0->申請成房東 1->取消
    //判斷上傳點擊哪個按鈕
    private boolean HSisClick=false;
    private boolean GPisClick=false;
    //判斷是否有成功上傳
    private boolean upNewHS=false;
    private boolean upNewGP=false;
    private SimpleDateFormat sdf;
    private FirebaseStorage storage;
    private String picUri; //回傳路徑用
    private ByteArrayOutputStream baos; //上傳用
    private String serverresp;
    private SharedPreferences sharedPreferences;
    private final String url = Common.URL + "memberCenterPersonalInformation";

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
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_membercenter_personalinformation, container, false);
        //bottomeSheet
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet,null);
        bottomSheetDialog.setContentView(bottomSheetView);
        ViewGroup parent = (ViewGroup) bottomSheetView.getParent();
        parent.setBackgroundResource(android.R.color.transparent);
        //------
        findView(view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 指定拍照存檔路徑
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
        contentUri = FileProvider.getUriForFile(
                activity, activity.getPackageName() + ".fileProvider", file);
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences", Context.MODE_PRIVATE);
        //跟後端提出請求
        JsonObject clientreq = new JsonObject();
        clientreq.addProperty("action", "getMember");
        int memberId=sharedPreferences.getInt("memberId",-1);
        clientreq.addProperty("member_id",memberId);
        serverresp = RemoteAccess.getJsonData(url, clientreq.toString());
        handleData();
        handleClick();

    }

    private void findView(View view) {
        btPIEdit = view.findViewById(R.id.btPIEdit);
        btPIApply = view.findViewById(R.id.btPIApply);
        etNameL = view.findViewById(R.id.etNameL);
        etNameF = view.findViewById(R.id.etNameF);
        etId = view.findViewById(R.id.etId);
        etBirthday = view.findViewById(R.id.etBirthday);
        etPhone = view.findViewById(R.id.etPhone);
        etMail = view.findViewById(R.id.etMail);
        etAddress=view.findViewById(R.id.etAddress);
        ivHeadshot = view.findViewById(R.id.ivHeadshot);
        ivIdPicF = view.findViewById(R.id.ivIdPicF);
        ivIdPicB = view.findViewById(R.id.ivIdPicB);
        ivGoodPeople = view.findViewById(R.id.ivGoodPeople);
        rbMan = view.findViewById(R.id.rbMan);
        rbWoman = view.findViewById(R.id.rbWoman);
        tvGoodPeople=view.findViewById(R.id.tvGoodPeople);
        tvGoodPeopleNote=view.findViewById(R.id.tvGoodPeopleNote);
        tvRole=view.findViewById(R.id.tvRole);
        scrollView=view.findViewById(R.id.scrollView);
        GPNote=view.findViewById(R.id.GPNote);
        HSNote=view.findViewById(R.id.HSNote);
        //bottomsheet
        btTakepic=bottomSheetView.findViewById(R.id. btTakepic);
        btPickpic=bottomSheetView.findViewById(R.id.btPickpic);
        btCancel=bottomSheetView.findViewById(R.id.btCancel);
    }

    private void handleData() {
        if (RemoteAccess.networkCheck(activity)) {
            //防沒連到伺服器閃退(會有空指標例外)
            if (serverresp.equals("error")) {
                Toast.makeText(activity, "與伺服器連線錯誤", Toast.LENGTH_SHORT).show();
                btPIEdit.setEnabled(false);
                btPIApply.setEnabled(false);
                return;
            }

            member = new Gson().fromJson(serverresp, Member.class);
            //整理回傳的資訊
            String name = member.getNameL() + member.getNameF();
            int gender = member.getGender();
            String id = member.getId();
            String phone = "0"+member.getPhone();
            String address=member.getAddress();
            sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
            String birthday = sdf.format(member.getBirthady());
            String role;
            if(member.getRole()==1){
                role="帳號權限:房客";
                if(member.getCitizen()!=null&&!member.getCitizen().isEmpty()){
                    role +="\t(申請房東資料審核中)";
                    tvGoodPeople.setVisibility(View.VISIBLE);
                    ivGoodPeople.setVisibility(View.VISIBLE);
                }
            }
            else if(member.getRole()==2){
                role="帳號權限:房客及房東";
                btPIApply.setVisibility(View.GONE);
                tvGoodPeople.setVisibility(View.VISIBLE);
                ivGoodPeople.setVisibility(View.VISIBLE);

            }
            else{
                role="帳號權限:";
            }
            //設定當前的欄位資料
            tvRole.setText(role);
            //信箱
            if(member.getMail()!=null) {
                etMail.setText(member.getMail());
            }
            //大頭貼
            if (member.getHeadshot() != null&&!member.getHeadshot().isEmpty()) {
                getImage(ivHeadshot, member.getHeadshot());
            }
            //良民證
            if(member.getCitizen() != null &&!member.getCitizen().isEmpty()) {
                getImage(ivGoodPeople, member.getCitizen());
            }
            //身分證
            if(member.getIdImgb()!=null&&member.getIdImgf()!=null&&!member.getIdImgb().isEmpty()&&!member.getIdImgf().isEmpty()){
                getImage(ivIdPicF, member.getIdImgf());
                getImage(ivIdPicB, member.getIdImgb());
            }
            //姓名合併一起
            etNameL.setText(name);
            //性別
            if (gender == 1) {
                rbMan.setChecked(true);
            } else if (gender == 2) {
                rbWoman.setChecked(true);
            }
            etId.setText(id);
            etPhone.setText(phone);
            etBirthday.setText(birthday);
            etAddress.setText(address);
        }
    }
    //下載Firebase storage的照片
    public void getImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024 * 6; //設定上限
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String errorMessage = task.getException() == null ? "" : task.getException().getMessage();
                        Toast.makeText(activity, "圖片取得錯誤", Toast.LENGTH_SHORT).show();
                        Log.d("顯示Firebase取得圖片的錯誤", errorMessage);

                    }
                });

    }
    //上傳Firebase storage的照片
    private String uploadImage(byte[] imageByte,String imgSort) {
        // 取得storage根目錄位置
        StorageReference rootRef = storage.getReference();
        //  回傳資料庫的路徑
        final String imagePath = getString(R.string.app_name) + "/Person/"+member.getPhone()+"/"+ imgSort;
        // 建立當下目錄的子路徑
        final StorageReference imageRef = rootRef.child(imagePath);
        // 將儲存在imageVIew的照片上傳
        imageRef.putBytes(imageByte)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("顯示Firebase上傳圖片的狀態","上傳成功");
                    } else {
                        String errorMessage = task.getException() == null ? "" : task.getException().getMessage();
                        Log.d("顯示Firebase上傳圖片的錯誤", errorMessage);
                    }
                });
        return imageRef.getPath();
    }


    private void handleClick() {
        btPIEdit.setOnClickListener(v -> {
            //編輯個人資料
            if (btPIEditClick == 0) {
                //變更按鈕
                btPIEditClick = 1;//完成編輯時的代碼
                btPIApplyClick = 1;//取消時的代碼
                btPIEdit.setBackgroundResource(R.drawable.bt_sure);
                btPIApply.setVisibility(View.VISIBLE);
                btPIApply.setBackgroundResource(R.drawable.bt_cancel);
                HSNote.setVisibility(View.VISIBLE);
                etNameL.setEnabled(true); //改姓
                etNameL.setText(member.getNameL());
                etNameF.setVisibility(View.VISIBLE); //顯示名的欄位
                etNameF.setEnabled(true);
                etNameF.setText(member.getNameF());
//                etId.setEnabled(true);
//                etBirthday.setEnabled(true);
//                etBirthday.setInputType(InputType.TYPE_NULL);
//                etPhone.setEnabled(true); //改電話
                etMail.setEnabled(true); //改email
                etAddress.setEnabled(true); //改address

            }
            //點擊完成(編輯的及申請房東)
            else if (btPIEditClick == 1||btPIEditClick == 2) {
                //判斷是否為編輯個人資料
                if(etNameF.getVisibility()==View.VISIBLE) {
                    if (etNameL.getText().toString().trim().isEmpty()) {
                        etNameL.setError("姓不可為空");
                        return;

                    } else if (etNameF.getText().toString().trim().isEmpty()) {
                        etNameF.setError("名字不可為空");
                        return;

                    } else if (etPhone.getText().toString().trim().isEmpty()) {
                        etPhone.setError("電話不可為空");
                        return;

                    } else if (etAddress.getText().toString().trim().isEmpty()) {
                        etAddress.setError("地址不可為空");
                        return;
                    }
                     else if (!etMail.getText().toString().trim().isEmpty()&&!android.util.Patterns.EMAIL_ADDRESS.matcher(etMail.getText().toString().trim()).matches()) {
                        etMail.setError("電子郵件格式不正確");
                        return;
                    }
                    else if (etPhone.getText().toString().trim().length()!=10) {
                        etPhone.setError("手機號碼格式不正確");
                        return;
                    }
                    member.setNameL(etNameL.getText().toString().trim());
                    member.setNameF(etNameF.getText().toString().trim());
                    member.setPhone(Integer.parseInt(etPhone.getText().toString().trim()));
                    member.setAddress(etAddress.getText().toString().trim());
                    member.setMail(etMail.getText().toString().trim());
                    //如果有更改上傳大頭貼圖片
                    if(upNewHS) {
                        baos = new ByteArrayOutputStream();
                        ((BitmapDrawable) ivHeadshot.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        picUri = uploadImage(baos.toByteArray(),"Headshot");
                        if(picUri.isEmpty()){
                            Toast.makeText(activity, "大頭貼上傳失敗請重新上傳", Toast.LENGTH_SHORT).show();
                            upNewHS=false;
                            return;
                        }
                        member.setHeadshot(picUri);
                        upNewHS=false;
                    }
                }
                //若點擊完成為申請成為房東
               if(btPIEditClick == 2){
                    //上傳良民證圖片
                    if (upNewGP) {
                        baos = new ByteArrayOutputStream();
                        ((BitmapDrawable) ivGoodPeople.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        picUri = uploadImage(baos.toByteArray(),"Citizen");
                        if(picUri.isEmpty()){
                            Toast.makeText(activity, "大頭貼上傳失敗請重新上傳", Toast.LENGTH_SHORT).show();
                            upNewGP = false;
                            return;
                        }
                        member.setCitizen(picUri);
                        //後面會復原
//                        upNewGP = false;
                    } else {
                        Toast.makeText(activity, "未更新良民證照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //轉成member物件
                String updateMember = new Gson().toJson(member);
                JsonObject clientreq=new JsonObject();
                clientreq.addProperty("action","updateMember");
                clientreq.addProperty("member",updateMember);
                serverresp = RemoteAccess.getJsonData(url, clientreq.toString());
                if(serverresp.equals("true")){
                    Toast.makeText(activity, "更新成功", Toast.LENGTH_SHORT).show();
                    String citizen=member.getCitizen()==null?"":member.getCitizen();
                    sharedPreferences.edit()
                            .putString("name",member.getNameL()+member.getNameF())
                            .putString("address",member.getAddress())
                            .putString("mail",member.getMail())
                            .putString("headshot",member.getHeadshot())
                            .putString("citizen",citizen)
                            .apply();

                    //------------------
                    //將頁面恢復參數變回預設
                    //如果是房客且有上傳良民證
                    if(member.getRole()==1) {
                        if (upNewGP) {
                                tvRole.setText("帳號權限:房客\t(申請房東資料審核中)");
                        }
                    }
                    //如果是房東
                    if(member.getRole()==2) {
                        btPIApply.setVisibility(View.GONE);
                    }
                    //恢復參數
                    btPIEditClick = 0;
                    btPIApplyClick = 0;
                    HSisClick=false;
                    GPisClick=false;
                    upNewHS=false;
                    upNewGP=false;
                    btPIEdit.setBackgroundResource(R.drawable.bt_edit);
                    btPIApply.setBackgroundResource(R.drawable.bt_apply);
                    etNameL.setEnabled(false); //改姓
                    String name=member.getNameL()+member.getNameF();
                    etNameL.setText(name);
                    etNameF.setVisibility(View.GONE); //顯示名的欄位
                    etMail.setEnabled(false); //改email
                    etAddress.setEnabled(false); //改address
                    tvGoodPeopleNote.setVisibility(View.GONE);
                    GPNote.setVisibility(View.GONE);
                    HSNote.setVisibility(View.GONE);
                    //------------------
                    //遇到bug的又不想解的解法
//                    Navigation.findNavController(v).popBackStack(R.id.MemberCenterPersonalInformationFragment,true);
//                    Navigation.findNavController(v)
//                            .navigate(R.id.MemberCenterPersonalInformationFragment);


                }
                else{
                    Toast.makeText(activity, "更新失敗", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btPIApply.setOnClickListener(v -> {
            //點選申請房東
            if (btPIApplyClick == 0) {
                btPIEditClick = 2;//申請房東的完成
                btPIApplyClick = 1;
                btPIApply.setBackgroundResource(R.drawable.bt_cancel); //改為取消圖片
                btPIEdit.setBackgroundResource(R.drawable.bt_sure); //改為確定圖片
                tvGoodPeople.setVisibility(View.VISIBLE);
                ivGoodPeople.setVisibility(View.VISIBLE);
                tvGoodPeopleNote.setVisibility(View.VISIBLE);
                scrollView.fullScroll(View.FOCUS_DOWN);
                GPNote.setVisibility(View.VISIBLE);
            }
            //點選取消
            else if (btPIApplyClick == 1) {
                Navigation.findNavController(v).popBackStack(R.id.meberCenterPersonalInformationFragment,true);
                Navigation.findNavController(v)
                        .navigate(R.id.meberCenterPersonalInformationFragment);

            }

        });
            ivHeadshot.setOnClickListener(v->{
                //編輯模式時及點選編輯時
                if(btPIEditClick==1) {
                    HSisClick = true;
                    bottomSheetDialog.show();
                }
            });
            ivGoodPeople.setOnClickListener(v->{
                //編輯模式
                if(btPIEditClick==2) {
                    GPisClick = true;
                    bottomSheetDialog.show();
                }
            });
            btPickpic.setOnClickListener(v->{
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPictureLauncher.launch(intent);
            });
            btTakepic.setOnClickListener(v->{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                try {
                    takePictureLauncher.launch(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(activity,"找不到相機應用程式", Toast.LENGTH_SHORT).show();
                }
            });
            btCancel.setOnClickListener(v->{
                bottomSheetDialog.dismiss();
                HSisClick=false;
                GPisClick=false;
            });
            bottomSheetDialog.setOnCancelListener(dialog -> {
                HSisClick=false;
                GPisClick=false;
            });



    }

    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
        //透過bottomsheet判斷即可
//        else{
//            HSisClick=false;
//            GPisClick=false;
//        }
    }


    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
        //透過bottomsheet判斷即可
//        else{
//            HSisClick=false;
//            GPisClick=false;
//        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        Intent cropIntent = UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .getIntent(activity);
        cropPictureLauncher.launch(cropIntent);
    }

    private void cropPictureResult(ActivityResult result) {
        if (result.getData() != null) {
            Uri resultUri = UCrop.getOutput(result.getData());

            try {
                //設定圖片用
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    bitmap = BitmapFactory.decodeStream(
                            activity.getContentResolver().openInputStream(resultUri));
                } else {
                    ImageDecoder.Source source =
                            ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                //點擊更新頭貼
                if(HSisClick){
                    ivHeadshot.setImageBitmap(bitmap);
                    //上傳大頭貼成功
                    upNewHS=true;
                    HSisClick=false;
                    bottomSheetDialog.dismiss();
                }
                //點擊更新良民證
                else if(GPisClick){
                    ivGoodPeople.setImageBitmap(bitmap);
                    //上傳良民證成功
                    upNewGP=true;
                    GPisClick=false;
                    bottomSheetDialog.dismiss();
                }

            } catch (IOException e) {
                Log.e("顯示cropPictureResult的錯誤", e.toString());

            }

        }
        //透過bottomsheet判斷即可
//        else{
//            HSisClick=false;
//            GPisClick=false;
//        }
    }
}