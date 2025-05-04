package dev.tevarin.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AutoDiYuQiShi {

    private static final String IP_API_URL = "https://api64.ipify.org?format=json";
    private static final String LOCATION_API_URL = "http://ip-api.com/json/";

    private static final HttpClient httpClient = HttpClients.createDefault();

    private static String fetchResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpEntity entity = httpClient.execute(request).getEntity();
        return EntityUtils.toString(entity);
    }

    private static String getPublicIPAddress() throws IOException {
        String response = fetchResponse(IP_API_URL);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("ip");
    }

    private static String getLocationByIP(String ipAddress) throws IOException {
        String apiUrl = LOCATION_API_URL + ipAddress;
        String response = fetchResponse(apiUrl);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("regionName");
    }

    private static final Map<String, String> LOCATION_MAP = initializeLocationMap();

    private static Map<String, String> initializeLocationMap() {
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("beijing", "北京");
        locationMap.put("tianjin", "天津");
        locationMap.put("shanghai", "上海");
        locationMap.put("chongqing", "重庆");
        locationMap.put("anhui", "安徽");
        locationMap.put("fujian", "福建");
        locationMap.put("gansu", "甘肃");
        locationMap.put("guangdong", "广东");
        locationMap.put("guizhou", "贵州");
        locationMap.put("hainan", "海南");
        locationMap.put("hebei", "河北");
        locationMap.put("heilongjiang", "黑龙江");
        locationMap.put("henan", "河南");
        locationMap.put("hubei", "湖北");
        locationMap.put("hunan", "湖南");
        locationMap.put("jiangsu", "江苏");
        locationMap.put("jiangxi", "江西");
        locationMap.put("jilin", "吉林");
        locationMap.put("liaoning", "辽宁");
        locationMap.put("qinghai", "青海");
        locationMap.put("shaanxi", "陕西");
        locationMap.put("shandong", "山东");
        locationMap.put("shanxi", "山西");
        locationMap.put("sichuan", "四川");
        locationMap.put("yunnan", "云南");
        locationMap.put("zhejiang", "浙江");
        locationMap.put("guangxi", "广西");
        locationMap.put("neimenggu", "内蒙古");
        locationMap.put("ningxia", "宁夏");
        locationMap.put("xizang", "西藏");
        locationMap.put("xinjiang", "新疆");
        locationMap.put("hongkong", "香港");
        locationMap.put("macau", "澳门");
        locationMap.put("taiwan", "台湾");
        return locationMap;
    }

    public static String getLocation() {
        try {
            String location = getLocationByIP(getPublicIPAddress());
            String province = LOCATION_MAP.getOrDefault(location.toLowerCase(), "外国");
            return (province != null) ? province : "外国";
        } catch (IOException e) {
            e.printStackTrace();
            return "获取位置失败";
        }
    }
}
