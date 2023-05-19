package idv.tfp10105.project_forfun.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.tfp10105.project_forfun.common.bean.Area;
import idv.tfp10105.project_forfun.common.bean.City;

public class CityAreaUtil {
    private static CityAreaUtil cityAreaUtil = new CityAreaUtil();
    private Gson gson;

    List<City> cityList;
    List<Area> areaList;
    Map<Integer, List<Area>> areaMap;

    private CityAreaUtil() {
        gson = new Gson();

        areaMap = new HashMap<>();


        // 取得縣市級行政區資料
        String url = Common.URL + "/getCityAreaData";
        String jsonIn = RemoteAccess.getJsonData(url, "");
        JsonObject object = gson.fromJson(jsonIn, JsonObject.class);

        String cityJson = object.get("city").getAsString();
        Type listCity = new TypeToken<List<City>>() {}.getType();
        cityList = gson.fromJson(cityJson, listCity);

        String areaJson = object.get("area").getAsString();
        Type listArea = new TypeToken<List<Area>>() {}.getType();
        areaList = gson.fromJson(areaJson, listArea);
        for (Area area : areaList) {
            // 把資料從list轉成map，方便之後取資料
            if (areaMap.get(area.getCityId()) == null) {
                List<Area> areas = new ArrayList<>();
                areas.add(area);

                areaMap.put(area.getCityId(), areas);
            } else {
                areaMap.get(area.getCityId()).add(area);
            }
        }

//            for (Integer key : areaMap.keySet()) {
//                for (Area aa : areaMap.get(key)) {
//                    Log.d("jsonIn", "key = " + key + ", cityId = " + aa.getCityId() + ", getAreaName = " + aa.getAreaName());
//                }
//            }
    }

    public static CityAreaUtil getInstance() {
        return cityAreaUtil;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public Map<Integer, List<Area>> getAreaMap () {
        return areaMap;
    }
}
