
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.zarroboogs.study.net.BroserContent;
import org.zarroboogs.study.net.HttpFactory;

import com.google.gson.Gson;
import com.wolf.weibo.httpclient.javabean.HasloginBean;
import com.wolf.weibo.httpclient.javabean.LoginResultHelper;
import com.wolf.weibo.httpclient.javabean.PreLonginBean;
import com.wolf.weibo.httpclient.utils.Constaces;

public class Sina {

    private static BroserContent mBroserContent = BroserContent.getInstance();

    public static void main(String[] args) throws IOException {
        login("86898@163.com", "asd5565661234");
    }

    public static void login(String userName, String passWord) {

        HttpClient client = mBroserContent.getHttpClient();
        // 获得rsaPubkey,rsakv,servertime等参数值
        PreLonginBean params;
        try {
            params = preLogin(encodeAccount(userName), client);
            HttpPost post = doLogin(userName, passWord, params);
            HttpResponse response = client.execute(post);
            LoginResultHelper helper = new LoginResultHelper(response.getEntity());
            System.out.println("登陆是否成功了：  " + helper.isLogin());
            if (helper.isLogin()) {
                HttpGet userPageGet = HttpFactory.createHttpGet(helper.getUserPageUrl(), null);
                HttpResponse userPageResponse = client.execute(userPageGet);
                String responseString = EntityUtils.toString(userPageResponse.getEntity(), "GBK");

                Gson gson = new Gson();
                String loginResponse = getJsonString(responseString) + "}";
                System.out.println("json: " + loginResponse);

                HasloginBean hasloginBean = gson.fromJson(loginResponse, HasloginBean.class);
                
                sendWeibo(mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", "6gBvZH", "11" + new Date().getTime(), null, null);
			} else {
				System.out.println(helper.getResponseString());
				System.out.println(helper.getErrorReason());
			}
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }

	private static HttpPost doLogin(String userName, String passWord,
			PreLonginBean params) throws ScriptException, NoSuchMethodException {
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
		nvps.add(new BasicNameValuePair("su", encodeAccount(userName)));
		nvps.add(new BasicNameValuePair("service", "miniblog"));
		nvps.add(new BasicNameValuePair("servertime", params.getServertime() + ""));
		nvps.add(new BasicNameValuePair("nonce", params.getNonce()));
		nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
		nvps.add(new BasicNameValuePair("rsakv", params.getRsakv()));
		nvps.add(new BasicNameValuePair("sp", getRsaPassWord(passWord, params)));
		nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
		nvps.add(new BasicNameValuePair("prelt", "166"));
		nvps.add(new BasicNameValuePair("url",
		        "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		nvps.add(new BasicNameValuePair("returntype", "META"));

		HttpPost post = HttpFactory.createHttpPost(Constaces.LOGIN_FIRST_URL, headers, nvps);
		return post;
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

    private static String getRsaPassWord(String p, PreLonginBean params) throws ScriptException, NoSuchMethodException {
        PwdEncodeJSRunner pwdEncodeJSRunner = new PwdEncodeJSRunner();
        String test = pwdEncodeJSRunner.getRsaPassWord(p, params);
        System.out.println("FromJS======== [" + test);
        return test;
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
