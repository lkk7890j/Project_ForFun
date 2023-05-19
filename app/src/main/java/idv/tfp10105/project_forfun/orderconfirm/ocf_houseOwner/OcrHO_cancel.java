package idv.tfp10105.project_forfun.orderconfirm.ocf_houseOwner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Order;
import idv.tfp10105.project_forfun.common.bean.Publish;

public class OcrHO_cancel extends Fragment {
    private int TAPNUMBER = 16; //此頁面編號
    private int OrderStatusNumber = 6; //訂單流程的狀態編號
    private Activity activity;
    private RecyclerView recyclerView;
    private FirebaseStorage storage;
    private SharedPreferences sharedPreferences;
    private List<Order> orders;
    private int signInId;
    private Gson gson = new Gson();
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvHint;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreferences = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE); //共用的
        storage = FirebaseStorage.getInstance();
        orders = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ocr_h_o_cancel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHint = view.findViewById(R.id.tv_ocrHO_cancel_HintText);

        signInId = sharedPreferences.getInt("memberId", -1);

        recyclerView = view.findViewById(R.id.recycleview_ocrHO_cancel);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        //顯示adapter
        //showAlls();

        //更新的功能
        swipeRefreshLayout = view.findViewById(R.id.swipe_ocrHO_cancel);
        //下拉可更新，要配合ui元件
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //動畫開始
            swipeRefreshLayout.setRefreshing(true);
            showAlls();
            //動畫結束
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        showAlls();
    }

    //顯示全部
    private void showAlls() {
        if (!orders.isEmpty()) {
            orders.clear();
            orders = getOrderListInfo(OrderStatusNumber, signInId);
            if(orders.isEmpty()){
                tvHint.setText("尚未有訂單！");
            }else{
                setOrderList(orders);
            }
        } else {
            orders = getOrderListInfo(OrderStatusNumber, signInId);
            if(orders.isEmpty()){
                tvHint.setText("尚未有訂單！");
            }else{
                setOrderList(orders);
            }
        }
    }

    //拿後端對應資料
    private List<Order> getOrderListInfo(int status, int memberId) {
        List<Order> orderlists = null;
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "OrderConfirm";
            //後端先拿預載資料
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("RESULTCODE", 5);
            jsonObject.addProperty("STATUS", status);
            jsonObject.addProperty("SIGNINID", memberId); //房客
            String jsonin = RemoteAccess.getJsonData(url, jsonObject.toString());

            JsonObject tmp = gson.fromJson(jsonin, JsonObject.class);
            Type listType = new TypeToken<List<Order>>() {
            }.getType();

            orderlists = gson.fromJson(tmp.get("ORDERLIST").getAsString(), listType);

            Log.d("ORDER", orderlists.toString());

            return orderlists;
        } else {
            Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //set recycleView Adapter
    private void setOrderList(List<Order> orderList) {
        MyAdaptor myAdaptor = (MyAdaptor) recyclerView.getAdapter();
        if (myAdaptor == null) {
            myAdaptor = new MyAdaptor(activity, orderList);
            recyclerView.setAdapter(myAdaptor);
        }
        myAdaptor.setOrderList(orderList);
        myAdaptor.notifyDataSetChanged();
    }

    //recycleView
    private class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.Holder> {
        private List<Order> orderList;
        private Context context;
        private final int imageSize;

        public MyAdaptor(Context context, List<Order> orderList) {
            this.context = context;
            this.orderList = orderList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setOrderList(List<Order> orderList) {
            this.orderList = orderList;
        }


        //view Holder
        class Holder extends RecyclerView.ViewHolder {
            ImageView imgPic;
            TextView tvTitle, tvArea, tvControlText;
            ImageView btClick;

            public Holder(@NonNull View itemView) {
                super(itemView);
                imgPic = itemView.findViewById(R.id.img_ocrItrmRV_pic);
                tvTitle = itemView.findViewById(R.id.tv_ocrItrmRV_Title);
                tvArea = itemView.findViewById(R.id.tv_ocrItrmRV_Area);
                btClick = itemView.findViewById(R.id.bt_ocrItrmRV_con1);
                tvControlText = itemView.findViewById(R.id.tv_ocrItrmRV_con1);
            }

        }

        @Override
        public int getItemCount() {
            return orderList == null ? 0 : orderList.size();
        }

        @Override
        public MyAdaptor.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(activity).
                    inflate(R.layout.ocr_item_rvview, parent, false);
            return new MyAdaptor.Holder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdaptor.Holder holder, int position) {

            final Order order = orderList.get(position);
            int orderId = order.getOrderId();
            int publishId = order.getPublishId();
            Publish publish = getPublish(publishId);

            //圖片
            String imgPath = publish.getTitleImg();
            if (imgPath != null) {
                setImgFromFireStorage(imgPath, holder.imgPic);
            } else {
                holder.imgPic.setImageResource(R.drawable.no_image);
            }

            holder.tvTitle.setText(publish.getTitle());
            holder.tvArea.setText(publish.getAddress());

            holder.tvControlText.setText("查看物件資訊"); //bt上顯示的字
            holder.btClick.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("publishId",publishId);
                Navigation.findNavController(v).navigate(R.id.publishDetailFragment,bundle);
            });
        }
    }

    //拿刊登單物件
    private Publish getPublish(int publishId) {
        Publish publish = new Publish();
        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "OrderConfirm";
            //後端先拿預載資料
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("RESULTCODE", 2);
            jsonObject.addProperty("PUBLISHID", publishId);
            String jsonin = RemoteAccess.getJsonData(url, jsonObject.toString());

            JsonObject tmp = gson.fromJson(jsonin, JsonObject.class);
            String punStr = tmp.get("PUBLISH").getAsString();

            publish = gson.fromJson(punStr, Publish.class);

            Log.d("PUB", publish.toString());

            return publish;

        } else {
            Toast.makeText(activity, "網路連線失敗", Toast.LENGTH_SHORT).show();
        }
        return null;
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