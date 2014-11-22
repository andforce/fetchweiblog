
package com.wolf.weibo.httpclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.zarroboogs.study.net.BroserContent;
import org.zarroboogs.study.net.HttpFactory;

import com.google.gson.Gson;
import com.wolf.weibo.httpclient.javabean.Constaces;
import com.wolf.weibo.httpclient.javabean.HasloginBean;
import com.wolf.weibo.httpclient.javabean.PreLonginBean;

public class Sina {

    private static BroserContent mBroserContent = BroserContent.getInstance();

    public static void main(String[] args) throws IOException {
        login("86898@163.com", "asd556566123");
    }

    public static void login(String u, String p) {

        HttpClient client = mBroserContent.getHttpClient();
        // 获得rsaPubkey,rsakv,servertime等参数值
        PreLonginBean params;
        try {
            params = preLogin(encodeAccount(u), client);
            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Host", "login.sina.com.cn"));
            headers.add(new BasicHeader("Cache-Control", "max-age=0"));
            headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
            headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
            headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));
            headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
            headers.add(new BasicHeader("Referer",
                    "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH"));
            headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
            headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("entry", "weibo"));
            nvps.add(new BasicNameValuePair("gateway", "1"));
            nvps.add(new BasicNameValuePair("from", ""));
            nvps.add(new BasicNameValuePair("savestate", "7"));
            nvps.add(new BasicNameValuePair("useticket", "1"));
            nvps.add(new BasicNameValuePair("pagerefer", ""));
            nvps.add(new BasicNameValuePair("vsnf", "1"));
            nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
            nvps.add(new BasicNameValuePair("service", "miniblog"));
            nvps.add(new BasicNameValuePair("servertime", params.getServertime() + ""));
            nvps.add(new BasicNameValuePair("nonce", params.getNonce()));
            nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
            nvps.add(new BasicNameValuePair("rsakv", params.getRsakv()));
            nvps.add(new BasicNameValuePair("sp", getPassWord(p, params)));
            nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
            nvps.add(new BasicNameValuePair("prelt", "166"));
            nvps.add(new BasicNameValuePair("url",
                    "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
            nvps.add(new BasicNameValuePair("returntype", "META"));

            HttpPost post = HttpFactory.createHttpPost(Constaces.LOGIN_FIRST_URL, headers, nvps);

            HttpResponse response = client.execute(post);

            String entity = EntityUtils.toString(response.getEntity(), "GBK");
            System.out.println("\r\r执行加密登陆：\r" + entity);

            // try{sinaSSOController.setCrossDomainUrlList({"retcode":0,"arrURL":["http:\/\/crosdom.weicaifu.com\/sso\/crosdom?action=login"]});}catch(e){}
            // try{sinaSSOController.crossDomainAction('login',function(){location.replace('http://passport.weibo.com/wbsso/login?url=http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3Fframelogin%3D1%26callback%3Dparent.sinaSSOController.feedBackUrlCallBack%26sudaref%3Dweibo.com&ticket=ST-MTg3ODIzMDA3NQ==-1416290371-xd-DA438CD75651470DE3717B9B3E4B5EE9&retcode=0');});}catch(e){}
            // http://crosdom.weicaifu.com/sso/crosdom?action=login
            String splitby = "url=";

            String tmp = entity.split(splitby)[1];
            String url = tmp.split("retcode=0")[0] + "retcode=0";

            String strScr = ""; // 首页用户script形式数据
            String nick = "暂无"; // 昵称

            url = URLDecoder.decode(URLDecoder.decode(url));
            System.out.println("\r\rRealUrl : " + url);
            // 获取到实际url进行连接
            HttpGet getMethod = new HttpGet(url);

            response = client.execute(getMethod);
            entity = EntityUtils.toString(response.getEntity(), "GBK");
            System.out.println("HAS_LOGIN: " + entity);

            Gson gson = new Gson();
            String loginResponse = getJsonString(entity) + "}";
            System.out.println("json: " + loginResponse);

            HasloginBean hasloginBean = gson.fromJson(loginResponse, HasloginBean.class);

            nick = hasloginBean.getUserinfo().getDisplayname();
            url = hasloginBean.getUserinfo().getUserdomain();

            getMethod = new HttpGet("http://weibo.com/" + url);
            response = client.execute(getMethod);
            entity = EntityUtils.toString(response.getEntity());
            // System.out.println(entity);

            // mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", app_src,
            // textContent, mCookie, pid
            List<Cookie> cookies = mBroserContent.getCookieStore().getCookies();
            String cookieString = "";
            for (Cookie cookie : cookies) {
                cookieString = cookieString + cookie.getName() + "=" + cookie.getValue() + "; ";
            }
            System.out.println(cookieString);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
            sendWeibo(mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", "6gBvZH", "11", cookieString, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }

    public static boolean sendWeibo(BroserContent broserContent, String url, String app_src, String content, String cookie,
            String pid) {
        CloseableHttpClient httpClient = broserContent.getHttpClient();
        // http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH
        // http://widget.weibo.com/public/aj_addMblog.php

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headers.add(new BasicHeader("Host", "widget.weibo.com"));
        headers.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headers.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        headers.add(new BasicHeader("Referer", "http://widget.weibo.com/topics/topic_vote_base.php?" + "tag=Weibo&app_src="
                + app_src
                + "&isshowright=0&language=zh_cn"));
        headers.add(new BasicHeader("User-Agent", Constaces.User_Agent));

        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("app_src", app_src));
        loginParams.add(new BasicNameValuePair("content", content));
        if (!TextUtils.isEmpty(pid)) {
            loginParams.add(new BasicNameValuePair("pic_id", pid));
        }
        loginParams.add(new BasicNameValuePair("return_type", "2"));
        loginParams.add(new BasicNameValuePair("refer", ""));
        loginParams.add(new BasicNameValuePair("vsrc", "base_topic"));
        loginParams.add(new BasicNameValuePair("wsrc", "app_topic_base"));
        loginParams.add(new BasicNameValuePair("ext", "login=>1;url=>"));
        loginParams.add(new BasicNameValuePair("html_type", "2"));
        loginParams.add(new BasicNameValuePair("_t", "0"));
        // loginParams.add(new BasicNameValuePair("Cookie", cookie));

        HttpPost logInPost = HttpFactory.createHttpPost(url, headers, loginParams);

        // logInPost.addHeader("Cookie", cookie);
        CloseableHttpResponse logInResponse = null;
        String allResponse = "";
        boolean isSuccess = false;
        try {
            logInResponse = httpClient.execute(logInPost);
            HttpEntity mEntity = logInResponse.getEntity();
            if (mEntity != null) {
                InputStream in;
                try {
                    in = mEntity.getContent();
                    String str = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    while ((str = br.readLine()) != null) {
                        allResponse += str;
                    }

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error IllegalStateException----");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("Error IOException");
                }
            }

            System.out.println("==========================send resule:\r\n" + allResponse);
            Gson gson = new Gson();
            WeiBoPostResult result = gson.fromJson(allResponse, WeiBoPostResult.class);
            if (logInResponse.getStatusLine().getStatusCode() == 200) {
                if (result != null && result.getCode() == 100000) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccess = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                if (logInResponse != null) {
                    logInResponse.close();
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static class WeiBoPostResult implements java.io.Serializable {
        private static final long serialVersionUID = 2670736249286930507L;
        private int code = 0;
        private String msg = "";

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }

    private static String getPassWord(String p, PreLonginBean params) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        // FileReader f = new FileReader("d://sso.js");
        se.eval(SinaSSOEncoder.getJs());
        String pass = "";
        if (se instanceof Invocable) {
            Invocable invoke = (Invocable) se;
            // 调用preprocess方法，并传入两个参数密码和验证码
            pass = invoke.invokeFunction("getpass", p, params.getServertime() + "",
                    params.getNonce(), params.getPubkey()).toString();
            System.out.println("加密之后的密码是： " + pass);
        }
        return pass;
    }

    private static String buildPreLoginUrl(String su, String ssoLoginjs, String time) {
        String url = "http://login.sina.com.cn/sso/prelogin.php?";
        url = url + "entry=weibo&";
        url = url + "callback=sinaSSOController.preloginCallBack&";
        url = url + "su=" + su + "&";
        url = url + "rsakt=mod&";
        url = url + "client=" + ssoLoginjs + "&";
        // time = new Date().getTime();
        url = url + "_=" + time;
        return url;

    }

    /**
     * 新浪微博预登录，获取密码加密公钥
     * @param unameBase64
     * @return 返回从结果获取的参数的哈希表
     * @throws IOException
     */
    private static PreLonginBean preLogin(String unameBase64, HttpClient client) throws IOException {
        // 1416557391245
        long time = new Date().getTime();
        String url = buildPreLoginUrl(unameBase64, Constaces.SSOLOGIN_JS, time + "");

        Header[] getHeader = {
                new BasicHeader("Host", "login.sina.com.cn"),
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
                new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=6gBvZH"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
        };

        HttpGet httpGet = HttpFactory.createHttpGet(url, getHeader);
        HttpResponse httpResponse = client.execute(httpGet);
        String result = EntityUtils.toString(httpResponse.getEntity(), "GBK");// ResponseUtils.getResponseLines(true,
                                                                              // httpResponse,
                                                                              // "GBK");
        result = getJsonString(result);
        System.out.println("Pre Result : " + result);
        Gson gson = new Gson();
        PreLonginBean preLonginBean = gson.fromJson(result, PreLonginBean.class);
        System.out.println("\r----------------------------------------------------");
        System.out.println("exectime:        " + preLonginBean.getExectime());
        System.out.println("nonce:        " + preLonginBean.getNonce());
        System.out.println("pcid        " + preLonginBean.getPcid());
        System.out.println("pubkey        " + preLonginBean.getPubkey());
        System.out.println("retcode        " + preLonginBean.getRetcode());
        System.out.println("rsakey        " + preLonginBean.getRsakv());
        System.out.println("servertime        " + preLonginBean.getServertime());
        System.out.println("----------------------------------------------------\r");
        return preLonginBean;

    }

    private static String getJsonString(String result) {
        Pattern p = Pattern.compile("\\{([^)]*?)\\}");
        Matcher matcher = p.matcher(result);
        if (matcher.find()) {
            result = matcher.group();
        }
        return result;
    }

    private static String encodeAccount(String account) {
        String userName = "";
        try {
            userName = Base64.encodeBase64String(URLEncoder.encode(account, "UTF-8").getBytes());
            // userName = BASE64Encoder.encode(URLEncoder.encode(account,"UTF-8").getBytes());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("SU==================" + userName);
        return userName;
    }

}
