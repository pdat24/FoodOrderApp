package com.firstapp.foodorderapp.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<Integer, String> timeRangeTable = new HashMap<>();
    public static Map<Integer, String> priceRangeTable = new HashMap<>();

    static {
        timeRangeTable.put(0, "0 - 10 min");
        timeRangeTable.put(1, "10 - 30 min");
        timeRangeTable.put(2, "more than 30 min");
    }

    static {
        priceRangeTable.put(0, "1 - 10$");
        priceRangeTable.put(1, "10 - 30$");
        priceRangeTable.put(2, "more than 30$");
    }

    static final public String API_BASE_URL = "http://192.168.0.108:8080/api/v1/";
    static final public int OK_CODE = 200;
    static final public int CONFLICT_CODE = 409;
    static final public int UNAUTHORIZED_CODE = 401;
    static final public String MAIN_SHARED_PREFERENCE = "MAIN_SHARED_PREFERENCE";
    static final public String UID = "uid";
    static final public String FOOD_NAME = "FOOD_NAME";
    static final public String FOOD_THUMBNAIL = "FOOD_THUMBNAIL";
    static final public String FOOD_DETAIL = "FOOD_DETAIL";
    static final public String FOOD_PRICE = "FOOD_PRICE";
    static final public String FOOD_FINISH_TIME = "FOOD_FINISH_TIME";
    static final public String FOOD_VOTE = "FOOD_VOTE";
    static final public String CATEGORY_ID = "CATEGORY_ID";
    static final public String CATEGORY_NAME = "CATEGORY_NAME";
    static final public int UN_FILTER_CODE = -1;
}
