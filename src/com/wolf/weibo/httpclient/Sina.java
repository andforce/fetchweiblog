
package com.wolf.weibo.httpclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
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
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.zarroboogs.study.net.BroserContent;
import org.zarroboogs.study.net.HttpFactory;
import org.zarroboogs.study.net.ResponseUtils;

import com.google.gson.Gson;
import com.wolf.weibo.httpclient.javabean.Constaces;
import com.wolf.weibo.httpclient.javabean.HasloginBean;
import com.wolf.weibo.httpclient.javabean.PreLonginBean;

public class Sina {

    private static BroserContent mBroserContent = BroserContent.getInstance();

    public static void main(String[] args) throws IOException {
        WeiBoUser user = login("86118@163.com", "Andforce!@#");
        System.out.println(user.getUserName());
    }

    public static WeiBoUser login(String u, String p) {

        HttpClient client = mBroserContent.getHttpClient();
        
        Header[] getHeader = {
        		new BasicHeader("Host", "i.sso.sina.com.cn"),
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("If-Modified-Since", "Fri, 08 Aug 2014 05:57:32 GMT"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
                new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=3G5oUM"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
                };
        
        
        HttpGet httpGet = HttpFactory.createHttpGet("http://i.sso.sina.com.cn/js/ssologin.js", getHeader);
        try {
			client.execute(httpGet);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        WeiBoUser user = null;
        try {
            // 获得rsaPubkey,rsakv,servertime等参数值
            PreLonginBean params = preLogin(encodeAccount(u), client);

            Header[] postHeader = {
            		new BasicHeader("Host", "login.sina.com.cn"),
            		new BasicHeader("Cache-Control", "max-age=0"),
                    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                    new BasicHeader("Origin", "http://widget.weibo.com"),
                    new BasicHeader("User-Agent", Constaces.User_Agent),
                    new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
                    new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=3G5oUM"),
                    new BasicHeader("Accept-Encoding", "gzip,deflate"),
                    new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
//                    new BasicHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7"),
                    //new BasicHeader("Referer", "http://weibo.com/?c=spr_web_sq_firefox_weibo_t001"),
                    
            };

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
            nvps.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
            nvps.add(new BasicNameValuePair("returntype", "META"));

            HttpPost post = HttpFactory.createHttpPost(Constaces.LOGIN_FIRST_URL,postHeader, nvps);
            HttpResponse response = client.execute(post);
            String entity = ResponseUtils.getResponseLines(true, response, "GBK");//EntityUtils.toString(response.getEntity(), "GBK");
            System.out.println("\r\r执行加密登陆：\r" + entity);

            // try{sinaSSOController.setCrossDomainUrlList({"retcode":0,"arrURL":["http:\/\/crosdom.weicaifu.com\/sso\/crosdom?action=login"]});}catch(e){}
            // try{sinaSSOController.crossDomainAction('login',function(){location.replace('http://passport.weibo.com/wbsso/login?url=http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3Fframelogin%3D1%26callback%3Dparent.sinaSSOController.feedBackUrlCallBack%26sudaref%3Dweibo.com&ticket=ST-MTg3ODIzMDA3NQ==-1416290371-xd-DA438CD75651470DE3717B9B3E4B5EE9&retcode=0');});}catch(e){}
//http://crosdom.weicaifu.com/sso/crosdom?action=login
            String splitby = "url=";
            if (true) {
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
                //System.out.println(entity);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");

                //mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", app_src, textContent, mCookie, pid
                List<Cookie> cookies = mBroserContent.getCookieStore().getCookies();
                String cookieString = "";
                for (Cookie cookie : cookies) {
                    cookieString = cookieString + cookie.getName() + "=" + cookie.getValue() + "; ";
                }
                sendWeibo(mBroserContent, "http://widget.weibo.com/public/aj_addMblog.php", "3G5oUM", "123456789", cookieString, null);
                
                
                Document doc = Jsoup.parse(entity);
                Elements els = doc.select("script");

                if (els != null && els.size() > 0) {
                    for (int i = 0, leg = els.size(); i < leg; i++) {

                        if (els.get(i).html().indexOf("$CONFIG") > -1) {
                            strScr = els.get(i).html();
                            break;
                        }
                    }
                }

                if (!strScr.equals("")) {
                    ScriptEngineManager manager = new ScriptEngineManager();
                    ScriptEngine engine = manager.getEngineByName("javascript");

                    engine.eval("function getMsg(){" + strScr
                            + "return $CONFIG['onick'];}");
                    if (engine instanceof Invocable) {
                        Invocable invoke = (Invocable) engine;
                        // 调用preprocess方法，并传入两个参数密码和验证码

                        nick = invoke.invokeFunction("getMsg", null).toString();

                    }
                }

                user = new WeiBoUser();
                user.setUserName(u);
                user.setUserPass(p);
                user.setDisplayName(nick);

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            // logger.info(e.getMessage());
            user = null;
        }

        return user;
    }

    
    public static boolean sendWeibo(BroserContent broserContent, String url, String app_src, String content, String cookie, String pid) {
        CloseableHttpClient httpClient = broserContent.getHttpClient();
        // http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=3G5oUM
        // http://widget.weibo.com/public/aj_addMblog.php
        Header[] loginHeaders = {
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Accept-Encoding", "gzip, deflate"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
                // new BasicHeader("Cache-Control", "max-age=0"),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
                new BasicHeader("Host", "widget.weibo.com"),
                new BasicHeader("Origin", "http://widget.weibo.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest"),
                new BasicHeader("Cookie", cookie),
                new BasicHeader("Referer", "http://widget.weibo.com/topics/topic_vote_base.php?" + "tag=Weibo&app_src=" + app_src
                        + "&isshowright=0&language=zh_cn"),
                new BasicHeader("User-Agent", Constaces.User_Agent), };

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

        HttpPost logInPost = HttpFactory.createHttpPost(url, loginHeaders, loginParams);

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
                    params.getNonce(),params.getPubkey()).toString();
            System.out.println("加密之后的密码是： " + pass);
        }
        return pass;
    }

    private static String buildPreLoginUrl(String su, String ssoLoginjs ,String time){
        String url = "http://login.sina.com.cn/sso/prelogin.php?";
        url = url + "entry=weibo&";
        url = url + "callback=sinaSSOController.preloginCallBack&";
        url = url + "su=" + su + "&";
        url = url + "rsakt=mod&";
        url = url + "client="+ ssoLoginjs + "&";
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
//        1416557391245
        long time = new Date().getTime();
        String url = buildPreLoginUrl(unameBase64, Constaces.SSOLOGIN_JS, time + "");

        Header[] getHeader = {
        		new BasicHeader("Host", "login.sina.com.cn"),
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
                new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public&app_src=3G5oUM"),
                new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"),
                new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"),
        };
        
        HttpGet httpGet = HttpFactory.createHttpGet(url, getHeader);
        HttpResponse httpResponse = client.execute(httpGet);
        String result = ResponseUtils.getResponseLines(true, httpResponse, "GBK");
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

    /**
     * 打印页面
     * @param entity
     * @throws IOException
     */
    private static String dump(HttpEntity entity) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf8"));
        return IOUtils.toString(br);
    }

    public static boolean Share(String u, String p, String content, String pic, String surl) {
        HttpClient client = mBroserContent.getHttpClient();

        try {
            HttpPost post = new HttpPost(
                    Constaces.LOGIN_FIRST_URL);

            String data = getServerTime();

            String nonce = makeNonce(6);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("entry", "weibo"));
            nvps.add(new BasicNameValuePair("gateway", "1"));
            nvps.add(new BasicNameValuePair("from", ""));
            nvps.add(new BasicNameValuePair("savestate", "7"));
            nvps.add(new BasicNameValuePair("useticket", "1"));
            nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
            nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
            nvps.add(new BasicNameValuePair("service", "miniblog"));
            nvps.add(new BasicNameValuePair("servertime", data));
            nvps.add(new BasicNameValuePair("nonce", nonce));
            nvps.add(new BasicNameValuePair("pwencode", "wsse"));
            nvps.add(new BasicNameValuePair("sp", new SinaSSOEncoder().encode(
                    p, data, nonce)));

            nvps
                    .add(new BasicNameValuePair(
                            "url",
                            "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
            nvps.add(new BasicNameValuePair("returntype", "META"));
            nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
            nvps.add(new BasicNameValuePair("vsnf", "1"));
            nvps.add(new BasicNameValuePair("prelt", "1021"));

            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = client.execute(post);
            String entity = EntityUtils.toString(response.getEntity());
            System.out.println(entity);
            if (entity.replace("\"", "").indexOf("retcode=0") > -1) {

                String url = entity.substring(entity
                        .indexOf("http://weibo.com/ajaxlogin.php?"), entity
                        .indexOf("code=0") + 6);

                // 获取到实际url进行连接
                HttpGet getMethod = new HttpGet(url);

                response = client.execute(getMethod);
                entity = EntityUtils.toString(response.getEntity());
                System.out.println(entity);
                entity = entity.substring(entity.indexOf("userdomain") + 13,
                        entity.lastIndexOf("\""));
                System.out.println(entity);

                /*************************************************************************************/

                post = new HttpPost("http://v.t.sina.com.cn/share/aj_share.php");
                post
                        .addHeader(
                                "Referer",
                                "http://v.t.sina.com.cn/share/share.php?url=http%3A%2F%2Fnews.sina.com.cn%2Fc%2F2012-03-31%2F074424204961.shtml&title=%E5%B1%B1%E4%B8%9C%E6%BB%95%E5%B7%9E%E6%9D%91%E6%B0%91%E5%9B%A0%E7%8B%BC%E5%92%AC%E4%BA%BA%E4%BA%8B%E4%BB%B6%E7%95%99%E5%BF%83%E7%90%86%E9%98%B4%E5%BD%B1&ralateUid=1618051664&source=%E6%96%B0%E6%B5%AA%E6%96%B0%E9%97%BB&sourceUrl=http%3A%2F%2Fnews.sina.com.cn%2F&content=gb2312&pic=http%3A%2F%2Fi1.sinaimg.cn%2Fdy%2Fcr%2F2012%2F0331%2F1728605356.jpg");

                nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("appkey", "1671520477"));
                nvps.add(new BasicNameValuePair("content", content));
                nvps.add(new BasicNameValuePair("from", "share"));
                nvps.add(new BasicNameValuePair("refer", ""));
                nvps.add(new BasicNameValuePair("share_pic", pic));
                nvps.add(new BasicNameValuePair("source", ""));
                nvps.add(new BasicNameValuePair("sourceUrl", surl));
                nvps.add(new BasicNameValuePair("styleid", "1"));
                nvps.add(new BasicNameValuePair("url_type", "0"));
                post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                response = client.execute(post);

                entity = EntityUtils.toString(response.getEntity());
                if (entity.replace("\"", "").indexOf("code:A00006") > -1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }

    }

    public static boolean AddW(String u, String p, String text, byte[] pic) {
        HttpClient client = mBroserContent.getHttpClient();

        try {
            PreLonginBean params = preLogin(encodeAccount(u), client);

            HttpPost post = new HttpPost(
                    Constaces.LOGIN_FIRST_URL);
            post
                    .setHeader("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post
                    .setHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 5.1; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");

            post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            post.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
            post.setHeader("Referer",
                    "http://weibo.com/?c=spr_web_sq_firefox_weibo_t001");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            String nonce = makeNonce(6);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
            nvps.add(new BasicNameValuePair("entry", "weibo"));
            nvps.add(new BasicNameValuePair("from", ""));
            nvps.add(new BasicNameValuePair("gateway", "1"));
            nvps.add(new BasicNameValuePair("nonce", nonce));
            nvps.add(new BasicNameValuePair("pagerefer", "http://i.firefoxchina.cn/old/"));
            nvps.add(new BasicNameValuePair("prelt", "111"));
            nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
            nvps.add(new BasicNameValuePair("returntype", "META"));
            nvps.add(new BasicNameValuePair("rsakv", params.getRsakv()));
            nvps.add(new BasicNameValuePair("savestate", "0"));
            nvps.add(new BasicNameValuePair("servertime", params.getServertime() + ""));

            nvps.add(new BasicNameValuePair("service", "miniblog"));
            // nvps.add(new BasicNameValuePair("sp", new SinaSSOEncoder().encode(p, data, nonce)));

            /******************** *加密密码 ***************************/
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("javascript");
            // FileReader f = new FileReader("d://sso.js");
            se.eval(SinaSSOEncoder.getJs());
            String pass = "";

            if (se instanceof Invocable) {
                Invocable invoke = (Invocable) se;
                // 调用preprocess方法，并传入两个参数密码和验证码

                pass = invoke.invokeFunction("getpass",
                        p, params.getServertime() + "", nonce, params.getPubkey()).toString();

                System.out.println("c = " + pass);
            }

            nvps.add(new BasicNameValuePair("sp", pass));
            nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
            nvps
                    .add(new BasicNameValuePair(
                            "url",
                            "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));

            nvps.add(new BasicNameValuePair("useticket", "1"));
            // nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
            nvps.add(new BasicNameValuePair("vsnf", "1"));

            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = client.execute(post);

            String entity = EntityUtils.toString(response.getEntity());

            if (entity.replace("\"", "").indexOf("retcode=0") > -1) {
                String url = entity.substring(entity
                        .indexOf("http://weibo.com/sso/login.php?"), entity
                        .indexOf("code=0") + 6);

                // 获取到实际url进行连接
                HttpGet getMethod = new HttpGet(url);
                response = client.execute(getMethod);
                entity = EntityUtils.toString(response.getEntity());
                System.out.println(entity);

                // 获取uid
                String uid = entity.substring(entity.indexOf("uniqueid") + 11,
                        entity.indexOf("userid") - 3);

                // 获取昵称
                String nick = "";
                getMethod = new HttpGet("http://weibo.com/?wvr=3.6&lf=reg");
                response = client.execute(getMethod);
                entity = EntityUtils.toString(response.getEntity());
                Document doc = Jsoup.parse(entity);
                Elements e = doc.getElementsByClass("person_infos");
                if (e != null && e.size() > 0) {
                    nick = e.get(0).text();
                    System.out.println("昵称：" + nick);
                }

                String pid = "";
                if (pic != null && pic.length > 0) {
                    // 上传图片
                    post = new HttpPost(
                            "http://picupload.service.weibo.com/interface/pic_upload.php?cb=http%3A%2F%2Fweibo.com%2Faj%2Fstatic%2Fupimgback.html%3Fcallback%3DSTK_ijax_133939711612881&url=weibo.com%2Fu%2F"
                                    + uid
                                    + "&markpos=1&logo=1&nick=%40"
                                    + nick
                                    + "&marks=1&app=miniblog&s=rdxt");

                    post.setHeader("Host", "picupload.service.weibo.com");
                    post.setHeader("Referer", "http://weibo.com/u/" + uid);
                    ByteArrayBody bin = new ByteArrayBody(pic, "image/jpeg",
                            "verifycode.png");
                    MultipartEntity reqEntity = new MultipartEntity();
                    reqEntity.addPart("pic1", bin);
                    post.setEntity(reqEntity);
                    response = client.execute(post);

                    org.apache.http.Header[] hs = response
                            .getHeaders("Location");
                    System.out.println(hs[0].getValue());
                    String picLocation = hs[0].getValue();
                    pid = picLocation.substring(picLocation.indexOf("pid") + 4,
                            picLocation.indexOf("token") - 1);
                    System.out.println("pid: " + pid);
                }

                /*************************************************************************************/

                post = new HttpPost("http://weibo.com/aj/mblog/add?_rnd="
                        + System.currentTimeMillis());
                post.addHeader("Referer", "http://weibo.com/u/" + uid + "");
                nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("_surl", ""));
                nvps.add(new BasicNameValuePair("_t", "0"));
                nvps.add(new BasicNameValuePair("hottopicid", ""));
                nvps.add(new BasicNameValuePair("location", "home"));
                nvps.add(new BasicNameValuePair("module", "stissue"));
                nvps.add(new BasicNameValuePair("pic_id", pid));
                nvps.add(new BasicNameValuePair("rank", "0"));
                nvps.add(new BasicNameValuePair("rankid", ""));
                nvps.add(new BasicNameValuePair("text", text));
                post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                response = client.execute(post);

                entity = EntityUtils.toString(response.getEntity());
                System.out.println(entity);
                if (entity.replace("\"", "").indexOf("code:100000") > -1) {
                    System.out.println("发布成功");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static byte[] readFileImage(String filename) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream(filename));
        int len = bufferedInputStream.available();
        byte[] bytes = new byte[len];
        int r = bufferedInputStream.read(bytes);
        if (len != r) {
            bytes = null;
            throw new IOException("读取文件不正确");
        }
        bufferedInputStream.close();
        return bytes;
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

    private static String makeNonce(int len) {
        String x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str = "";
        for (int i = 0; i < len; i++) {
            str += x.charAt((int) (Math.ceil(Math.random() * 1000000) % x
                    .length()));
        }
        return str;
    }

    private static String getServerTime() {
        // long servertime = new Date().getTime() / 1000;
        // return String.valueOf(servertime);

        return String.valueOf(System.currentTimeMillis() / 1000);
    }

}
