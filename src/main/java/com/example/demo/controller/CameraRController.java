package com.example.demo.controller;

import com.example.demo.Util.HttpClentUtil;
import com.example.demo.Util.Tools;
import com.example.demo.Util.Util;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;



@Controller
@Api(value = "明景摄像头地图中控制")
public class CameraRController {

    private static List d = new ArrayList();
    private static ReentrantLock lock = new ReentrantLock(true);

    /**
     * @param response
     * @param request
     * @param juedu
     * @param iszhengshu
     * @param type
     * @param userName
     * @param password
     * @param ip
     * @param distance
     * @param degree
     * @param cameraType
     * @param jobsType
     * @return
     */
    @RequestMapping({"/cameraRotation"})
    @ResponseBody
    public String cameraRotation(HttpServletResponse response, HttpServletRequest request, String juedu, String iszhengshu, String type, String userName, String password, String ip, @RequestParam(defaultValue = "0") double distance, String degree, String cameraType, String jobsType) {
        int deg = 0;
        String res = "";

        String ips = Tools.getValue("ips");
        if ((degree != null) && (degree != "")) {
            deg = Integer.parseInt(degree);
        }
        try {
            if (d.contains(ip)) {
                res = "0";
            } else {
                lock.lock();
                d.add(ip);
                if (type.equals("1")) {
                    double angle1 = Util.getAngle(ip, userName, password);
                    double ad = angle1 - deg;
                    if (ad < 0.0D) {
                        ad += 360.0D;
                    }
                    res = String.valueOf(ad);
                } else {

                    double num;
                    if ("mingjing".equalsIgnoreCase(cameraType)) {
                        if ("xuanzhuan".equalsIgnoreCase(jobsType)) {
                            request.setCharacterEncoding("utf-8");
                            response.setCharacterEncoding("utf-8");
                            int angle = (int) Math.floor(NumberUtils.toDouble(juedu));
                            if (angle < 0) {
                                angle += 360;
                            }
                            double jiaodu = Util.getAngle(ip, userName, password);
                            if ((ips.contains(ip)) && (jiaodu != -1.0D)) {
                                int du = (int) Math.floor(jiaodu);
                                int ty = du - angle < 180 ? 4 : angle > du ? 4 : angle - du < 180 ? 5 : 5;
                                Util.move(ty, ip, userName, password);
                                //一直转动 知道与旋转角度重合位置
                                for (; ; ) {
                                    double jiaodu1 = Util.getAngle(ip, userName, password);

                                    if ((Math.abs(jiaodu1 - angle) < 2.0D) || (Math.abs(jiaodu1 - angle) > 358.0D) || (jiaodu1 == -1.0D)) {
                                        break;
                                    }
                                    Thread.sleep(3000);
                                }
                                //停止
                                Util.move2(ip, userName, password);
                                double redu = Util.getAngle(ip, userName, password);
                                res = String.valueOf(redu - deg);
                                if (redu - deg < 0.0D) {
                                    res = String.valueOf(redu - deg + 360.0D);
                                }
                            } else {
                                res = "-1";
                            }
                        } else if ("bianbei".equalsIgnoreCase(jobsType)) {
                            distance *= 0.0027027027027027D;
                            System.out.println("distance" + distance);

                            num = new BigDecimal(Double.parseDouble(Util.getMultiply(ip, userName, password)) / 273.0D).setScale(2, 4).doubleValue();
                            int ty = num > distance ? 9 : 10;
                            Util.bian(ty, ip, userName, password);
                            boolean b = true;
                            while (b) {
                                double bianBei1 = new BigDecimal(Double.parseDouble(Util.getMultiply(ip, userName, password)) / 273.0D).setScale(2, 4).doubleValue();
                                if (ty == 10) {
                                    if ((bianBei1 >= distance) || (bianBei1 >= 20.0D)) {
                                        b = false;
                                    }
                                } else if ((ty == 9) && (
                                        (bianBei1 <= distance) || (bianBei1 <= 1.0D))) {
                                    b = false;
                                }
                                Thread.sleep(3000);
                            }
                            String path = "http://" + ip + "/merlin/PtzCtrl.cgi?operation=" + 0 + "&speed=" + 1 + "&channelno=0";
                            Map<String, String> map = new HashMap();
                            String encoding = DatatypeConverter.printBase64Binary(("admin" + ":" + "admin888").getBytes("UTF-8"));
                            map.put("Authorization", "Basic " + encoding);
                            String str1 = HttpClentUtil.sendPost(path, null, "utf-8", map);
                        } else {
                            return "-15";
                        }
                    } else if (!"dahua".equalsIgnoreCase(cameraType)) {
                        return "-10";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            d.remove(ip);
        }
        return res;
    }

    @RequestMapping({"/getDis"})
    @ResponseBody
    public double getDis(double longitude1, double latitude1, double longitude2, double latitude2)
            throws UnsupportedEncodingException {
        double d = algorithm(longitude1, latitude1, longitude2, latitude2);
        System.out.println("d------------------------------------------" + d);
        return d;
    }

    public static double algorithm(double longitude1, double latitude1, double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);

        double Lat2 = rad(latitude2);

        double a = Lat1 - Lat2;

        double b = rad(longitude1) - rad(longitude2);

        double s = 2.0D * Math.asin(

                Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));

        s *= 6378137.0D;

        s = Math.round(s * 10000.0D) / 10000.0D;

        return s;
    }

    private static double rad(double d) {
        return d * 3.141592653589793D / 180.0D;
    }
}
