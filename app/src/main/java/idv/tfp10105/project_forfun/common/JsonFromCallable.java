package idv.tfp10105.project_forfun.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class JsonFromCallable implements Callable<String> {
    private String url;
    private String clientReq; //客戶端發出的請求(JSON)

    public JsonFromCallable(String url, String clientReq) {
        this.url = url;
        this.clientReq = clientReq;
    }
    //對伺服器的設定並取得伺服器的回覆
    @Override
    public String call() throws Exception {
        //設定客戶端與伺服器的連線
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setDoInput(true); // 允許對伺服器發出請求
            connection.setDoOutput(true); // 允許從伺服器接收回覆
            // 不知道請求內容大小時可以呼叫此方法將請求內容分段傳輸，設定0代表使用預設大小
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false); // do not use a cached copy
            //與伺服器提出請求的方式GET or POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("charset", "UTF-8");

            //寫出客戶端請求的資料String格式給伺服器(JSON)
            try (
                    OutputStreamWriter os=new OutputStreamWriter(connection.getOutputStream());
                    BufferedWriter bw = new BufferedWriter(os) ){
                bw.write(clientReq);
                Log.d("顯示客戶端送出的請求字串:", clientReq);
            }

            //檢查伺服器回傳的狀態
            int responseCode = connection.getResponseCode();
            Log.d("顯示與伺服器連線的狀態碼：", responseCode + "");
            if (responseCode == 200) {
                //用來儲存伺服器回傳的資料String(JSON)
                StringBuilder serveResp = new StringBuilder();
                //讀入伺服器回傳的資料給客戶端String(JSON)
                try (InputStreamReader is = new InputStreamReader(connection.getInputStream());
                     BufferedReader br = new BufferedReader(is);
                ) {
                    String line;
                    while ((line=br.readLine()) != null) {
                        serveResp.append(line);
                    }
                   Log.d("顯示伺服器給的JSON回應:", serveResp+"");
                }
                return serveResp.toString();
            }
            else {
                return "error";
            }


        }
        catch (IOException e) {
            Log.d("顯示與伺服器傳輸的錯誤：", e.toString());
            return "error";
        }
        finally {
            //完成此次連線後關閉連線
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}
