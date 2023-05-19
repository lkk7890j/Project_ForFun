package idv.tfp10105.project_forfun.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.tfp10105.project_forfun.MainActivity;
import idv.tfp10105.project_forfun.R;
import idv.tfp10105.project_forfun.common.CityAreaUtil;
import idv.tfp10105.project_forfun.common.Common;
import idv.tfp10105.project_forfun.common.RemoteAccess;
import idv.tfp10105.project_forfun.common.bean.Area;
import idv.tfp10105.project_forfun.common.bean.City;
import idv.tfp10105.project_forfun.common.bean.Favorite;
import idv.tfp10105.project_forfun.common.bean.Publish;
import idv.tfp10105.project_forfun.common.bean.PublishHome;

public class HomeFragment extends Fragment {
    private final double SEARCH_DISTANCE = 100.0;

    private MainActivity activity;
    private Gson gson;
    private Geocoder geocoder;
    private FirebaseStorage storage;
    private PublishAdapter publishAdapter;
    private SharedPreferences sharedPreferences;

    // UI元件
    private MapView mapView;
    private GoogleMap googleMap;
    private Button homeBtnDistanceSort, homeBtnRentSort, homeBtnSearch;
    private RecyclerView homePublishListView;
    private FrameLayout homeLoading;

    // 搜尋頁面
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText editHomeSearchRentLower, editHomeSearchRentUpper;
    private Spinner spHomeSearchCity, spHomeSearchArea;
    private RadioGroup radioHomeSearchGender, radioHomeSearchType;
    private RadioButton radioHomeSearchGender0, radioHomeSearchGender1, radioHomeSearchGender2;
    private RadioButton radioHomeSearchType0, radioHomeSearchType1, radioHomeSearchType2;
    private Button btnHomeSearch;


    // 變動資料
    private int userId;
    private List<PublishHome> publishHomeList;
    private List<City> cityList;
    private List<Area> areaList;
    private Map<Integer, List<Area>> areaMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 各種元件初始化
        activity = (MainActivity) getActivity();
        gson = new Gson();
        geocoder = new Geocoder(activity);
        storage = FirebaseStorage.getInstance();
        sharedPreferences = activity.getSharedPreferences( "SharedPreferences", Context.MODE_PRIVATE);

        cityList = CityAreaUtil.getInstance().getCityList();
        areaList = CityAreaUtil.getInstance().getAreaList();
        areaMap = CityAreaUtil.getInstance().getAreaMap();

        publishAdapter = new PublishAdapter(new DiffUtil.ItemCallback<PublishHome>() {
            @Override
            public boolean areItemsTheSame(@NonNull PublishHome oldItem, @NonNull PublishHome newItem) {
                return oldItem.getPublish().getPublishId().intValue() == newItem.getPublish().getPublishId().intValue();
            }

            @Override
            public boolean areContentsTheSame(@NonNull PublishHome oldItem, @NonNull PublishHome newItem) {
                return oldItem.equals(newItem);
            }
        });

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = sharedPreferences.getInt("memberId",-1);

        // 按鈕
        homeBtnDistanceSort = view.findViewById(R.id.homeBtnDistanceSort);
        homeBtnRentSort = view.findViewById(R.id.homeBtnRentSort);
        homeBtnSearch = view.findViewById(R.id.homeBtnSearch);

        homeBtnDistanceSort.setOnClickListener(v -> sortByDistance(publishHomeList));

        homeBtnRentSort.setOnClickListener(v -> sortByRent(publishHomeList));

        homeBtnSearch.setOnClickListener(v -> bottomSheetDialog.show());

        // RecyclerView
        homePublishListView = view.findViewById(R.id.homePublishListView);
        homePublishListView.setLayoutManager(new LinearLayoutManager(activity));
        homePublishListView.setAdapter(publishAdapter);

        // 地圖
        mapView = view.findViewById(R.id.homeMap);
        homeLoading = view.findViewById(R.id.homeLoading);
        // 顯示loading畫面
        homeLoading.setVisibility(View.VISIBLE);

        // 檢查手機設定中定位功能是否開啟
        checkPositioning();

        // 初始化地圖
        mapView.onCreate(savedInstanceState);
        mapView.onStart();
        mapView.getMapAsync(googleMap -> {
            this.googleMap = googleMap;

            showMyLocation();
        });

        // 1. 想做個Loading....畫面，但用下面的方式會完全卡死，感覺要用thread
        // 先抓位置另存，activity先取得使用者位置存到偏好設定檔，先使用存好的去呈現，後續再去抓新的
        // 主執行緒執行ＬＯＡＤＩＮＧ畫面，另開執行緒去抓資料hostdelay?
        // ProgressDialog 或 ProgressBar
//        while (googleMap == null) {
//            Log.d("home", "Loading....");
//        }
//
//        Log.d("home", "OK!!!");

        // 2. 經緯度轉地址只會有一段，如何從中取出縣市和行政區資料去轉換成自定義的ID
        // 目前解法跑回圈比對縣市(行政區)名稱是否存在於地址中，有的話就可以找出對應的ID
        // 但在行政區的部分，不同縣市卻有一樣的行政區名稱，導致實作的複雜度提高，想問看看有沒有好的方法可以解決
//        for (City city : cityList) {
//            if ("使用者地址".contains(city.getCityName())) {
//                city.getCityId();
//                break;
//            }
//        }

        // 搜尋頁面
        View bottomSheetView = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_search,null);
        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 下拉選單
        spHomeSearchCity = bottomSheetView.findViewById(R.id.spHomeSearchCity);
        spHomeSearchArea = bottomSheetView.findViewById(R.id.spHomeSearchArea);
        handleSpinner();

        // 租金範圍
        editHomeSearchRentLower = bottomSheetView.findViewById(R.id.editHomeSearchRentLower);
        editHomeSearchRentUpper = bottomSheetView.findViewById(R.id.editHomeSearchRentUpper);

        // 性別限制
        radioHomeSearchGender = bottomSheetView.findViewById(R.id.radioHomeSearchGender);
        radioHomeSearchGender0 = bottomSheetView.findViewById(R.id.radioHomeSearchGender0);
        radioHomeSearchGender1 = bottomSheetView.findViewById(R.id.radioHomeSearchGender1);
        radioHomeSearchGender2 = bottomSheetView.findViewById(R.id.radioHomeSearchGender2);

        // 房屋類型
        radioHomeSearchType = bottomSheetView.findViewById(R.id.radioHomeSearchType);
        radioHomeSearchType0 = bottomSheetView.findViewById(R.id.radioHomeSearchType0);
        radioHomeSearchType1 = bottomSheetView.findViewById(R.id.radioHomeSearchType1);
        radioHomeSearchType2 = bottomSheetView.findViewById(R.id.radioHomeSearchType2);

        // 按鈕
        btnHomeSearch = bottomSheetView.findViewById(R.id.btnHomeSearch);
        handleButton();
    }

    private void checkPositioning() {
        // 建立定位請求物件
        LocationRequest locationRequest = LocationRequest.create();

        // 建立定位設定物件
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        // 抓取手機的設定
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

        // 檢查是否開啟定位設定
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

        // 加入失敗監聽器，當定位功能沒開啟時，讓其跳至設定畫面
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity, 0);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    // 顯示現在位置的小藍點 + 移動攝影機到現在位置
    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 顯示現在位置的小藍點
//        googleMap.setMyLocationEnabled(true);

        // 取得現在位置
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity);

        // 取得最新位置
        Task<Location> task = fusedLocationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken());

        task.addOnSuccessListener(location -> {
            if (location != null) {
                // 清除地圖標記
                googleMap.clear();

                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                // 處理刊登清單
                publishHomeList = getPublishHomeListNearby(getPublishData(), userLatLng) ;
                addPublishMarker(publishHomeList);
                publishAdapter.submitList(new ArrayList<>(publishHomeList));

                // 標記使用者位置
                drawCircle(userLatLng);
                addMapPeople(userLatLng);
                moveCamera(userLatLng);

//                latLngToName(userLatLng.latitude, userLatLng.longitude);

                // 隱藏loading畫面
                homeLoading.setVisibility(View.GONE);
            }
        });
    }

    // 畫圓
    private void drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)             // 2. 設定圓心
                .radius(SEARCH_DISTANCE)    // 3. 設定半徑(公尺)
                .strokeWidth(3)             // 4. 設定線寬
                .strokeColor(Color.rgb(239, 119, 220))  // 5. 設定線色
                .fillColor(Color.argb(170, 236, 211, 208));  // 6. 設定填滿色

        googleMap.addCircle(circleOptions);
    }

    // 加入地圖小人
    private void addMapPeople(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_man));

        googleMap.addMarker(markerOptions);
    }

    // 加入地圖標記
    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));

        googleMap.addMarker(markerOptions);
    }

    // 刊登資料加到地圖標記
    private void addPublishMarker(List<PublishHome> publishHomeList) {
        for (PublishHome publishHome : publishHomeList) {
            addMarker(new LatLng(publishHome.getPublish().getLatitude(), publishHome.getPublish().getLongitude()));
        }
    }

    // 移動攝影機
    private void moveCamera(LatLng latLng) {
        double lat = latLng.latitude;
        double lnt = latLng.longitude;

        // 建立CameraPosition和CameraUpdate
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lnt))
                .zoom(17.5f)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        googleMap.moveCamera(cameraUpdate);
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

    // 取得刊登資料
    private List<Publish> getPublishData() {
        List<Publish> publishList = null;

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "/getPublishData";
            JsonObject request = new JsonObject();
            request.addProperty("action", "getAll");

            String jsonIn = RemoteAccess.getJsonData(url, gson.toJson(request));

            JsonObject response = gson.fromJson(jsonIn, JsonObject.class);
            String publishJson = response.get("publishList").getAsString();
            Type listPublish = new TypeToken<List<Publish>>() {}.getType();
            publishList = gson.fromJson(publishJson, listPublish);

//            Log.d("publish", publishJson);
        }

        return publishList;
    }

    // 取得附近的刊登資訊
    private List<PublishHome> getPublishHomeListNearby(List<Publish> publishes, LatLng latLng) {
        List<PublishHome> publishList = new ArrayList<>();

        for (Publish publish : publishes) {
            // 計算距離
            float[] distance = new float[1];
            Location.distanceBetween(latLng.latitude, latLng.longitude, publish.getLatitude(), publish.getLongitude(), distance);

            // 搜尋範圍內才加入list中
            if (distance[0] < SEARCH_DISTANCE) {
                PublishHome publishHome = new PublishHome();
                publishHome.setPublish(publish);
                publishHome.setDistance(distance[0]);

                publishList.add(publishHome);
            }
        }

        return publishList;
    }

    // 取得刊登資訊
    private List<PublishHome> getPublishHomeList(List<Publish> publishes, LatLng latLng) {
        List<PublishHome> publishList = new ArrayList<>();

        for (Publish publish : publishes) {
            // 計算距離
            float[] distance = new float[1];
            Location.distanceBetween(latLng.latitude, latLng.longitude, publish.getLatitude(), publish.getLongitude(), distance);

            PublishHome publishHome = new PublishHome();
            publishHome.setPublish(publish);
            publishHome.setDistance(distance[0]);

            publishList.add(publishHome);
        }

        return publishList;
    }

    private Favorite getMyFavoriteByPublishId (int userId, int publishId) {
        Favorite favorite = null;

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "/favoriteController";
            JsonObject request = new JsonObject();
            request.addProperty("action", "getMyFavoriteByPublishId");
            request.addProperty("userId", userId);
            request.addProperty("publishId", publishId);

            String jsonResule = RemoteAccess.getJsonData(url, gson.toJson(request));
            Log.d("publish", jsonResule);

            JsonObject response = gson.fromJson(jsonResule, JsonObject.class);
            String favoriteJson = response.get("favorite").getAsString();

            favorite = gson.fromJson(favoriteJson, Favorite.class);
        }

        return favorite;
    }

    private Favorite addMyFavorite (int userId, int publishId) {
        Favorite favorite = null;

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "/favoriteController";
            JsonObject request = new JsonObject();
            request.addProperty("action", "addMyFavorite");
            request.addProperty("userId", userId);
            request.addProperty("publishId", publishId);

            String jsonResule = RemoteAccess.getJsonData(url, gson.toJson(request));
            Log.d("publish", jsonResule);

            JsonObject response = gson.fromJson(jsonResule, JsonObject.class);
            String favoriteJson = response.get("favorite").getAsString();

            favorite = gson.fromJson(favoriteJson, Favorite.class);
        }

        return favorite;
    }

    private boolean deleteMyFavorite (int favoriteId) {
        boolean result = false;

        if (RemoteAccess.networkCheck(activity)) {
            String url = Common.URL + "/favoriteController";
            JsonObject request = new JsonObject();
            request.addProperty("action", "remove");
            request.addProperty("removeId", favoriteId);

            String jsonResule = RemoteAccess.getJsonData(url, gson.toJson(request));
            Log.d("publish", jsonResule);

            JsonObject response = gson.fromJson(jsonResule, JsonObject.class);
            result = response.get("pass").getAsBoolean();
        }

        return result;
    }

    private class PublishAdapter extends ListAdapter<PublishHome, PublishAdapter.PublishViewHolder> {

        protected PublishAdapter(@NonNull DiffUtil.ItemCallback<PublishHome> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public PublishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.publish_home_itemview, parent, false);
            return new PublishViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PublishViewHolder holder, int position) {
            holder.onBind(getItem(position));
        }

        private class PublishViewHolder extends RecyclerView.ViewHolder {
            final private ImageView homePublishImg, homePublishLike;
            final private TextView homePublishName, homePublishArea, homePublishSquare, homePublishRent;

            private Favorite favorite;

            public PublishViewHolder(@NonNull View itemView) {
                super(itemView);

                homePublishImg = itemView.findViewById(R.id.homePublishImg);
                homePublishLike = itemView.findViewById(R.id.homePublishLike);
                homePublishName = itemView.findViewById(R.id.homePublishName);
                homePublishArea = itemView.findViewById(R.id.homePublishArea);
                homePublishSquare = itemView.findViewById(R.id.homePublishSquare);
                homePublishRent = itemView.findViewById(R.id.homePublishRent);
            }

            void onBind(PublishHome publishHome) {
                String cityName = "";
                for (City city : cityList) {
                    if (city.getCityId().equals(publishHome.getPublish().getCityId())) {
                        cityName = city.getCityName();
                        break;
                    }
                }

                String areaName = "";
                for (Area area : areaList) {
                    if (area.getAreaId().equals(publishHome.getPublish().getAreaId())) {
                        areaName = area.getAreaName();
                        break;
                    }
                }

                homePublishName.setText(publishHome.getPublish().getTitle());
                homePublishArea.setText(cityName + areaName);
                homePublishSquare.setText(publishHome.getPublish().getSquare() + "坪");
                homePublishRent.setText(publishHome.getPublish().getRent() + "/月");

                getImage(homePublishImg, publishHome.getPublish().getTitleImg());

                itemView.setOnClickListener(v -> {
                    // 把ID帶到詳細頁面
                    Bundle bundle = new Bundle();
                    bundle.putInt("publishId", publishHome.getPublish().getPublishId());

                    Navigation.findNavController(v).navigate(R.id.publishDetailFragment, bundle);
                });

                // 取得收藏資料
                favorite = getMyFavoriteByPublishId(userId, publishHome.getPublish().getPublishId());

                homePublishLike.setImageResource(favorite == null ? R.drawable.icon_unfavorite : R.drawable.icon_favorite);
                homePublishLike.setOnClickListener(v -> {
                    // 遊客不可收藏
                    int role = sharedPreferences.getInt("role", -1);
                    if (role == 3) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle("無法收藏");
                        dialog.setMessage("請先註冊為房客");
                        dialog.setPositiveButton("確定", null);

                        Window window = dialog.show().getWindow();
                        // 修改按鈕顏色
                        Button btnOK = window.findViewById(android.R.id.button1);
                        btnOK.setTextColor(getResources().getColor(R.color.black));

                        return;
                    }

                    if (favorite == null) {
                        favorite = addMyFavorite(userId, publishHome.getPublish().getPublishId());
                        homePublishLike.setImageResource(R.drawable.icon_favorite);
                        Toast.makeText(activity, "已加入收藏", Toast.LENGTH_SHORT).show();
                    } else {
                        if (deleteMyFavorite(favorite.getFavoriteId())) {
                            favorite = null;
                            homePublishLike.setImageResource(R.drawable.icon_unfavorite);
                            Toast.makeText(activity, "已取消收藏", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "取消收藏失敗", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }
    }

    private void sortByDistance(List<PublishHome> publishHomeList) {
        Collections.sort(publishHomeList, (o1, o2) -> (int)(o1.getDistance() - o2.getDistance()));

        publishAdapter.submitList(new ArrayList<>(publishHomeList), () -> homePublishListView.scrollToPosition(0));
    }

    private void sortByRent(List<PublishHome> publishHomeList) {
        Collections.sort(publishHomeList, (o1, o2) -> o1.getPublish().getRent() - o2.getPublish().getRent());

        publishAdapter.submitList(new ArrayList<>(publishHomeList), new Runnable() {
            // 刻意留的，提醒自己可用Runnable
            @Override
            public void run() {
                homePublishListView.scrollToPosition(0);
            }
        });
    }

    /*
     * 處理下拉選單
     */
    private void handleSpinner() {
        // 縣市
        List<String> cityNames = new ArrayList<>();
        for (City city : cityList) {
            cityNames.add(city.getCityName());
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, cityNames);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spHomeSearchCity.setAdapter(cityAdapter);
        spHomeSearchCity.setSelection(0, true);
        spHomeSearchCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 更換對應的行政區資料
                List<String> areaNames = new ArrayList<>();
                for (Area area : areaMap.get(cityList.get(position).getCityId())) {
                    areaNames.add(area.getAreaName());
                }

                ArrayAdapter<String> areaAdapter = (ArrayAdapter<String>) spHomeSearchArea.getAdapter();
                areaAdapter.clear();
                areaAdapter.addAll(areaNames);
                areaAdapter.notifyDataSetChanged();
                spHomeSearchArea.setSelection(0, true);

//                Log.d("spinner", "cityName = " + cityName + ", position = " + position + ", id = " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 行政區
        List<String> areaNames = new ArrayList<>();
        for (Area area : areaMap.get(1)) {
            // 沒資料的情況預設為1
            areaNames.add(area.getAreaName());
        }

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, areaNames);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spHomeSearchArea.setAdapter(areaAdapter);
        spHomeSearchArea.setSelection(0, true);
        spHomeSearchArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String areaName = ((TextView)view).getText().toString();
//                Log.d("spinner", "areaName = " + areaName + ", position = " + position + ", id = " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*
     * 處理按鈕
     */
    private void handleButton() {
        btnHomeSearch.setOnClickListener(v -> {
            // 資料整理
            // 縣市
            int cityId = cityList.get(spHomeSearchCity.getSelectedItemPosition()).getCityId();
            String cityName = cityList.get(spHomeSearchCity.getSelectedItemPosition()).getCityName();

            // 行政區
            int areaId = areaMap.get(cityId).get(spHomeSearchArea.getSelectedItemPosition()).getAreaId();
            String areaName = areaMap.get(cityId).get(spHomeSearchArea.getSelectedItemPosition()).getAreaName();

//            Log.d("home", "cityName = " + cityName + " areaName = " + areaName);

            // 取得新的位置
            Address address = nameToLatLng(cityName + areaName);
            LatLng userLatLng = new LatLng(address.getLatitude(), address.getLongitude());

            // 租金範圍
            int rentLower = 0;
            if (!editHomeSearchRentLower.getText().toString().trim().isEmpty()) {
                rentLower = Integer.parseInt(editHomeSearchRentLower.getText().toString().trim());
            }

            int rentUpper = Integer.MAX_VALUE;
            if (!editHomeSearchRentUpper.getText().toString().trim().isEmpty()) {
                rentUpper = Integer.parseInt(editHomeSearchRentUpper.getText().toString().trim());
            }

            if (rentLower >= rentUpper) {
                editHomeSearchRentLower.setError("上限須超過下限");
                editHomeSearchRentUpper.setError("上限須超過下限");
                return;
            }

            // 性別限制
            int gender = 0;
            switch (radioHomeSearchGender.getCheckedRadioButtonId()) {
                case R.id.radioHomeSearchGender0:
                    gender = 0;
                    break;
                case R.id.radioHomeSearchGender1:
                    gender = 1;
                    break;
                case R.id.radioHomeSearchGender2:
                    gender = 2;
                    break;
            }

            // 房屋類型
            int type = 0;
            switch (radioHomeSearchType.getCheckedRadioButtonId()) {
                case R.id.radioHomeSearchType0:
                    type = 0;
                    break;
                case R.id.radioHomeSearchType1:
                    type = 1;
                    break;
                case R.id.radioHomeSearchType2:
                    type = -1;
                    break;
            }

            //往server送資料
            if (RemoteAccess.networkCheck(activity)) {
                Map<String, String> searchMap = new HashMap<>();
                searchMap.put("cityId", String.valueOf(cityId));
                searchMap.put("areaId", String.valueOf(areaId));
                searchMap.put("rentLower", String.valueOf(rentLower));
                searchMap.put("rentUpper", String.valueOf(rentUpper));
                searchMap.put("gender", String.valueOf(gender));
                searchMap.put("type", String.valueOf(type));

                String url = Common.URL + "/getPublishData";
                JsonObject request = new JsonObject();
                request.addProperty("action", "getBySearch");
                request.addProperty("searchParam", gson.toJson(searchMap));

//                Log.d("home", gson.toJson(request));
                String jsonResule = RemoteAccess.getJsonData(url, gson.toJson(request));

                JsonObject response = gson.fromJson(jsonResule, JsonObject.class);
                String publishJson = response.get("publishList").getAsString();
                Type listCity = new TypeToken<List<Publish>>() {}.getType();
                List<Publish> publishes = gson.fromJson(publishJson, listCity);

                publishHomeList = getPublishHomeList(publishes, userLatLng);
//                Log.d("home", gson.toJson(publishHomeList));

                // 清除地圖標記
                googleMap.clear();

                // 增加地圖標記
                addPublishMarker(publishHomeList);

                // 更新清單
                publishAdapter.submitList(new ArrayList<>(publishHomeList));

                // 移動攝影機
                moveCamera(userLatLng);

                // 關閉搜尋頁面
                bottomSheetDialog.dismiss();
            }
        });
    }

    /*
     * 地名/地址 轉 緯經度
     */
    private Address nameToLatLng(String name) {
        try {
            List<Address> addressList = geocoder.getFromLocationName(name, 1);
            if (addressList != null && addressList.size() > 0) {
                return addressList.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 緯經度 轉 地名/地址
     */
    private String latLngToName(final double lat, final double lng) {
        try {
            if (!Geocoder.isPresent()) {
                return null;
            }
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            StringBuilder name = new StringBuilder();
            Address address = addressList.get(0);
            if (address != null) {
//                Log.d("home", "getAdminArea = "+address.getAdminArea()+address.getSubAdminArea());

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    name.append(address.getAddressLine(i))
                            .append("\n");

                    Log.d("home", "user address = " + address.getAddressLine(i));
                }
            }
            return name.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}