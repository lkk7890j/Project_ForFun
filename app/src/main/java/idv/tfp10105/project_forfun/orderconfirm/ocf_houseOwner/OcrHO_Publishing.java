package idv.tfp10105.project_forfun.orderconfirm.ocf_houseOwner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Publish;

public class OcrHO_Publishing extends Fragment {
    private Activity activity;
    private final String url = Common.URL + "publishListController";
    private SharedPreferences sharedPreferences;
    private RecyclerView rvPublishList;
    private TextView tvPublishlNote;
    private int phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ocr_h_o_publishing, container, false);
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        phone = sharedPreferences.getInt("phone", -1);
        rvPublishList = view.findViewById(R.id.rvPublishList);
        tvPublishlNote = view.findViewById(R.id.tvPublishlNote);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int memberId = sharedPreferences.getInt("memberId", -1);
        List<Publish> publishes;
        List<String> cityNames;
        JsonObject req = new JsonObject();
        req.addProperty("action", "getPublishList");
        req.addProperty("memberId", memberId);
        JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
        Type publishList = new TypeToken<List<Publish>>() {
        }.getType();
        Type cityNamesList = new TypeToken<List<String>>() {
        }.getType();
        publishes = new Gson().fromJson(resp.get("publishes").getAsString(), publishList);
        cityNames = new Gson().fromJson(resp.get("cityNames").getAsString(), cityNamesList);
        if (publishes.size() == 0) {
            rvPublishList.setVisibility(View.GONE);
            tvPublishlNote.setVisibility(View.VISIBLE);
            return;
        }
        rvPublishList.setLayoutManager(new LinearLayoutManager(activity));
        rvPublishList.setAdapter(new PublishlistAdapter(activity, publishes, cityNames, phone));

    }

    //----------------------------
    // Adapter
    public class PublishlistAdapter extends RecyclerView.Adapter<PublishlistAdapter.PublishlistHolder> {
        private Context context;
        private List<Publish> publishes;
        private List<String> cityNames;
        private int phone;


        public PublishlistAdapter(Context context, List<Publish> publishes, List<String> cityNames, int phone) {
            this.context = context;
            this.publishes = publishes;
            this.cityNames = cityNames;
            this.phone = phone;
        }

        @NonNull
        @Override
        public PublishlistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.publishlist_itemview, parent, false);
            return new PublishlistHolder(view);
        }

        @Override
        public int getItemCount() {
            return publishes == null ? 0 : publishes.size();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull PublishlistHolder holder, int position) {
            Publish publish = publishes.get(position);
            String city = cityNames.get(position);
            holder.ivPublishList.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
            getImage(holder.ivPublishList, publish.getTitleImg() == null ? "/" : publish.getTitleImg());
            holder.tvPLName.setText(publish.getTitle());
            holder.tvPLArea.setText("地區:" + city);
            holder.tvPLPing.setText("坪數:" + publish.getSquare() + "坪");
            holder.tvPLMoney.setText("$" + publish.getRent() + "/月");
            PopupMenu popupMenu = new PopupMenu(activity, holder.ivPublishMore, Gravity.END);
            popupMenu.inflate(R.menu.publishlist_menu);
            if (publish.getStatus() == 2) {
                popupMenu.getMenu().getItem(3).setVisible(false);//close
                popupMenu.getMenu().getItem(2).setVisible(true);//open
                popupMenu.getMenu().getItem(1).setVisible(true);//delete
                popupMenu.getMenu().getItem(0).setVisible(true);//edit
                holder.tvPLStatus.setText("下架中");
                holder.tvPLStatus.setTextColor(getResources().getColor(R.color.black));
                holder.llPublishList.setAlpha((float) 0.5);
            } else if (publish.getStatus() == 3) {
                popupMenu.getMenu().getItem(3).setVisible(true);//close
                popupMenu.getMenu().getItem(2).setVisible(false);//open
                popupMenu.getMenu().getItem(1).setVisible(true);//delete
                popupMenu.getMenu().getItem(0).setVisible(true);//edit
                holder.tvPLStatus.setText("刊登中");
                holder.tvPLStatus.setTextColor(getResources().getColor(R.color.red));
                holder.llPublishList.setAlpha(1);
            } else if (publish.getStatus() == 1) {
                popupMenu.getMenu().getItem(3).setVisible(false);//close
                popupMenu.getMenu().getItem(2).setVisible(true);//open
                popupMenu.getMenu().getItem(1).setVisible(false);//delete
                popupMenu.getMenu().getItem(0).setVisible(false);//edit
                holder.tvPLStatus.setText("出租中");
                holder.tvPLStatus.setTextColor(getResources().getColor(R.color.red));
                holder.llPublishList.setAlpha((float) 0.5);

            } else if (publish.getStatus() == 0) {
                popupMenu.getMenu().getItem(3).setVisible(true);//close
                popupMenu.getMenu().getItem(2).setVisible(false);//open
                popupMenu.getMenu().getItem(1).setVisible(true);//delete
                popupMenu.getMenu().getItem(0).setVisible(false);//edit
                holder.tvPLStatus.setText("停權");
                holder.tvPLStatus.setTextColor(getResources().getColor(R.color.black));
                holder.llPublishList.setAlpha((float) 0.5);
            }

            //跳轉詳細資訊
            holder.itemView.setOnClickListener(v -> {
                // 把ID帶到詳細頁面
                Bundle bundle = new Bundle();
                bundle.putInt("publishId", publish.getPublishId());

                Navigation.findNavController(v).navigate(R.id.publishDetailFragment, bundle);
            });
            //更多選單
            holder.ivPublishMore.setOnClickListener(v -> {
                popupMenu.setOnMenuItemClickListener(item -> {
                    //點擊的publishId
                    int publishId = publish.getPublishId();
                    if (item.getItemId() == R.id.publishEdit) {
                        //編輯頁面
                        Bundle bundle = new Bundle();
                        bundle.putInt("publishId", publish.getPublishId());

                        Navigation.findNavController(v).navigate(R.id.publishFragment, bundle);

                    } else if (item.getItemId() == R.id.publishDelete) {
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(activity);
                        final EditText etinput = new EditText(activity);
                        etinput.setInputType(InputType.TYPE_CLASS_NUMBER);
                        deleteDialog.setView(etinput);
                        deleteDialog.setTitle("刪除");  //設置標題
                        deleteDialog.setIcon(R.mipmap.ic_launcher_round); //標題前面那個小圖示
                        deleteDialog.setMessage("請輸入您的手機號碼"); //提示訊息
                        deleteDialog.setPositiveButton(R.string.sure, (dialog, which) -> {
                            if (etinput.getText().toString().trim().equals("0" + String.valueOf(phone))) {
                                JsonObject req = new JsonObject();
                                req.addProperty("action", "pubishDelete");
                                req.addProperty("publishId", publishId);
                                if (RemoteAccess.networkCheck(activity)) {
                                    JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
                                    if (resp.get("result").getAsBoolean()) {
                                        Navigation.findNavController(tvPublishlNote).popBackStack(R.id.ocrHO_Publishing, true);
                                        Navigation.findNavController(tvPublishlNote).navigate(R.id.ocrHO_Publishing);
                                        publishes.remove(position);
                                        cityNames.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, "刪除失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(context, "請檢察網路狀態", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "輸入錯誤", Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteDialog.setNegativeButton(R.string.cancel, null);
                        Window window = deleteDialog.show().getWindow();
                        Button btSure = window.findViewById(android.R.id.button1);
                        Button btCancel = window.findViewById(android.R.id.button2);
                        btSure.setTextColor(getResources().getColor(R.color.black));
                        btCancel.setTextColor(getResources().getColor(R.color.black));
                    } else if (item.getItemId() == R.id.publishOpen) {
                        if (RemoteAccess.networkCheck(activity)) {
                            JsonObject req = new JsonObject();
                            req.addProperty("action", "updateStatus");
                            req.addProperty("publishId", publishId);
                            req.addProperty("status", "open");
                            if (publish.getStatus() == 1){
                                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(activity);
                                deleteDialog.setTitle("開啟刊登單");  //設置標題
                                deleteDialog.setIcon(R.mipmap.ic_launcher_round); //標題前面那個小圖示
                                deleteDialog.setMessage("請確認此刊登單合約是否已結束？"); //提示訊息
                                deleteDialog.setPositiveButton(R.string.sure, (dialog, which) -> {
                                    JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
                                    if (resp.get("result").getAsBoolean()) {
                                        Toast.makeText(context, "刊登單已開啟", Toast.LENGTH_SHORT).show();
                                        publish.setStatus(3);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "刊登單開啟失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                deleteDialog.setNegativeButton(R.string.cancel, null);
                                Window window = deleteDialog.show().getWindow();
                                Button btSure = window.findViewById(android.R.id.button1);
                                Button btCancel = window.findViewById(android.R.id.button2);
                                btSure.setTextColor(getResources().getColor(R.color.black));
                                btCancel.setTextColor(getResources().getColor(R.color.black));
                            }
                            else{
                                JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
                                if (resp.get("result").getAsBoolean()) {
                                    Toast.makeText(context, "刊登單已開啟", Toast.LENGTH_SHORT).show();
                                    publish.setStatus(3);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "刊登單開啟失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            Toast.makeText(context, "請檢察網路狀態", Toast.LENGTH_SHORT).show();
                        }
                    } else if (item.getItemId() == R.id.publishClose) {
                        if (RemoteAccess.networkCheck(activity)) {
                            JsonObject req = new JsonObject();
                            req.addProperty("action", "updateStatus");
                            req.addProperty("publishId", publishId);
                            req.addProperty("status", "close");
                            JsonObject resp = new Gson().fromJson(RemoteAccess.getJsonData(url, req.toString()), JsonObject.class);
                            if (resp.get("result").getAsBoolean()) {
                                Toast.makeText(context, "刊登單已關閉", Toast.LENGTH_SHORT).show();
                                publish.setStatus(2);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "刊登單關閉失敗", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(context, "請檢察網路狀態", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                });
                popupMenu.show();
            });


        }

        class PublishlistHolder extends RecyclerView.ViewHolder {
            CardView cvPublishlist;
            LinearLayout llPublishList;
            TextView tvPLName, tvPLArea, tvPLMoney, tvPLPing, tvPLStatus;
            ImageView ivPublishList, ivPublishMore;

            public PublishlistHolder(@NonNull View itemView) {
                super(itemView);
                ivPublishList = itemView.findViewById(R.id.ivPublishList);
                tvPLName = itemView.findViewById(R.id.tvPLName);
                tvPLArea = itemView.findViewById(R.id.tvPLArea);
                tvPLMoney = itemView.findViewById(R.id.tvPLMoney);
                tvPLPing = itemView.findViewById(R.id.tvPLPing);
                tvPLStatus = itemView.findViewById(R.id.tvPLStatus);
                llPublishList = itemView.findViewById(R.id.llPublishList);
                ivPublishMore = itemView.findViewById(R.id.ivPublishMore);
                cvPublishlist = itemView.findViewById(R.id.cvPublishlist);
                tvPLName.setSelected(true);
            }
        }

        //下載Firebase storage的照片
        public void getImage(final ImageView imageView, final String path) {
            FirebaseStorage storage;
            storage = FirebaseStorage.getInstance();
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
//                            Toast.makeText(context, "圖片取得錯誤", Toast.LENGTH_SHORT).show();
                            Log.d("顯示Firebase取得圖片的錯誤", errorMessage);

                        }
                    });

        }
    }
}