package com.example.zodeaccount.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author cxy
 * @date 2019年12月17日 下午2:49
 * HttpClient工具类
 */
@Component
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * get请求
     * @return
     */
    public static String doGet(String url, String params) {
        String cs="";
        int i=0;
        if (!StringUtils.isBlank(params)){
            JSONObject parse = JSONObject.parseObject(params);
            HashMap<String,Object> map=new HashMap<String, Object>(parse);
            for(String key:map.keySet()){
                String value = map.get(key).toString();
                if(i==0){
                    cs=cs+"?"+key+"="+value;
                    i++;
                }else {
                    cs=cs+"&"+key+"="+value;
                }
            }
        }

        HttpResponse response = null;
        String strResult = null;
        try {
            HttpClient client = new DefaultHttpClient();
            System.out.println("url:"+url+cs);
            HttpGet httpget = new HttpGet(url+cs);
            response = client.execute(httpget);
            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                /**读取服务器返回过来的json字符串数据**/
                strResult = EntityUtils.toString(response.getEntity(),"UTF-8");

                System.out.println("strResult:"+strResult);
                return strResult;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post请求（用于请求json格式的参数）
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        int state = 0;
        String jsonString = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                jsonString = EntityUtils.toString(responseEntity);
                logger.error(url+"请求返回:"+jsonString);
                return jsonString;
            }
            else{
                logger.error("请求返回:"+state+"("+url+")");
            }
        }
        finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post请求（用于请求json格式的参数 加了请求头的post请求）
     * @param url
     * @param params
     * @return
     */
    public static String doPostAddHeader(String url, String params,Map<String,String> headMap) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Charset","UTF-8");
        StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
        Set<String> keys = headMap.keySet();
        httpPost.setEntity(entity);
        for (String key : keys){
            httpPost.setHeader(key, headMap.get(key));
        }
        CloseableHttpResponse response = null;
        int state = 0;
        String jsonString = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                jsonString = EntityUtils.toString(responseEntity, Charset.forName("utf-8"));
                logger.error(url+"请求返回:"+jsonString);
                return jsonString;
            }
            else{
                logger.error("请求返回:"+state+"("+url+")");
            }
        }
        finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

