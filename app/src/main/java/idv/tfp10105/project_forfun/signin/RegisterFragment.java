package idv.tfp10105.project_forfun.signin;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.bean.Member;
import idv.tfp10105.project_forfun.common.RemoteAccess;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {
    private Activity activity;
    private String sBundle = ""; //存傳過來的值
    private Member member;//註冊的會員資料
    private ImageButton btRgSubmit, btRgCancel;
    private EditText etRgNameL, etRgNameF, etRgId, etRgBirthday, etRgPhone, etRgMail, etRgAddress;
    private ImageView ivRgHeadshot, ivRgIdPicF, ivRgIdPicB, ivRgGoodPeople;
    private RadioButton rbRgMan, rbRgWoman;
    private TextView tvRgGoodPeople, rgTitle, rgGoodPeopleNote;
    //BottomSheet的元件
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Button btPickpic, btTakepic, btCancel;
    private FirebaseStorage storage;
    private String picUri; //回傳路徑用
    private ByteArrayOutputStream baos; //上傳用
    //拍照
    private Uri contentUri;
    //判斷是否上傳成功
    private boolean uploadHeadshot = false;
    private boolean uploadIdPicF = false;
    private boolean uploadIdPicB = false;
    private boolean uploadGoodPeople = false;
    //判斷上傳點選哪顆
    private boolean clickHeadshot = false;
    private boolean clickIdPicF = false;
    private boolean clickIdPicB = false;
    private boolean clickGoodPeople = false;
    private final String url = Common.URL + "/register";
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
        sBundle = getArguments() != null ? getArguments().getString("Apply") : "";
        // 指定拍照存檔路徑
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
        contentUri = FileProvider.getUriForFile(
                activity, activity.getPackageName() + ".fileProvider", file);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regist, container, false);
        //bottomeSheet
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        ViewGroup parent = (ViewGroup) bottomSheetView.getParent();
        parent.setBackgroundResource(android.R.color.transparent);
        //------
        findView(view);
        handleView();
        handleClick();
        return view;
    }




    private void findView(View view) {
        btRgSubmit = view.findViewById(R.id.btRgSubmit);
        btRgCancel = view.findViewById(R.id.btRgCancel);
        etRgNameL = view.findViewById(R.id.etRgNameL);
        etRgNameF = view.findViewById(R.id.etRgNameF);
        rbRgWoman = view.findViewById(R.id.rbRgWoman);
        rbRgMan = view.findViewById(R.id.rbRgMan);
        etRgPhone = view.findViewById(R.id.etRgPhone);
        etRgId = view.findViewById(R.id.etRgId);
        etRgBirthday = view.findViewById(R.id.etRgBirthday);
        etRgMail = view.findViewById(R.id.etRgMail);
        etRgAddress = view.findViewById(R.id.etRgAddress);
        ivRgHeadshot = view.findViewById(R.id.ivRgHeadshot);
        ivRgIdPicF = view.findViewById(R.id.ivRgIdPicF);
        ivRgIdPicB = view.findViewById(R.id.ivRgIdPicB);
        rgTitle = view.findViewById(R.id.rgTitle);
        tvRgGoodPeople = view.findViewById(R.id.tvRgGoodPeople);
        ivRgGoodPeople = view.findViewById(R.id.ivRgGoodPeople);
        rgGoodPeopleNote = view.findViewById(R.id.rgGoodPeopleNote);
        //bottomsheet
        btTakepic = bottomSheetView.findViewById(R.id.btTakepic);
        btPickpic = bottomSheetView.findViewById(R.id.btPickpic);
        btCancel = bottomSheetView.findViewById(R.id.btCancel);

    }

    private void handleView() {
        etRgBirthday.setInputType(InputType.TYPE_NULL);
        if (sBundle.equals("Landlord")) {
            rgTitle.setText("房東註冊頁面");
            tvRgGoodPeople.setVisibility(View.VISIBLE);
            ivRgGoodPeople.setVisibility(View.VISIBLE);
            rgGoodPeopleNote.setVisibility(View.VISIBLE);
        } else if (sBundle.equals("Tenant")) {
            rgTitle.setText("房客註冊頁面");
        }
    }

    private void handleClick() {
        // 快速填寫假資料
        rgTitle.setOnClickListener(v -> {
            etRgNameL.setError(null);
            etRgNameF.setError(null);
            etRgId.setError(null);
            etRgAddress.setError(null);
            etRgBirthday.setError(null);
            etRgPhone.setError(null);
            etRgMail.setError(null);
            etRgAddress.setError(null);

            if (sBundle.equals("Landlord")) {
                etRgNameL.setText("王");
                etRgNameF.setText("大明");
            } else {
                etRgNameL.setText("王");
                etRgNameF.setText("小明");
            }
            rbRgMan.setChecked(true);
            etRgId.setText("A123456789");
            etRgAddress.setText("台北市中山區南京東路三段219號5樓");
//            etRgBirthday.setText("1991/01/24");
//            etRgPhone.setText("0912345678");
//            etRgMail.setText("test@email.com");
            uploadHeadshot = true;
            uploadIdPicB = true;
            uploadIdPicF = true;
            if (sBundle.equals("Landlord")) {
                uploadGoodPeople = true;
            }
        });
        //改生日用
        etRgBirthday.setOnClickListener(v -> {
            Calendar m_Calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
            DatePickerDialog.OnDateSetListener datepicker = (view, year, month, dayOfMonth) -> {
                m_Calendar.set(Calendar.YEAR, year);
                m_Calendar.set(Calendar.MONTH, month);
                m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etRgBirthday.setText(sdf.format(m_Calendar.getTime()));
            };
            DatePickerDialog dialog = new DatePickerDialog(activity,
                    datepicker,
                    m_Calendar.get(Calendar.YEAR),
                    m_Calendar.get(Calendar.MONTH),
                    m_Calendar.get(Calendar.DAY_OF_MONTH));
            Calendar maxCalendar = Calendar.getInstance();
            maxCalendar.set(Calendar.YEAR, maxCalendar.get(Calendar.YEAR) - 18);
            dialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        });

        btRgSubmit.setOnClickListener(v -> {
            //如果是房客
            boolean status = true;
            if (etRgNameL.getText().toString().trim().isEmpty()) {
                etRgNameL.setError("姓不可為空");
                status = false;
            }
            if (etRgNameF.getText().toString().trim().isEmpty()) {
                etRgNameF.setError("名字不可為空");
                status = false;

            }
            if (etRgPhone.getText().toString().trim().isEmpty()) {
                etRgPhone.setError("電話不可為空");
                status = false;
            }
            if (etRgId.getText().toString().trim().isEmpty()) {
                etRgId.setError("身分證不可為空");
                status = false;
            }
            if (!rbRgMan.isChecked() && !rbRgWoman.isChecked()) {
                Toast.makeText(activity, "請選擇性別", Toast.LENGTH_SHORT).show();
            }
            if (etRgAddress.getText().toString().trim().isEmpty()) {
                etRgAddress.setError("地址不可為空");
                status = false;
            }
            if (etRgMail.getText().toString().trim().isEmpty()) {
                etRgMail.setError("郵件不可為空");
                status = false;
            }
            if (!etRgMail.getText().toString().trim().isEmpty()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etRgMail.getText().toString().trim()).matches()) {
                    etRgMail.setError("電子郵件格式不正確");
                    status = false;
                }
            }
            if (etRgPhone.getText().toString().trim().length() != 10) {
                etRgPhone.setError("手機號碼格式不正確");
                status = false;
            }
            if (etRgBirthday.getText().toString().trim().length() != 10) {
                etRgBirthday.setError("生日不可為空");
                status = false;
            }
            if (!uploadHeadshot) {
                Toast.makeText(activity, "請上傳大頭貼", Toast.LENGTH_SHORT).show();
                status = false;
            }
            if (!uploadIdPicF) {
                Toast.makeText(activity, "請上傳身分證(正面)", Toast.LENGTH_SHORT).show();
                status = false;
            }
            if (!uploadIdPicB) {
                Toast.makeText(activity, "請上傳身分證(反面)", Toast.LENGTH_SHORT).show();
                status = false;
            }
            //如果是房東
            if (sBundle.equals("Landlord")) {
                if (!uploadGoodPeople) {
                    Toast.makeText(activity, "請上傳良民證", Toast.LENGTH_SHORT).show();
                    status = false;
                }
            }
            //檢查點
            if (!status) {
                return;
            }
            member = new Member();
            member.setPhone(Integer.parseInt(etRgPhone.getText().toString().trim()));
            //先確認手機號碼是否重複
            JsonObject reqJson = new JsonObject();
            reqJson.addProperty("action", "checkPhone");
            reqJson.addProperty("member", new Gson().toJson(member));
            String resp = RemoteAccess.getJsonData(url, reqJson.toString());
            JsonObject respJson = new Gson().fromJson(resp, JsonObject.class);
            if (!respJson.get("pass").getAsBoolean()) {
                Toast.makeText(activity, "手機號碼已註冊", Toast.LENGTH_SHORT).show();
                return;
            }
            //沒重複再繼續執行
            member.setNameL(etRgNameL.getText().toString().trim());
            member.setNameF(etRgNameF.getText().toString().trim());
            if (rbRgMan.isChecked()) {
                member.setGender(1);
            } else if (rbRgWoman.isChecked()) {
                member.setGender(2);
            }
            member.setId(etRgId.getText().toString().trim());
            member.setAddress(etRgAddress.getText().toString().trim());
            member.setBirthady(Timestamp.valueOf(etRgBirthday.getText().toString().replace("/", "-") + " " + "00:00:00"));
            member.setMail(etRgMail.getText().toString().trim());
            //大頭照
            baos = new ByteArrayOutputStream();
            ((BitmapDrawable) ivRgHeadshot.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            picUri = uploadImage(baos.toByteArray(), "Headshot");
            member.setHeadshot(picUri);
            //身分證正面
            baos = new ByteArrayOutputStream();
            ((BitmapDrawable) ivRgIdPicB.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            picUri = uploadImage(baos.toByteArray(), "IdImgb");
            member.setIdImgb(picUri);
            //身分證反面
            baos = new ByteArrayOutputStream();
            ((BitmapDrawable) ivRgIdPicF.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            picUri = uploadImage(baos.toByteArray(), "IdImgf");
            member.setIdImgf(picUri);
            //如果是申請房東
            if (sBundle.equals("Landlord")) {
                //良民證
                baos = new ByteArrayOutputStream();
                ((BitmapDrawable) ivRgGoodPeople.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                picUri = uploadImage(baos.toByteArray(), "Citizen");
                member.setCitizen(picUri);
            }
            //將資料轉成JSON
            reqJson.addProperty("action", "register");
            reqJson.addProperty("member", new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(member));
            resp = RemoteAccess.getJsonData(url, reqJson.toString());
            respJson = new Gson().fromJson(resp, JsonObject.class);
            if (respJson.get("status").getAsBoolean()) {
                Toast.makeText(activity, "註冊成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).popBackStack(R.id.registerFragment, true);
                Navigation.findNavController(v)
                        .navigate(R.id.signinInFragment);
            } else {
                Toast.makeText(activity, "註冊失敗請聯繫管理員", Toast.LENGTH_SHORT).show();
            }

        });

        btRgCancel.setOnClickListener(v -> {
            //第二層
            Navigation.findNavController(v).popBackStack(R.id.registerFragment, true);
            //第一層
            Navigation.findNavController(v).popBackStack(R.id.registIntroductionFragment, true);
            Navigation.findNavController(v)
                    .navigate(R.id.registIntroductionFragment);

        });

        ivRgHeadshot.setOnClickListener(v -> {
            clickHeadshot = true;
            bottomSheetDialog.show();
        });
        ivRgIdPicB.setOnClickListener(v -> {
            clickIdPicB = true;
            bottomSheetDialog.show();
        });
        ivRgIdPicF.setOnClickListener(v -> {
            clickIdPicF = true;
            bottomSheetDialog.show();
        });
        ivRgGoodPeople.setOnClickListener(v -> {
            clickGoodPeople = true;
            bottomSheetDialog.show();
        });
        btPickpic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPictureLauncher.launch(intent);
        });
        btTakepic.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            try {
                takePictureLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "找不到相機應用程式", Toast.LENGTH_SHORT).show();
            }
        });
        btCancel.setOnClickListener(v -> {
            //關閉bottomsheet
            bottomSheetDialog.dismiss();
            clickHeadshot = false;
            clickIdPicF = false;
            clickIdPicB = false;
            clickGoodPeople = false;
        });
        bottomSheetDialog.setOnCancelListener(dialog -> {
            //關閉bottomsheet
            clickHeadshot = false;
            clickIdPicF = false;
            clickIdPicB = false;
            clickGoodPeople = false;
        });

    }

    //拍照
    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
    }

    //選取照片
    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
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
                //圖片處理
                //設定圖片顯示用
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
                if (clickHeadshot) {
                    ivRgHeadshot.setImageBitmap(bitmap);
                    uploadHeadshot = true;
                } else if (clickIdPicB) {
                    ivRgIdPicB.setImageBitmap(bitmap);
                    uploadIdPicB = true;
                } else if (clickIdPicF) {
                    ivRgIdPicF.setImageBitmap(bitmap);
                    uploadIdPicF = true;
                } else if (clickGoodPeople) {
                    ivRgGoodPeople.setImageBitmap(bitmap);
                    uploadGoodPeople = true;
                }
                //  ucrop編輯完成
                bottomSheetDialog.dismiss();
                clickHeadshot = false;
                clickIdPicF = false;
                clickIdPicB = false;
                clickGoodPeople = false;

            } catch (IOException e) {
                Log.e("顯示cropPictureResult的錯誤", e.toString());

            }

        }
    }

    //上傳Firebase storage的照片
    private String uploadImage(byte[] imageByte, String imgSort) {
        // 取得storage根目錄位置
        StorageReference rootRef = storage.getReference();
        //  回傳資料庫的路徑
        final String imagePath = getString(R.string.app_name) + "/Person/" + member.getPhone() + "/" + imgSort;
        // 建立當下目錄的子路徑
        final StorageReference imageRef = rootRef.child(imagePath);
        // 將儲存在imageVIew的照片上傳
        imageRef.putBytes(imageByte)
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
}