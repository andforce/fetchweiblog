package com.wolf.weibo.httpclient.javabean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

public class LoginResultHelper {

	private boolean mIsLogin = false;
	private String mErrorReason = "";
	private String mResponseString;
	private String mUserPageUrl;

	public boolean isLogin() {
		return mIsLogin;
	}

	public String getErrorReason() {
		return mErrorReason;
	}

	public String getResponseString() {
		return this.mResponseString;
	}

	public String getUserPageUrl() {
		if (mIsLogin) {
			String regx = "url=";
			String tmp = getResponseString().split(regx)[1];
			String url = tmp.split("retcode=0")[0] + "retcode=0";
			try {
				url = URLDecoder.decode(url, "GBK");
				url = URLDecoder.decode(url, "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return url;
		}
		return null;
	}

	public LoginResultHelper(HttpEntity entity) {
		try {
			String entityString = EntityUtils.toString(entity, "GBK");
			if (entityString.contains("\"retcode\":0")) {
				mIsLogin = true;
			} else if (entityString
					.contains("reason=%CE%AA%C1%CB%C4%FA%B5%C4%D5%CA%BA%C5%B0%B2%C8%AB%A3%AC%C7%EB%CA%E4%C8%EB%D1%E9%D6%A4%C2%EB")) {
				// 为了您的帐号安全，请输入验证码
				mIsLogin = false;
				mErrorReason = "为了您的帐号安全，请输入验证码";
			} else if (entityString.contains("%B5%C7%C2%BC%C3%FB%BB%F2%C3%DC%C2%EB%B4%ED%CE%F3")) {
				// 用户名或密码错误
				mIsLogin = false;
				mErrorReason = "用户名或密码错误";
			}
			this.mResponseString = entityString;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
