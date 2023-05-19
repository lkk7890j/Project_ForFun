package idv.tfp10105.project_forfun.discussionboard.controller;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.KeyboardUtils;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Post;

import static android.app.Activity.RESULT_OK;

public class DiscussionInsertFragment extends Fragment {
    private static final String TAG = "TAG_dis_InsertFragment";
    private FragmentActivity activity;
    private EditText etTitle, etContext;
    private ImageButton insert_bt_push;
    private CircularImageView insert_bt_memberHead;
    private TextView insert_MemberName, insert_board;
    private Spinner insert_spinner;
    private String imagePath = "Project_ForFun/Discussion_insert/no_image.jpg";
    private FirebaseStorage storage;
    private byte[] image;
    private File file;
    private Uri contentUri;
    private ImageView insert_bt_picture;
    private boolean pictureTaken;
    private String url = Common.URL ;
    private SharedPreferences sharedPreferences;
    private String name, headshot;
    private Bundle bundle;
    private Integer memberId;
    private BottomSheetDialog bottomSheetDialog;


    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::takePictureResult);

    ActivityResultLauncher<Intent> pickPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::pickPictureResult);

    ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::cropPictureResult);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        storage = FirebaseStorage.getInstance();
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name","");
        headshot = sharedPreferences.getString("headshot", "");
        memberId = sharedPreferences.getInt("memberId",-1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discussion_insert, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleSpinner();
        handleInsert_bt_picture();
        handleFinishInsert();
        insert_MemberName.setText(name);
        showImage(insert_bt_memberHead ,headshot);

    }



    private void findViews(View view) {
        insert_bt_picture = view.findViewById(R.id.insert_bt_image);
        insert_bt_memberHead = view.findViewById(R.id.insert_bt_memberHead);
        insert_bt_push = view.findViewById(R.id.insert_bt_insert);
        insert_spinner = view.findViewById(R.id.insert_spinner);
        etTitle = view.findViewById(R.id.insert_et_title);
        etContext = view.findViewById(R.id.insert_et_context);
        insert_MemberName = view.findViewById(R.id.insert_memberName_text);
//        insert_board = view.findViewById(R.id.insert_board);

    }

    //控制spinner
    private String handleSpinner( ) {
        //index:0    index:1   index:2
        List<String> itemList = Arrays.asList("租屋交流", "知識問答", "需求單");
        //實例化Adapter物件，並設定選項的外觀
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemList);
        //設定展開時的外觀
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //設定Adapter
        insert_spinner.setAdapter(adapter);
        //設定預選選項
        insert_spinner.setSelection(0, false);

        //註冊/實作 選項被選取監聽器
        insert_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                insert_board.setText(insert_spinner.getSelectedItem().toString());

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                TextView errorText = (TextView) insert_spinner.getSelectedView();
                errorText.setError("請選擇板塊");
                //just to highlight that this is an error
                errorText.setTextColor(Color.RED);
                //changes the selected item text to this
                errorText.setText("請選擇板塊");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, new String[]{""});
                insert_spinner.setAdapter(adapter);

            }
        });
        return  insert_spinner.getSelectedItem().toString();
    }

    private void handleInsert_bt_picture() {

        //初始化BottomSheet
        bottomSheetDialog = new BottomSheetDialog(activity);
        //連結的介面
        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet, null);
        //自定義的三個按鈕
        Button btCancel = view.findViewById(R.id.btCancel);
        Button bt_pickPicture = view.findViewById(R.id.btPickpic);
        Button bt_takePicture = view.findViewById(R.id.btTakepic);
        //將介面載入至BottomSheet內
        bottomSheetDialog.setContentView(view);
        //取得BottomSheet介面設定
        ViewGroup parent = (ViewGroup) view.getParent();
        //將背景設為透明，否則預設白底
        parent.setBackgroundResource(android.R.color.transparent);

        //按鈕控制
        bt_takePicture.setOnClickListener(v -> {
            //開啟拍照
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //指定儲存路徑
            file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            file = new File(file, "picture.jpg");
            contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileProvider", file);
            //取得原圖
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            bottomSheetDialog.dismiss();
            try {
                takePictureLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "找不到相機應用程式", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });


        bt_pickPicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPictureLauncher.launch(intent);
        });

        insert_bt_picture.setOnClickListener((v)->{
            //顯示BottomSheet
            bottomSheetDialog.show();
        });

        btCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

        });

    }

    private void handleFinishInsert() {
        insert_bt_push.setOnClickListener(v -> {
            //取得user輸入的值
            String title = etTitle.getText().toString().trim();
            KeyboardUtils.hideKeyboard(activity);
            if (title.length() <= 0) {
                etTitle.setError("請輸入標題");
                return;
            }
            String context = etContext.getText().toString().trim();
            KeyboardUtils.hideKeyboard(activity);
            if (context.length() <= 0){
                etContext.setError("請輸入內文");
                return;
            }
            if (RemoteAccess.networkCheck(activity)) {
                //用json傳至後端
                url += "DiscussionBoardController";
                Post post = new Post(0, insert_spinner.getSelectedItem().toString(), memberId , title, context, imagePath);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "postInsert");
                jsonObject.addProperty("post", new Gson().toJson(post));
                int count;
                //執行緒池物件
                String result = RemoteAccess.getJsonData(url, jsonObject.toString());
                //新增筆數
                count = Integer.parseInt(result);
                //筆數為0
                if (count == 0) {
//                    Toast.makeText(activity, "新增失敗", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(activity, "新增成功", Toast.LENGTH_SHORT).show();

                    //傳板塊資料回去
                    bundle = new Bundle();
                    bundle.putString("board",insert_spinner.getSelectedItem().toString());
                    //抽掉頁面
                    Navigation.findNavController(v).popBackStack(R.id.discussionInsertFragment,true);
                    Navigation.findNavController(v).navigate(R.id.discussionBoardFragment,bundle);

                }
            } else {
                Toast.makeText(activity, "沒有網路連線", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            //呼叫截圖
            crop(contentUri);
        }
    }

    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
    }

    //來源圖路徑
    private void crop(Uri sourceImageUri) {
        //截完圖要放的路徑
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        //要儲存的URI
        Uri destinationUri = Uri.fromFile(file);
        //UCrop.of：給來源給目的並建立Intent物件
        Intent cropIntent = UCrop.of(sourceImageUri, destinationUri).getIntent(activity);
        //呼叫cropPictureLauncher 開啟Ucrop
        cropPictureLauncher.launch(cropIntent);
    }

    private void cropPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            //取出截完的結果圖
            Uri resultUri = UCrop.getOutput(result.getData());
            if (resultUri != null) {
                uploadImage(resultUri);
            }
        }
        bottomSheetDialog.dismiss();
    }

    private String uploadImage(Uri resultUri) {

        //取得根目錄
        StorageReference rootRef = storage.getReference();
        imagePath = getString(R.string.app_name) + "/Discussion_insert/" + System.currentTimeMillis();

        //建立當下目錄的子路徑
        final StorageReference imageRef = rootRef.child(imagePath);
        //將儲存照片上傳 檔案
        imageRef.putFile(resultUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "上傳成功";
                        Log.d(TAG, message);
                        Toast.makeText(activity, "上傳結果： " + message, Toast.LENGTH_SHORT).show();
                        //下載剛上傳的照片
                        downloadImage(imagePath);

                    } else {
                        String message = task.getException() == null ? "上傳失敗" : task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
        return imagePath;
    }

    private void downloadImage(String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024 * 10;
        StorageReference imageRef = storage.getReference().child(imagePath);
        //最多能暫存記憶體的量
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        image = task.getResult();
                        //轉bitmap呈現前端
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        insert_bt_picture.setImageBitmap(bitmap);
                    } else {
                        String message  = task.getException() == null ? "下載失敗" : task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        insert_bt_picture.setImageResource(R.drawable.no_image);
//                        Toast.makeText(activity, message , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 下載Firebase storage的照片並顯示在ImageView上
    private void showImage(final ImageView imageView, final String path) {
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
                                "下載失敗" + ": " + path : task.getException().getMessage() + ": " + path;
                        imageView.setImageResource(R.drawable.no_image);
                        Log.e(TAG, message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}


