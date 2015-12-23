package com.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangjingchao on 15/12/14.
 */
public class Config {
    private Map<Integer, Child> mMap = new HashMap<Integer, Child>();

    public Config(){
        init();
    }


    private void init(){
        mMap.put(1, new Child(1, "中餐", 8, 14, 20, 29, 50));
        mMap.put(2, new Child(2, "咖啡", 9, 15, 19, 37, 55));
        mMap.put(3, new Child(3, "烤肉", 10, 16, 18, 45, 60));
        mMap.put(4, new Child(4, "西餐", 11, 17, 17, 52, 65));
        mMap.put(5, new Child(5, "披萨", 12, 18, 16, 58, 70));
        mMap.put(6, new Child(6, "寿司", 7, 13, 15, 65, 75));

        mMap.put(7, new Child(7, "温泉", 15, 21, 20, 29, 50));
        mMap.put(8, new Child(8, "KTV", 16, 22, 19, 37, 55));
        mMap.put(9, new Child(9, "桌球", 35, 23, 18, 45, 60));
        mMap.put(10, new Child(10, "酒吧", 18, 24, 17, 52, 65));
        mMap.put(11, new Child(11, "养生", 13, 19, 16, 58, 70));
        mMap.put(12, new Child(12, "棋牌", 14, 20, 15, 65, 75));

        mMap.put(13, new Child(13, "服装", 20, 26, 20, 29, 50));
        mMap.put(14, new Child(14, "鞋城", 21, 27, 19, 37, 55));
        mMap.put(15, new Child(15, "皮具", 22, 28, 18, 45, 60));
        mMap.put(16, new Child(16, "布艺", 23, 29, 17, 52, 65));
        mMap.put(17, new Child(17, "西装", 7, 30, 16, 58, 70));
        mMap.put(18, new Child(18, "旗袍", 19, 25, 15, 65, 75));

        mMap.put(19, new Child(19, "珠宝", 27, 33, 20, 29, 50));
        mMap.put(20, new Child(20, "香水", 28, 34, 19, 37, 55));
        mMap.put(21, new Child(21, "名表", 29, 35, 18, 45, 60));
        mMap.put(22, new Child(22, "名包", 30, 36, 17, 52, 65));
        mMap.put(23, new Child(23, "古玩", 25, 31, 16, 58, 70));
        mMap.put(24, new Child(24, "赛车", 26, 32, 15, 65, 75));

        mMap.put(25, new Child(25, "超市", 2, 32, 20, 29, 50));
        mMap.put(26, new Child(26, "玩具", 3, 33, 19, 37, 55));
        mMap.put(27, new Child(27, "美妆", 4, 34, 18, 45, 60));
        mMap.put(28, new Child(28, "书店", 5, 17, 17, 52, 65));
        mMap.put(29, new Child(29, "药店", 6, 36, 16, 58, 70));
        mMap.put(30, new Child(30, "便利", 1, 31, 15, 65, 75));

        mMap.put(31, new Child(31, "影楼", 3, 9, 20, 29, 50));
        mMap.put(32, new Child(32, "手机", 4, 10, 19, 37, 55));
        mMap.put(33, new Child(33, "电脑", 5, 11, 18, 45, 60));
        mMap.put(34, new Child(34, "电玩", 6, 12, 17, 52, 65));
        mMap.put(35, new Child(35, "3D", 1, 24, 16, 58, 70));
        mMap.put(36, new Child(36, "音响", 2, 8, 15, 65, 75));

    }

    public Child getChild(int n){
      return mMap.get(n);
    }
}
