package idv.tfp10105.project_forfun.orderconfirm.tappayNetwork;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.FutureTask;

public class RemoteAccess_TapPay {
    private static final String TAG = "TAG_RemoteAccess";

    // 實機或模擬器
    // public static String URL = "http://10.2.0.177:8080/TextToJson_Web/";
    // 官方模擬器連線可使用「10.0.2.2」
    public final static String URL = "http://10.0.2.2:8080/TextToJson_Web/";

    public static String getRemoteData(String url, String outStr, String apiKey) {
        JsonCallable_TapPay callable = new JsonCallable_TapPay(url, outStr, apiKey);
        FutureTask<String> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();
        try {
            return task.get();
        } catch (Exception e) {
            task.cancel(true);
            return "";
        }
    }

    // 檢查是否有網路連線
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                Network network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    Log.d(TAG, msg);
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }
}
