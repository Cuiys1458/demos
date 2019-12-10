package com.example.demo.Util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpClentUtil {
    /**
     * @param url   链接    例 https://www.baidu.com
     * @param param 参数   格式param1=1&param2=2
     * @param map   header参数 (有就传没有就null)
     * @return
     */
    public static String sendGet(String url, String param, Map<String, String> map) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        if (StringUtils.isNotEmpty(param)) {
            url = url + "?" + param;
        }
        HttpGet httpget = new HttpGet(url);
        if (map != null) {
            for (String key : map.keySet()) {
                System.out.println("header信息" + key + "--->" + map.get(key));
                httpget.setHeader(key, map.get(key));
            }
        }
        HttpResponse response = httpclient.execute(httpget);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String str = EntityUtils.toString(entity);
        return str;
    }

    /**
     * @param url     要提交的目标url
     * @param map     参数集合
     * @param charset 编码
     * @param headers header
     * @return
     */
    public static String sendPost(String url, Map<String, String> map, String charset, Map<String, String> headers) {
        // 定义一个可关闭的httpClient的对象
        CloseableHttpClient httpClient = null;

        // 定义httpPost对象
        HttpPost post = null;

        // 返回结果
        String result = null;

        try {
            // 1.创建httpClient的默认实例
            httpClient = HttpClients.createDefault();
            // 2.提交post
            post = new HttpPost(url);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    System.out.println("header信息" + key + "--->" + headers.get(key));
                    post.setHeader(key, headers.get(key));
                }
            }
            // 3.设置参数
            List<NameValuePair> list = new ArrayList<>();
            // 4.迭代参数
            if (map != null) {
                for (String key : map.keySet()) {
                    list.add(new BasicNameValuePair(key, map.get(key)));
                }
            }
            // 5.编码
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                post.setEntity(entity);
            }

            // 执行
            CloseableHttpResponse response = httpClient.execute(post);
            try {
                if (response != null) {
                    HttpEntity httpEntity = response.getEntity();
                    // 如果返回的内容不为空
                    if (httpEntity != null) {
                      result = EntityUtils.toString(httpEntity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭response
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
