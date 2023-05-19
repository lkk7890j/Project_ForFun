package idv.tfp10105.project_forfun.common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.FutureTask;

public class RemoteAccess {

    //取得JSON格式的資料
    public static String getJsonData(String url, String clientInStr) {
        JsonFromCallable callable = new JsonFromCallable(url, clientInStr);
        FutureTask<String> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();
        try {
            return task.get();
        } catch (Exception e) {
            Log.d("顯示getJsonData的錯誤:",e.toString());
            task.cancel(true);

            return "";
        }
    }

    // 檢查是否有網路連線
    public static boolean networkCheck(Activity activity) {
        ConnectivityManager connectivityManager;
        Network network;
        NetworkCapabilities networkCapabilities;
        connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo  = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }


}
