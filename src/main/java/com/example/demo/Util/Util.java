package com.example.demo.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Util {
    private static ReentrantLock lock = new ReentrantLock(true);

    //停止转动
    public static String move2(String ip, String name, String password) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
        String encoding = DatatypeConverter.printBase64Binary((new StringBuilder(String.valueOf(name))).append(":").append(password).toString().getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip).append("/merlin/PtzCtrl.cgi?operation=").append(0).append("&speed=").append(3).append("&channelno=0").toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic "))
                .append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        System.out.println("摄像头停止命令执行:"+statusLine);
        return "ok";
    }

    //获取角度的链接
    public static HttpResponse conect(String ip, String name, String password) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String encoding = DatatypeConverter
                .printBase64Binary((new StringBuilder(String.valueOf(name)))
                        .append(":").append(password).toString()
                        .getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip)
                .append("/merlin/QueryPtz.cgi?operation=83&channelno=0")
                .toString();
        //http://192.168.1.8/merlin/QueryPtz.cgi?operation=83&channelno=0
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic "))
                .append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    //获取变倍的链接
    public static HttpResponse conect1(String ip, String name, String password) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String encoding = DatatypeConverter
                .printBase64Binary((new StringBuilder(String.valueOf(name)))
                        .append(":").append(password).toString()
                        .getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip)
                .append("/merlin/QueryPtz.cgi?operation=85&channelno=0")
                .toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic "))
                .append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    /**
     * 通过ip,name,password查询当前角度
     *
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static double getAngle(String ip, String name, String password) throws ClientProtocolException, IOException {
        HttpResponse response = conect(ip, name, password);
        if (response == null) {
            return -1.0;
        }
        StatusLine statusLine = response.getStatusLine();
        int responseCodeStart = statusLine.getStatusCode();
        if (responseCodeStart == 200 || responseCodeStart == 622) {
            HttpEntity entity = response.getEntity();
            String str = EntityUtils.toString(entity);
            String num = str.substring(str.indexOf(":") + 1, str.indexOf(",")).trim();
            System.out.println(num+"-------");
            if (num.contains("m")) {
                System.out.println(num + "responseCodeStart---" + responseCodeStart);
                return -1.0;
            } else {
                int angle = Integer.parseInt(num);
                System.out.println("持续获取角度"+angle);
                return angle / 100.0;
            }
        } else {
            return -1.0;
        }
    }

    /**
     * 根据type的值选择方向转动，5-右，4-左
     *
     * @param type
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws Exception
     */
    public static int move(int type, String ip, String name, String password) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
        String encoding = DatatypeConverter.printBase64Binary((new StringBuilder(String.valueOf(name))).append(":").append(password).toString().getBytes("UTF-8"));
       //http://admin:admin12345@172.16.1.56/merlin/PtzCtrl.cgi?operation=4&speed=5&channelno=0&value=0
        String path = (new StringBuilder("http://")).append(ip).append("/merlin/PtzCtrl.cgi?operation=").append(type).append("&speed=").append(1).append("&channelno=0").toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic ")).append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        //200请求成功、303重定向、400请求错误、401未授权、403禁止访问、404文件未找到、500服务器错误
        StatusLine statusLine = response.getStatusLine();
        int  responseCodeStart = statusLine.getStatusCode();
        System.out.println("responseCodeStart+让其旋转返回状态"+responseCodeStart);
        return responseCodeStart;
    }

    /**
     * 根据ip,name,password查询当前变焦倍数
     *
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String getMultiply(String ip, String name, String password) throws ClientProtocolException, IOException {
        HttpResponse response = conect1(ip, name, password);
        StatusLine statusLine = response.getStatusLine();
        int responseCodeStart = statusLine.getStatusCode();
        if (responseCodeStart == 200) {
            HttpEntity entity = response.getEntity();
            String str = EntityUtils.toString(entity);
            String num = str.substring(str.indexOf(":") + 1, str.indexOf(",")).trim();
            System.out.println("ok持续转动");
            return num;
        } else {
            return "0";
        }
    }

    /**
     * 变倍根据type选择放大缩小 10-放大，9-缩小，0-停止
     *
     * @param type
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws Exception
     * http://admin:admin888@192.168.1.8/merlin/PtzCtrl.cgi?operation=0&speed=5&channelno=0&value=0
     */
    public static String bian(int type, String ip, String name, String password) throws Exception {
        //System.out.println("type-----"+type);
        HttpClient httpclient = new DefaultHttpClient();
        String encoding = DatatypeConverter.printBase64Binary((new StringBuilder(String.valueOf(name))).append(":").append(password).toString().getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip).append("/merlin/PtzCtrl.cgi?operation=").append(type).append("&speed=").append(1).append("&channelno=0").toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic ")).append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        int responseCodeStart = statusLine.getStatusCode();
        System.out.println("bian====responseCodeStart" + responseCodeStart);
        String str = "变倍转动中---" + responseCodeStart;
        if (type == 0) {
            str = "停止中----------";
        }
        return str;
    }





    /**
     * 根据type的值选择方向转动，5-右，4-左
     *
     * @param type
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws Exception
     */
    public static int moveforspeed(int speed,int type, String ip, String name, String password) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
        String encoding = DatatypeConverter.printBase64Binary((new StringBuilder(String.valueOf(name))).append(":").append(password).toString().getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip).append("/merlin/PtzCtrl.cgi?operation=").append(type).append("&speed=").append(speed).append("&channelno=0").toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic ")).append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        //200请求成功、303重定向、400请求错误、401未授权、403禁止访问、404文件未找到、500服务器错误
        StatusLine statusLine = response.getStatusLine();
        int  responseCodeStart = statusLine.getStatusCode();
        System.out.println("responseCodeStart"+responseCodeStart);
        return responseCodeStart;
    }


    /**
     * test设置水平角度
     *
     * @param type
     * @param ip
     * @param name
     * @param password
     * @return
     * @throws Exception
     * http://admin:admin888@192.168.1.8/merlin/PtzCtrl.cgi?operation=0&speed=5&channelno=0&value=0
     */
    public static String shui(int type, String ip, String name, String password) throws Exception {
        //System.out.println("type-----"+type);
        HttpClient httpclient = new DefaultHttpClient();
        String encoding = DatatypeConverter.printBase64Binary((new StringBuilder(String.valueOf(name))).append(":").append(password).toString().getBytes("UTF-8"));
        String path = (new StringBuilder("http://")).append(ip).append("/merlin/PTZSetposition.cgi?operation=86&speed=1&channelno=0&position=300").toString();
        HttpGet httpget = new HttpGet(path);
        httpget.setHeader("Authorization", (new StringBuilder("Basic ")).append(encoding).toString());
        HttpResponse response = httpclient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        int responseCodeStart = statusLine.getStatusCode();
        System.out.println("bian====responseCodeStart" + responseCodeStart);
        String str = "变倍转动中---" + responseCodeStart;
        if (type == 0) {
            str = "停止中----------";
        }
        return str;
    }
}
