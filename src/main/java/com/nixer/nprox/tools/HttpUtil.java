package com.nixer.nprox.tools;

import com.alibaba.fastjson.JSONObject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpUtil {


    public static String sendPost(String url, String Params) throws IOException {
        return qudoJsonPost(url, Params, null);
    }

    public static String sendGet(String url) throws IOException {
        return tosendGet(url, "");
    }

    public static String sendPostHander(String url, String Params, Map<String, Object> header) throws IOException {
        return qudoJsonPost(url, Params, header);
    }

    public static String doGet(String url,Boolean proxy){
       return doOne(url,"GET",null,proxy,null);
    }
    public static String doGet(String url){
        return doOne(url,"GET",null,false,null);
    }
    public static String doPost(String url,Boolean proxy){
        return doOne(url,"POST",null,proxy,null);
    }
    public static String doPost(String url){
        return doOne(url,"POST",null,false,null);
    }
    public static String doOne(String url,String methed,Object data,Boolean isproxy,Header[] headers)  {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        HttpHost proxy = new HttpHost("127.0.0.1", 7890);

        //把代理设置到请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(2000).build();
        if(isproxy){
            defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
        }
        CloseableHttpClient httpsClient = null;
        SSLContext sslContext =
                null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpsClient =
               HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        CookieStore cookieStore = new BasicCookieStore();

        BasicClientCookie cookie = new BasicClientCookie("user-has-accepted-cookies", "true");
        //放入cookiestore
        cookieStore.addCookie(cookie);
        // Create local HTTP context
        HttpContext localContext = new BasicHttpContext();
        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);


        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
//            System.setProperty("proxyHost", "127.0.0.1"); // PROXY_HOST：代理的IP地址
//            System.setProperty("proxyPort",  "7890"); // PROXY_PORT：代理的端口号
            if(methed.equals("POST")){
                HttpPost httpPost = new HttpPost(url);
                if(data!=null){
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setEntity(new StringEntity(JSONObject.toJSONString(data), ContentType.create("application/json", "utf-8")));
                }
                if(headers!=null){
                    httpPost.setHeaders(headers);
                }
                response = httpclient.execute(httpPost);
                System.out.println("==========================httpGetsend=================="+url+methed+data+isproxy);
            }else{
                if(data!=null){
                    if(headers!=null){
                        httpGet.setHeaders(headers);
                    }
                    httpGet.setHeader("Content-Type", "application/json");
                    httpGet.setHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    System.out.println("==========================httpGetsend=================="+url+methed+data+isproxy);
                    response = httpclient.execute(httpGet,localContext);
                }else{
                    HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(url);
                    httpGetWithEntity.setHeaders(headers);
                    httpGetWithEntity.setHeader("Content-Type", "application/json");
                    httpGetWithEntity.setEntity(new StringEntity(JSONObject.toJSONString(data), ContentType.create("application/json", "utf-8")));
                    response = httpclient.execute(httpGetWithEntity);
                    System.out.println("==========================httpGetsendbodydata=================="+url+methed+data+isproxy);
                }
            }

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                //System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                return EntityUtils.toString(responseEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpclient != null) {
                    httpclient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String qudoJsonPost(String url, String Params, Map<String, Object> header) {
        OutputStreamWriter out = null;
        DataOutputStream outd = null;
        OutputStream os = null;
        BufferedReader reader = null;
        String response = "";
        try {
            URL httpUrl = null; //HTTP URL类 用这个类来创建连接
            //创建URL
            httpUrl = new URL(url);
            //建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json;charset=utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("contentType", "utf-8");
            if (header != null) {
                if (header != null && !header.isEmpty()) {
                    Set<String> keys = header.keySet();
                    for (String key : keys) {
                        conn.setRequestProperty(key, String.valueOf(header.get(key)));
                    }
                }
            }

            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(Params);
            writer.close();
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), Charset.forName("utf-8"));
                response += lines;
            }
            reader.close();
            // 断开连接
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return response;
    }

    public static String HttpUtilFormGet(String url, Map<String, String> params) {
        return doHttpUtilForm(url, params, "GET");
    }


    public static String HttpUtilForm(String url, Map<String, String> params) {
        return doHttpUtilForm(url, params, "POST");
    }

    public static String doHttpUtilForm(String url, Map<String, String> params, String askType) {
        URL u = null;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb.substring(0, sb.length() - 1);
        }
        String str = "";
        if (params != null) {

            for (String key : params.keySet()) {
                str = str + key + "=" + params.get(key) + "&";
            }
            str = str.substring(0, str.length() - 1);
        }
        System.out.println("send_url:" + url);
        System.out.println("send_data:" + sb.toString());
        // 尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            //// POST 只能为大写，严格限制，post会不识别
            con.setRequestMethod(askType);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", " application/x-www-form-urlencoded;charset=UTF-8");
            //  OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            OutputStream os = con.getOutputStream();
            os.write(str.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        // 读取返回内容
        StringBuffer buffer = new StringBuffer();
        try {
            //一定要有返回值，否则无法把请求发送给server端。
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                //buffer.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


    public static String postAsyncHttpClient(String url, Map<String, Object> params, Map<String, Object> header) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding("utf-8");
        if (header != null) {
            if (header != null && !header.isEmpty()) {
                Set<String> keys = header.keySet();
                for (String key : keys) {
                    builder.addHeader(key, String.valueOf(header.get(key)));
                }
            }
        }
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addQueryParam(key, String.valueOf(params.get(key)));
            }
        }
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody("utf-8");
        http.close();
        return body;
    }

    public static String bytePost(String urlString, String Params) {

        URL url;
        PrintWriter out = null;
        StringBuilder builder = null;
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.connect();
            if (Params == null) {
                Params = "";
            }

            // os = conn.getOutputStream();
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(Params.getBytes());
            // flush输出流的缓冲
            out.flush();
            // os.write(Params.getBytes());
            //os.close();
//            Log.d("jinxn", urlString == null ? "" : urlString);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { //
                // 获取响应码
//                Log.i("响应码", conn.getResponseCode() + "");
                is = conn.getInputStream();
                Reader in = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(in);
                builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    builder.append(str);
                }
                is.close();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("jinxn", builder == null ? "" : builder.toString());
        return builder == null ? null : builder.toString();
    }


    public static String bytePost2(String urlString, String Params) {
        HttpUtil hut = new HttpUtil();
        return hut.bytePost(urlString, Params);

    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String tosendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9," +
                    "image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;" +
                    "q=0.2");
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Referer", "https://api2.chiaexplorer.com");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "PostmanRuntime/7.28.1");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String headerPost(String url,Object data, Header[] headers) {
        //String url,String methed,Object data,Boolean isproxy
       return  doOne(url,"POST",data,false,headers);
    }

    public static String headerGet(String url,Object data, Header[] headers) {
        return  doOne(url,"GET",data,false,headers);
    }
}
