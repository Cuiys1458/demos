package com.example.demo.Util;

import main.java.com.netsdk.demo.module.PtzControlModule;

public class DaHua {
    public static void main(String[] args) {
        kongzhi();
    }
    public static void  kongzhi(){
        //查询
        //计算并保存
        double lng1=101.0302734375;
        double lat1=41.4756602003;
       // double lng2=101.03988647460939;
        double lng2=100.9569740295413;
        //double lat2=41.547130732998724;
        //上 9789 4956  下9691.0 4728  左7357.0 3980  右7302.0   3874
        double lat2=41.52631473117791;
        double a = Rad(lat1) - Rad(lat2);
        double b = Rad(lng1) - Rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                        Math.cos(Rad(lat1)) * Math.cos(Rad(lat2)) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;//WGS1984坐标系： 6378137.0
        s = Math.round(s * 10000) / 10000;
        System.out.print("两点之间距离为："+s);//单位m
    }
    private static double Rad(double d)
    {
        return d * Math.PI / 180.0;
    }
}
