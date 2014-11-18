package com.wolf.weibo.httpclient;

import java.io.BufferedInputStream;

import java.io.FileInputStream;

import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;

import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TencentClient {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
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

	/**
	 * 登录
	 * 
	 * @param uin
	 * @param p
	 * @return
	 */
	public static WeiBoUser login(String uin, String p) {
		WeiBoUser u = null;
		DefaultHttpClient client = new DefaultHttpClient(
				new ThreadSafeClientConnManager());

		try {

			/********************* 获取验证码 ***********************/
			HttpGet get = new HttpGet("http://check.ptlogin2.qq.com/check?uin="
					+ uin + "&appid=46000101&ptlang=2052&r=" + Math.random());
			get.setHeader("Host", "check.ptlogin2.qq.com");
			get.setHeader("Referer", "http://t.qq.com/?from=11");

			get
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0");
			HttpResponse response = client.execute(get);

			String entity = EntityUtils.toString(response.getEntity());
			String[] checkNum = entity.substring(entity.indexOf("(") + 1,
					entity.lastIndexOf(")")).replace("'", "").split(",");
			System.out.println(checkNum[0]);
			System.out.println(checkNum[1]);
			System.out.println(checkNum[2].trim());
			System.out.println(checkNum[2].trim().replace("\\x", ""));
			String pass = "";

			/******************** *加密密码 ***************************/
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");

			engine
					.eval("var hexcase = 1;var b64pad = \"\";var chrsz = 8;var mode = 32;function md5(A) {return hex_md5(A)}function hex_md5(A) {return binl2hex(core_md5(str2binl(A), A.length * chrsz))}"
							+ "function str_md5(A) {return binl2str(core_md5(str2binl(A), A.length * chrsz))}function hex_hmac_md5(A, B) {return binl2hex(core_hmac_md5(A, B))}function b64_hmac_md5(A, B) {return binl2b64(core_hmac_md5(A, B))}"
							+ "function str_hmac_md5(A, B) {return binl2str(core_hmac_md5(A, B))}"
							+ "function core_md5(K, F) {K[F >> 5] |= 128 << ((F) % 32);K[(((F + 64) >>> 9) << 4) + 14] = F;var J = 1732584193;var I = -271733879;var H = -1732584194;var G = 271733878;"
							+ "for (var C = 0; C < K.length; C += 16) {var E = J;var D = I;var B = H;var A = G;J = md5_ff(J, I, H, G, K[C + 0], 7, -680876936);G = md5_ff(G, J, I, H, K[C + 1], 12, -389564586);"
							+ "H = md5_ff(H, G, J, I, K[C + 2], 17, 606105819);I = md5_ff(I, H, G, J, K[C + 3], 22, -1044525330);J = md5_ff(J, I, H, G, K[C + 4], 7, -176418897);G = md5_ff(G, J, I, H, K[C + 5], 12, 1200080426);H = md5_ff(H, G, J, I, K[C + 6], 17, -1473231341);"
							+ "I = md5_ff(I, H, G, J, K[C + 7], 22, -45705983);J = md5_ff(J, I, H, G, K[C + 8], 7, 1770035416);G = md5_ff(G, J, I, H, K[C + 9], 12, -1958414417);H = md5_ff(H, G, J, I, K[C + 10], 17, -42063);"
							+ "I = md5_ff(I, H, G, J, K[C + 11], 22, -1990404162);J = md5_ff(J, I, H, G, K[C + 12], 7, 1804603682);G = md5_ff(G, J, I, H, K[C + 13], 12, -40341101);H = md5_ff(H, G, J, I, K[C + 14], 17, -1502002290);I = md5_ff(I, H, G, J, K[C + 15], 22, 1236535329);"
							+ "J = md5_gg(J, I, H, G, K[C + 1], 5, -165796510);G = md5_gg(G, J, I, H, K[C + 6], 9, -1069501632);H = md5_gg(H, G, J, I, K[C + 11], 14, 643717713);I = md5_gg(I, H, G, J, K[C + 0], 20, -373897302);"
							+ "J = md5_gg(J, I, H, G, K[C + 5], 5, -701558691);G = md5_gg(G, J, I, H, K[C + 10], 9, 38016083);H = md5_gg(H, G, J, I, K[C + 15], 14, -660478335);I = md5_gg(I, H, G, J, K[C + 4], 20, -405537848);"
							+ "J = md5_gg(J, I, H, G, K[C + 9], 5, 568446438);G = md5_gg(G, J, I, H, K[C + 14], 9, -1019803690);H = md5_gg(H, G, J, I, K[C + 3], 14, -187363961);I = md5_gg(I, H, G, J, K[C + 8], 20, 1163531501);"
							+ "J = md5_gg(J, I, H, G, K[C + 13], 5, -1444681467);G = md5_gg(G, J, I, H, K[C + 2], 9, -51403784);H = md5_gg(H, G, J, I, K[C + 7], 14, 1735328473);I = md5_gg(I, H, G, J, K[C + 12], 20, -1926607734);"
							+ "J = md5_hh(J, I, H, G, K[C + 5], 4, -378558);G = md5_hh(G, J, I, H, K[C + 8], 11, -2022574463);H = md5_hh(H, G, J, I, K[C + 11], 16, 1839030562);I = md5_hh(I, H, G, J, K[C + 14], 23, -35309556);"
							+ "J = md5_hh(J, I, H, G, K[C + 1], 4, -1530992060);G = md5_hh(G, J, I, H, K[C + 4], 11, 1272893353);H = md5_hh(H, G, J, I, K[C + 7], 16, -155497632);I = md5_hh(I, H, G, J, K[C + 10], 23, -1094730640);"
							+ "J = md5_hh(J, I, H, G, K[C + 13], 4, 681279174);G = md5_hh(G, J, I, H, K[C + 0], 11, -358537222);H = md5_hh(H, G, J, I, K[C + 3], 16, -722521979);I = md5_hh(I, H, G, J, K[C + 6], 23, 76029189);J = md5_hh(J, I, H, G, K[C + 9], 4, -640364487);G = md5_hh(G, J, I, H, K[C + 12], 11, -421815835);H = md5_hh(H, G, J, I, K[C + 15], 16, 530742520);"
							+ "I = md5_hh(I, H, G, J, K[C + 2], 23, -995338651);J = md5_ii(J, I, H, G, K[C + 0], 6, -198630844);G = md5_ii(G, J, I, H, K[C + 7], 10, 1126891415);H = md5_ii(H, G, J, I, K[C + 14], 15, -1416354905);"
							+ "I = md5_ii(I, H, G, J, K[C + 5], 21, -57434055);J = md5_ii(J, I, H, G, K[C + 12], 6, 1700485571);G = md5_ii(G, J, I, H, K[C + 3], 10, -1894986606);H = md5_ii(H, G, J, I, K[C + 10], 15, -1051523);I = md5_ii(I, H, G, J, K[C + 1], 21, -2054922799);"
							+ "J = md5_ii(J, I, H, G, K[C + 8], 6, 1873313359);G = md5_ii(G, J, I, H, K[C + 15], 10, -30611744);H = md5_ii(H, G, J, I, K[C + 6], 15, -1560198380);I = md5_ii(I, H, G, J, K[C + 13], 21, 1309151649);"
							+ "J = md5_ii(J, I, H, G, K[C + 4], 6, -145523070);G = md5_ii(G, J, I, H, K[C + 11], 10, -1120210379);H = md5_ii(H, G, J, I, K[C + 2], 15, 718787259);I = md5_ii(I, H, G, J, K[C + 9], 21, -343485551);"
							+ "J = safe_add(J, E);I = safe_add(I, D);H = safe_add(H, B);G = safe_add(G, A)}if (mode == 16) {return Array(I, H)} else {return Array(J, I, H, G)}}function md5_cmn(F, C, B, A, E, D) {"
							+ "return safe_add(bit_rol(safe_add(safe_add(C, F), safe_add(A, D)), E), B)}function md5_ff(C, B, G, F, A, E, D) {return md5_cmn((B & G) | ((~B) & F), C, B, A, E, D)}"
							+ "function md5_gg(C, B, G, F, A, E, D) {return md5_cmn((B & F) | (G & (~F)), C, B, A, E, D)}function md5_hh(C, B, G, F, A, E, D) {return md5_cmn(B ^ G ^ F, C, B, A, E, D)}"
							+ "function md5_ii(C, B, G, F, A, E, D) {return md5_cmn(G ^ (B | (~F)), C, B, A, E, D)}function core_hmac_md5(C, F) {var E = str2binl(C);if (E.length > 16) {E = core_md5(E, C.length * chrsz)}"
							+ "var A = Array(16), D = Array(16);for (var B = 0; B < 16; B++) {A[B] = E[B] ^ 909522486;D[B] = E[B] ^ 1549556828}var G = core_md5(A.concat(str2binl(F)), 512 + F.length * chrsz);"
							+ "return core_md5(D.concat(G), 512 + 128)}function safe_add(A, D) {var C = (A & 65535) + (D & 65535);var B = (A >> 16) + (D >> 16) + (C >> 16);return (B << 16) | (C & 65535)}"
							+ "function bit_rol(A, B) {return (A << B) | (A >>> (32 - B))}function str2binl(D) {var C = Array();var A = (1 << chrsz) - 1;for (var B = 0; B < D.length * chrsz; B += chrsz) {"
							+ "C[B >> 5] |= (D.charCodeAt(B / chrsz) & A) << (B % 32)}return C}function binl2str(C) {var D = \"\";var A = (1 << chrsz) - 1;for (var B = 0; B < C.length * 32; B += chrsz) {"
							+ "D += String.fromCharCode((C[B >> 5] >>> (B % 32)) & A)}return D}function binl2hex(C) {var B = hexcase ? \"0123456789ABCDEF\" : \"0123456789abcdef\";var D = \"\";"
							+ "for (var A = 0; A < C.length * 4; A++) {D += B.charAt((C[A >> 2] >> ((A % 4) * 8 + 4)) & 15)+ B.charAt((C[A >> 2] >> ((A % 4) * 8)) & 15)}return D}"
							+ "function binl2b64(D) {var C = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";var F = \"\";for (var B = 0; B < D.length * 4; B += 3) {var E = (((D[B >> 2] >> 8 * (B % 4)) & 255) << 16)"
							+ "| (((D[B + 1 >> 2] >> 8 * ((B + 1) % 4)) & 255) << 8)| ((D[B + 2 >> 2] >> 8 * ((B + 2) % 4)) & 255);for (var A = 0; A < 4; A++) {if (B * 8 + A * 6 > D.length * 32) {F += b64pad"
							+ "} else {F += C.charAt((E >> 6 * (3 - A)) & 63)}}}return F}function hexchar2bin(str) {var arr = [];for (var i = 0; i < str.length; i = i + 2) {arr.push(\"\\\\x\" + str.substr(i, 2))"
							+ "}arr = arr.join(\"\");eval(\"var temp = '\" + arr + \"'\");return temp}function QXWEncodePwd(pt_uin, p, vc) {var I = hexchar2bin(md5(p));var H = md5(I + TTescapechar2bin(pt_uin));"
							+ "var G = md5(H + vc.toUpperCase());return G}function TTescapechar2bin(str) {eval(\"var temp = '\" + str + \"'\");return temp}");
			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				// 调用preprocess方法，并传入两个参数密码和验证码

				pass = invoke.invokeFunction("QXWEncodePwd",
						checkNum[2].trim(), p, checkNum[1].trim()).toString();
				System.out.println("c = " + pass);
			}

			/************************* 登录 ****************************/
			get = new HttpGet(
					"http://ptlogin2.qq.com/login?ptlang=2052&u="
							+ uin
							+ "&p="
							+ pass
							+ "&verifycode="
							+ checkNum[1]
							+ "&aid=46000101&u1=http%3A%2F%2Ft.qq.com&ptredirect=1&h=1&from_ui=1&dumy=&fp=loginerroralert&action=4-12-14683&g=1&t=1&dummy=");
			get.setHeader("Connection", "keep-alive");
			get.setHeader("Host", "ptlogin2.qq.com");
			get.setHeader("Referer", "http://t.qq.com/?from=11");
			get
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0");
			response = client.execute(get);
			entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);
			if (entity.indexOf("登录成功") > -1) {
				get = new HttpGet("http://t.qq.com");
				response = client.execute(get);
				entity = EntityUtils.toString(response.getEntity());
				Document doc = Jsoup.parse(entity.replace("&nbsp;", ""));
				Element es = doc.getElementById("topNav1");
				String displayName = "";
				if (es != null) {
					Elements e = es.getElementsByTag("u");
					if (e != null && e.size() > 0)
						displayName = e.get(0).text().replace(" ", "").trim();
				}

				u = new WeiBoUser();
				u.setUserName(uin);
				u.setUserPass(p);
				u.setDisplayName(displayName);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
		return u;
	}

	public static boolean AddN(String uin, String p, String content) {
		boolean addOk = false;
		DefaultHttpClient client = new DefaultHttpClient();

		try {

			/********************* 获取验证码 ***********************/
			HttpGet get = new HttpGet("http://check.ptlogin2.qq.com/check?uin="
					+ uin + "&appid=46000101&ptlang=2052&r=" + Math.random());
			HttpResponse response = client.execute(get);
			String entity = EntityUtils.toString(response.getEntity());
			String[] checkNum = entity.substring(entity.indexOf("(") + 1,
					entity.lastIndexOf(")")).replace("'", "").split(",");
			System.out.println(checkNum[0]);
			System.out.println(checkNum[1]);
			System.out.println(checkNum[2].trim());
			System.out.println(checkNum[2].trim().replace("\\x", ""));
			String pass = "";

			/******************** *加密密码 ***************************/
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");

			engine
					.eval("var hexcase = 1;var b64pad = \"\";var chrsz = 8;var mode = 32;function md5(A) {return hex_md5(A)}function hex_md5(A) {return binl2hex(core_md5(str2binl(A), A.length * chrsz))}"
							+ "function str_md5(A) {return binl2str(core_md5(str2binl(A), A.length * chrsz))}function hex_hmac_md5(A, B) {return binl2hex(core_hmac_md5(A, B))}function b64_hmac_md5(A, B) {return binl2b64(core_hmac_md5(A, B))}"
							+ "function str_hmac_md5(A, B) {return binl2str(core_hmac_md5(A, B))}"
							+ "function core_md5(K, F) {K[F >> 5] |= 128 << ((F) % 32);K[(((F + 64) >>> 9) << 4) + 14] = F;var J = 1732584193;var I = -271733879;var H = -1732584194;var G = 271733878;"
							+ "for (var C = 0; C < K.length; C += 16) {var E = J;var D = I;var B = H;var A = G;J = md5_ff(J, I, H, G, K[C + 0], 7, -680876936);G = md5_ff(G, J, I, H, K[C + 1], 12, -389564586);"
							+ "H = md5_ff(H, G, J, I, K[C + 2], 17, 606105819);I = md5_ff(I, H, G, J, K[C + 3], 22, -1044525330);J = md5_ff(J, I, H, G, K[C + 4], 7, -176418897);G = md5_ff(G, J, I, H, K[C + 5], 12, 1200080426);H = md5_ff(H, G, J, I, K[C + 6], 17, -1473231341);"
							+ "I = md5_ff(I, H, G, J, K[C + 7], 22, -45705983);J = md5_ff(J, I, H, G, K[C + 8], 7, 1770035416);G = md5_ff(G, J, I, H, K[C + 9], 12, -1958414417);H = md5_ff(H, G, J, I, K[C + 10], 17, -42063);"
							+ "I = md5_ff(I, H, G, J, K[C + 11], 22, -1990404162);J = md5_ff(J, I, H, G, K[C + 12], 7, 1804603682);G = md5_ff(G, J, I, H, K[C + 13], 12, -40341101);H = md5_ff(H, G, J, I, K[C + 14], 17, -1502002290);I = md5_ff(I, H, G, J, K[C + 15], 22, 1236535329);"
							+ "J = md5_gg(J, I, H, G, K[C + 1], 5, -165796510);G = md5_gg(G, J, I, H, K[C + 6], 9, -1069501632);H = md5_gg(H, G, J, I, K[C + 11], 14, 643717713);I = md5_gg(I, H, G, J, K[C + 0], 20, -373897302);"
							+ "J = md5_gg(J, I, H, G, K[C + 5], 5, -701558691);G = md5_gg(G, J, I, H, K[C + 10], 9, 38016083);H = md5_gg(H, G, J, I, K[C + 15], 14, -660478335);I = md5_gg(I, H, G, J, K[C + 4], 20, -405537848);"
							+ "J = md5_gg(J, I, H, G, K[C + 9], 5, 568446438);G = md5_gg(G, J, I, H, K[C + 14], 9, -1019803690);H = md5_gg(H, G, J, I, K[C + 3], 14, -187363961);I = md5_gg(I, H, G, J, K[C + 8], 20, 1163531501);"
							+ "J = md5_gg(J, I, H, G, K[C + 13], 5, -1444681467);G = md5_gg(G, J, I, H, K[C + 2], 9, -51403784);H = md5_gg(H, G, J, I, K[C + 7], 14, 1735328473);I = md5_gg(I, H, G, J, K[C + 12], 20, -1926607734);"
							+ "J = md5_hh(J, I, H, G, K[C + 5], 4, -378558);G = md5_hh(G, J, I, H, K[C + 8], 11, -2022574463);H = md5_hh(H, G, J, I, K[C + 11], 16, 1839030562);I = md5_hh(I, H, G, J, K[C + 14], 23, -35309556);"
							+ "J = md5_hh(J, I, H, G, K[C + 1], 4, -1530992060);G = md5_hh(G, J, I, H, K[C + 4], 11, 1272893353);H = md5_hh(H, G, J, I, K[C + 7], 16, -155497632);I = md5_hh(I, H, G, J, K[C + 10], 23, -1094730640);"
							+ "J = md5_hh(J, I, H, G, K[C + 13], 4, 681279174);G = md5_hh(G, J, I, H, K[C + 0], 11, -358537222);H = md5_hh(H, G, J, I, K[C + 3], 16, -722521979);I = md5_hh(I, H, G, J, K[C + 6], 23, 76029189);J = md5_hh(J, I, H, G, K[C + 9], 4, -640364487);G = md5_hh(G, J, I, H, K[C + 12], 11, -421815835);H = md5_hh(H, G, J, I, K[C + 15], 16, 530742520);"
							+ "I = md5_hh(I, H, G, J, K[C + 2], 23, -995338651);J = md5_ii(J, I, H, G, K[C + 0], 6, -198630844);G = md5_ii(G, J, I, H, K[C + 7], 10, 1126891415);H = md5_ii(H, G, J, I, K[C + 14], 15, -1416354905);"
							+ "I = md5_ii(I, H, G, J, K[C + 5], 21, -57434055);J = md5_ii(J, I, H, G, K[C + 12], 6, 1700485571);G = md5_ii(G, J, I, H, K[C + 3], 10, -1894986606);H = md5_ii(H, G, J, I, K[C + 10], 15, -1051523);I = md5_ii(I, H, G, J, K[C + 1], 21, -2054922799);"
							+ "J = md5_ii(J, I, H, G, K[C + 8], 6, 1873313359);G = md5_ii(G, J, I, H, K[C + 15], 10, -30611744);H = md5_ii(H, G, J, I, K[C + 6], 15, -1560198380);I = md5_ii(I, H, G, J, K[C + 13], 21, 1309151649);"
							+ "J = md5_ii(J, I, H, G, K[C + 4], 6, -145523070);G = md5_ii(G, J, I, H, K[C + 11], 10, -1120210379);H = md5_ii(H, G, J, I, K[C + 2], 15, 718787259);I = md5_ii(I, H, G, J, K[C + 9], 21, -343485551);"
							+ "J = safe_add(J, E);I = safe_add(I, D);H = safe_add(H, B);G = safe_add(G, A)}if (mode == 16) {return Array(I, H)} else {return Array(J, I, H, G)}}function md5_cmn(F, C, B, A, E, D) {"
							+ "return safe_add(bit_rol(safe_add(safe_add(C, F), safe_add(A, D)), E), B)}function md5_ff(C, B, G, F, A, E, D) {return md5_cmn((B & G) | ((~B) & F), C, B, A, E, D)}"
							+ "function md5_gg(C, B, G, F, A, E, D) {return md5_cmn((B & F) | (G & (~F)), C, B, A, E, D)}function md5_hh(C, B, G, F, A, E, D) {return md5_cmn(B ^ G ^ F, C, B, A, E, D)}"
							+ "function md5_ii(C, B, G, F, A, E, D) {return md5_cmn(G ^ (B | (~F)), C, B, A, E, D)}function core_hmac_md5(C, F) {var E = str2binl(C);if (E.length > 16) {E = core_md5(E, C.length * chrsz)}"
							+ "var A = Array(16), D = Array(16);for (var B = 0; B < 16; B++) {A[B] = E[B] ^ 909522486;D[B] = E[B] ^ 1549556828}var G = core_md5(A.concat(str2binl(F)), 512 + F.length * chrsz);"
							+ "return core_md5(D.concat(G), 512 + 128)}function safe_add(A, D) {var C = (A & 65535) + (D & 65535);var B = (A >> 16) + (D >> 16) + (C >> 16);return (B << 16) | (C & 65535)}"
							+ "function bit_rol(A, B) {return (A << B) | (A >>> (32 - B))}function str2binl(D) {var C = Array();var A = (1 << chrsz) - 1;for (var B = 0; B < D.length * chrsz; B += chrsz) {"
							+ "C[B >> 5] |= (D.charCodeAt(B / chrsz) & A) << (B % 32)}return C}function binl2str(C) {var D = \"\";var A = (1 << chrsz) - 1;for (var B = 0; B < C.length * 32; B += chrsz) {"
							+ "D += String.fromCharCode((C[B >> 5] >>> (B % 32)) & A)}return D}function binl2hex(C) {var B = hexcase ? \"0123456789ABCDEF\" : \"0123456789abcdef\";var D = \"\";"
							+ "for (var A = 0; A < C.length * 4; A++) {D += B.charAt((C[A >> 2] >> ((A % 4) * 8 + 4)) & 15)+ B.charAt((C[A >> 2] >> ((A % 4) * 8)) & 15)}return D}"
							+ "function binl2b64(D) {var C = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";var F = \"\";for (var B = 0; B < D.length * 4; B += 3) {var E = (((D[B >> 2] >> 8 * (B % 4)) & 255) << 16)"
							+ "| (((D[B + 1 >> 2] >> 8 * ((B + 1) % 4)) & 255) << 8)| ((D[B + 2 >> 2] >> 8 * ((B + 2) % 4)) & 255);for (var A = 0; A < 4; A++) {if (B * 8 + A * 6 > D.length * 32) {F += b64pad"
							+ "} else {F += C.charAt((E >> 6 * (3 - A)) & 63)}}}return F}function hexchar2bin(str) {var arr = [];for (var i = 0; i < str.length; i = i + 2) {arr.push(\"\\\\x\" + str.substr(i, 2))"
							+ "}arr = arr.join(\"\");eval(\"var temp = '\" + arr + \"'\");return temp}function QXWEncodePwd(pt_uin, p, vc) {var I = hexchar2bin(md5(p));var H = md5(I + TTescapechar2bin(pt_uin));"
							+ "var G = md5(H + vc.toUpperCase());return G}function TTescapechar2bin(str) {eval(\"var temp = '\" + str + \"'\");return temp}");
			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				// 调用preprocess方法，并传入两个参数密码和验证码

				pass = invoke.invokeFunction("QXWEncodePwd",
						checkNum[2].trim(), p, checkNum[1].trim()).toString();
				System.out.println("c = " + pass);
			}

			/************************* 登录 ****************************/
			get = new HttpGet(
					"http://ptlogin2.qq.com/login?ptlang=2052&u="
							+ uin
							+ "&p="
							+ pass
							+ "&verifycode="
							+ checkNum[1]
							+ "&aid=46000101&u1=http%3A%2F%2Ft.qq.com&ptredirect=1&h=1&from_ui=1&dumy=&fp=loginerroralert&action=4-12-14683&g=1&t=1&dummy=");
			get.setHeader("Connection", "keep-alive");
			get.setHeader("Host", "ptlogin2.qq.com");
			get.setHeader("Referer", "http://t.qq.com/?from=11");
			get
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0");
			response = client.execute(get);
			entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);

			if (entity.indexOf("登录成功") > -1) {
				/************************* 分享 *************************/
				HttpPost post = new HttpPost(
						"http://api.t.qq.com/old/publish.php");
				post.addHeader("Referer", "http://api.t.qq.com/proxy.html");
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("apiType", "8"));
				nvps.add(new BasicNameValuePair("attips", ""));
				nvps.add(new BasicNameValuePair("content", content));
				nvps.add(new BasicNameValuePair("countType", ""));
				nvps.add(new BasicNameValuePair("endTime", ""));
				nvps.add(new BasicNameValuePair("pic", ""));
				nvps.add(new BasicNameValuePair("startTime", ""));
				nvps.add(new BasicNameValuePair("syncQQSign", "0"));
				nvps.add(new BasicNameValuePair("syncQzone", "0"));
				nvps.add(new BasicNameValuePair("viewModel", ""));
				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				response = client.execute(post);
				entity = EntityUtils.toString(response.getEntity());
				if (entity.replace("\"", "").indexOf("result:0") > -1) {
					addOk = true;
				}

			} else {
				addOk = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
		return addOk;
	}

	public static boolean Share(String uin, String p, String content,
			String sourcepic, String wizardurl) {
		boolean shareOk = false;
		DefaultHttpClient client = new DefaultHttpClient();

		try {

			/********************* 获取验证码 ***********************/
			HttpGet get = new HttpGet("http://check.ptlogin2.qq.com/check?uin="
					+ uin + "&appid=46000101&ptlang=2052&r=0.06225140227238779");

			HttpResponse response = client.execute(get);
			String entity = EntityUtils.toString(response.getEntity());
			String[] checkNum = entity.substring(entity.indexOf("(") + 1,
					entity.lastIndexOf(")")).replace("'", "").split(",");

			System.out.println(checkNum[1]);

			String pass = p;

			/******************** *加密密码 ***************************/
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");

			engine
					.eval("var chrsz=8;var mode=32;var hexcase=1;function preprocess(pwd,check){var B='';B=check;var value=md5(md5_3(pwd)+B);return value;}function md5_3(B){var A=new Array;"
							+ "A=core_md5(str2binl(B),B.length*chrsz);A=core_md5(A,16*chrsz);A=core_md5(A,16*chrsz);return binl2hex(A);}function md5(A){return hex_md5(A);}"
							+ "function hex_md5(A){return binl2hex(core_md5(str2binl(A),A.length*chrsz));}function str_hmac_md5(A,B){return binl2str(core_hmac_md5(A,B));}"
							+ "function core_md5(K,F){K[F>>5]|=128<<((F)%32);K[(((F+64)>>>9)<<4)+14]=F;var J=1732584193;var I=-271733879;var H=-1732584194;var G=271733878;"
							+ "for(var C=0;C<K.length;C+=16){var E=J;var D=I;var B=H;var A=G;J=md5_ff(J,I,H,G,K[C+0],7,-680876936);G=md5_ff(G,J,I,H,K[C+1],12,-389564586);H=md5_ff(H,G,J,I,K[C+2],17,606105819);"
							+ "I=md5_ff(I,H,G,J,K[C+3],22,-1044525330);J=md5_ff(J,I,H,G,K[C+4],7,-176418897);G=md5_ff(G,J,I,H,K[C+5],12,1200080426);H=md5_ff(H,G,J,I,K[C+6],17,-1473231341);"
							+ "I=md5_ff(I,H,G,J,K[C+7],22,-45705983);J=md5_ff(J,I,H,G,K[C+8],7,1770035416);G=md5_ff(G,J,I,H,K[C+9],12,-1958414417);H=md5_ff(H,G,J,I,K[C+10],17,-42063);"
							+ "I=md5_ff(I,H,G,J,K[C+11],22,-1990404162);J=md5_ff(J,I,H,G,K[C+12],7,1804603682);G=md5_ff(G,J,I,H,K[C+13],12,-40341101);H=md5_ff(H,G,J,I,K[C+14],17,-1502002290);"
							+ "I=md5_ff(I,H,G,J,K[C+15],22,1236535329);J=md5_gg(J,I,H,G,K[C+1],5,-165796510);G=md5_gg(G,J,I,H,K[C+6],9,-1069501632);H=md5_gg(H,G,J,I,K[C+11],14,643717713);"
							+ "I=md5_gg(I,H,G,J,K[C+0],20,-373897302);J=md5_gg(J,I,H,G,K[C+5],5,-701558691);G=md5_gg(G,J,I,H,K[C+10],9,38016083);H=md5_gg(H,G,J,I,K[C+15],14,-660478335);"
							+ "I=md5_gg(I,H,G,J,K[C+4],20,-405537848);J=md5_gg(J,I,H,G,K[C+9],5,568446438);G=md5_gg(G,J,I,H,K[C+14],9,-1019803690);H=md5_gg(H,G,J,I,K[C+3],14,-187363961);"
							+ "I=md5_gg(I,H,G,J,K[C+8],20,1163531501);J=md5_gg(J,I,H,G,K[C+13],5,-1444681467);G=md5_gg(G,J,I,H,K[C+2],9,-51403784);H=md5_gg(H,G,J,I,K[C+7],14,1735328473);"
							+ "I=md5_gg(I,H,G,J,K[C+12],20,-1926607734);J=md5_hh(J,I,H,G,K[C+5],4,-378558);G=md5_hh(G,J,I,H,K[C+8],11,-2022574463);H=md5_hh(H,G,J,I,K[C+11],16,1839030562);"
							+ "I=md5_hh(I,H,G,J,K[C+14],23,-35309556);J=md5_hh(J,I,H,G,K[C+1],4,-1530992060);G=md5_hh(G,J,I,H,K[C+4],11,1272893353);H=md5_hh(H,G,J,I,K[C+7],16,-155497632);"
							+ "I=md5_hh(I,H,G,J,K[C+10],23,-1094730640);J=md5_hh(J,I,H,G,K[C+13],4,681279174);G=md5_hh(G,J,I,H,K[C+0],11,-358537222);H=md5_hh(H,G,J,I,K[C+3],16,-722521979);"
							+ "I=md5_hh(I,H,G,J,K[C+6],23,76029189);J=md5_hh(J,I,H,G,K[C+9],4,-640364487);G=md5_hh(G,J,I,H,K[C+12],11,-421815835);"
							+ "H=md5_hh(H,G,J,I,K[C+15],16,530742520);I=md5_hh(I,H,G,J,K[C+2],23,-995338651);J=md5_ii(J,I,H,G,K[C+0],6,-198630844);"
							+ "G=md5_ii(G,J,I,H,K[C+7],10,1126891415);H=md5_ii(H,G,J,I,K[C+14],15,-1416354905);I=md5_ii(I,H,G,J,K[C+5],21,-57434055);J=md5_ii(J,I,H,G,K[C+12],6,1700485571);"
							+ "G=md5_ii(G,J,I,H,K[C+3],10,-1894986606);H=md5_ii(H,G,J,I,K[C+10],15,-1051523);I=md5_ii(I,H,G,J,K[C+1],21,-2054922799);J=md5_ii(J,I,H,G,K[C+8],6,1873313359);"
							+ "G=md5_ii(G,J,I,H,K[C+15],10,-30611744);H=md5_ii(H,G,J,I,K[C+6],15,-1560198380);I=md5_ii(I,H,G,J,K[C+13],21,1309151649);J=md5_ii(J,I,H,G,K[C+4],6,-145523070);"
							+ "G=md5_ii(G,J,I,H,K[C+11],10,-1120210379);H=md5_ii(H,G,J,I,K[C+2],15,718787259);I=md5_ii(I,H,G,J,K[C+9],21,-343485551);J=safe_add(J,E);"
							+ "I=safe_add(I,D);H=safe_add(H,B);G=safe_add(G,A);}if(mode==16){return Array(I,H);}else{return Array(J,I,H,G);}}function md5_cmn(F,C,B,A,E,D){"
							+ "return safe_add(bit_rol(safe_add(safe_add(C,F),safe_add(A,D)),E),B);}function md5_ff(C,B,G,F,A,E,D){return md5_cmn((B&G)|((~B)&F),C,B,A,E,D);}"
							+ "function md5_gg(C,B,G,F,A,E,D){return md5_cmn((B&F)|(G&(~F)),C,B,A,E,D);}function md5_hh(C,B,G,F,A,E,D){return md5_cmn(B^G^F,C,B,A,E,D);}"
							+ "function md5_ii(C,B,G,F,A,E,D){return md5_cmn(G^(B|(~F)),C,B,A,E,D);}function core_hmac_md5(C,F){var E=str2binl(C);if(E.length>16){E=core_md5(E,C.length*chrsz);"
							+ "}var A=Array(16),D=Array(16);for(var B=0;B<16;B++){A[B]=E[B]^909522486;D[B]=E[B]^1549556828;}var G=core_md5(A.concat(str2binl(F)),512+F.length*chrsz);"
							+ "return core_md5(D.concat(G),512+128);}function safe_add(A,D){var C=(A&65535)+(D&65535);var B=(A>>16)+(D>>16)+(C>>16);return(B<<16)|(C&65535);}"
							+ "function bit_rol(A,B){return(A<<B)|(A>>>(32-B));}function str2binl(D){var C=Array();var A=(1<<chrsz)-1;for(var B=0;B<D.length*chrsz;B+=chrsz){"
							+ "C[B>>5]|=(D.charCodeAt(B/chrsz)&A)<<(B%32);}return C;}function binl2str(C){var D='';var A=(1<<chrsz)-1;for(var B=0;B<C.length*32;B+=chrsz){"
							+ "D+=String.fromCharCode((C[B>>5]>>>(B%32))&A);}return D;}function binl2hex(C){var B=hexcase?'0123456789ABCDEF':'0123456789abcdef';var D='';for(var A=0;A<C.length*4;A++){"
							+ "D+=B.charAt((C[A>>2]>>((A%4)*8+4))&15)+B.charAt((C[A>>2]>>((A%4)*8))&15);}return D;}");
			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				// 调用preprocess方法，并传入两个参数密码和验证码
				pass = (String) invoke.invokeFunction("preprocess", pass,
						checkNum[1]);
				System.out.println("c = " + pass);
			}
			// reader.close();

			/************************* 登录 ****************************/
			get = new HttpGet(
					"http://ptlogin2.qq.com/login?ptlang=2052&u="
							+ uin
							+ "&p="
							+ p
							+ "&verifycode="
							+ checkNum[1]
							+ "&low_login_enable=1&low_login_hour=720&aid=46000101&u1=http%3A%2F%2Ft.qq.com&ptredirect=1&h=1&from_ui=1&dumy=&fp=loginerroralert&action=5-28-42101&g=1&t=1&dummy=");

			response = client.execute(get);
			entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);
			if (entity.indexOf("登录成功") > -1) {
				/************************* 分享 *************************/
				HttpPost post = new HttpPost(
						"http://radio.t.qq.com/publish.php");
				post
						.addHeader(
								"Referer",
								"http://radio.t.qq.com/share.php?title=%E4%B9%8C%E9%B2%81%E6%9C%A8%E9%BD%90%E5%B8%82%E5%A4%A7%E9%A3%8E%E8%87%B473%E4%BA%BA%E5%8F%97%E4%BC%A4%203%E4%BA%BA%E6%AD%BB%E4%BA%A1(%E5%9B%BE)&url=http://news.qq.com/a/20120331/000027.htm&pref=qqcom.dp.tmblog&source=qqnews");
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("content", content));
				nvps.add(new BasicNameValuePair("countType", ""));
				nvps.add(new BasicNameValuePair("pic", ""));
				nvps.add(new BasicNameValuePair("source", "1000001"));
				nvps.add(new BasicNameValuePair("sourcepic", sourcepic));
				nvps.add(new BasicNameValuePair("viewModel", "0"));
				nvps
						.add(new BasicNameValuePair("wizardpref",
								"qqcom.dp.tmblog"));
				nvps.add(new BasicNameValuePair("wizardurl", wizardurl));
				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				response = client.execute(post);
				entity = EntityUtils.toString(response.getEntity());
				if (entity.replace("\"", "").indexOf("result:0") > -1) {
					shareOk = true;
				}

			} else {
				shareOk = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
		return shareOk;
	}

	public static boolean Register(String mobile) {

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response;
			HttpPost post = new HttpPost(
					"http://ti.3g.qq.com/reg/s?sid=AeEWzbJsjdxX6P-KW2gUn29c&r=82864&aid=nsms");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("ac", "24"));
			nvps.add(new BasicNameValuePair("bid", "sms"));
			nvps.add(new BasicNameValuePair("mobile", mobile));
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = client.execute(post);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}

	}

	public static String getpath() {
		URL u = TencentClient.class.getClassLoader().getResource("md.js");

		String url = u.getPath().substring(u.getPath().indexOf("C:")).replace(
				"%20", " ");

		return url;
	}

	public static boolean Upload(String uin, String p, String content,
			byte[] pic) {
		boolean addOk = false;
		DefaultHttpClient client = new DefaultHttpClient();

		try {

			/********************* 获取验证码 ***********************/
			HttpGet get = new HttpGet("http://check.ptlogin2.qq.com/check?uin="
					+ uin + "&appid=46000101&ptlang=2052&r=" + Math.random());
			HttpResponse response = client.execute(get);
			String entity = EntityUtils.toString(response.getEntity());
			String[] checkNum = entity.substring(entity.indexOf("(") + 1,
					entity.lastIndexOf(")")).replace("'", "").split(",");
			System.out.println(checkNum[0]);
			System.out.println(checkNum[1]);
			System.out.println(checkNum[2].trim());
			System.out.println(checkNum[2].trim().replace("\\x", ""));
			String pass = "";

			/******************** *加密密码 ***************************/
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("javascript");

			engine
					.eval("var hexcase = 1;var b64pad = \"\";var chrsz = 8;var mode = 32;function md5(A) {return hex_md5(A)}function hex_md5(A) {return binl2hex(core_md5(str2binl(A), A.length * chrsz))}"
							+ "function str_md5(A) {return binl2str(core_md5(str2binl(A), A.length * chrsz))}function hex_hmac_md5(A, B) {return binl2hex(core_hmac_md5(A, B))}function b64_hmac_md5(A, B) {return binl2b64(core_hmac_md5(A, B))}"
							+ "function str_hmac_md5(A, B) {return binl2str(core_hmac_md5(A, B))}"
							+ "function core_md5(K, F) {K[F >> 5] |= 128 << ((F) % 32);K[(((F + 64) >>> 9) << 4) + 14] = F;var J = 1732584193;var I = -271733879;var H = -1732584194;var G = 271733878;"
							+ "for (var C = 0; C < K.length; C += 16) {var E = J;var D = I;var B = H;var A = G;J = md5_ff(J, I, H, G, K[C + 0], 7, -680876936);G = md5_ff(G, J, I, H, K[C + 1], 12, -389564586);"
							+ "H = md5_ff(H, G, J, I, K[C + 2], 17, 606105819);I = md5_ff(I, H, G, J, K[C + 3], 22, -1044525330);J = md5_ff(J, I, H, G, K[C + 4], 7, -176418897);G = md5_ff(G, J, I, H, K[C + 5], 12, 1200080426);H = md5_ff(H, G, J, I, K[C + 6], 17, -1473231341);"
							+ "I = md5_ff(I, H, G, J, K[C + 7], 22, -45705983);J = md5_ff(J, I, H, G, K[C + 8], 7, 1770035416);G = md5_ff(G, J, I, H, K[C + 9], 12, -1958414417);H = md5_ff(H, G, J, I, K[C + 10], 17, -42063);"
							+ "I = md5_ff(I, H, G, J, K[C + 11], 22, -1990404162);J = md5_ff(J, I, H, G, K[C + 12], 7, 1804603682);G = md5_ff(G, J, I, H, K[C + 13], 12, -40341101);H = md5_ff(H, G, J, I, K[C + 14], 17, -1502002290);I = md5_ff(I, H, G, J, K[C + 15], 22, 1236535329);"
							+ "J = md5_gg(J, I, H, G, K[C + 1], 5, -165796510);G = md5_gg(G, J, I, H, K[C + 6], 9, -1069501632);H = md5_gg(H, G, J, I, K[C + 11], 14, 643717713);I = md5_gg(I, H, G, J, K[C + 0], 20, -373897302);"
							+ "J = md5_gg(J, I, H, G, K[C + 5], 5, -701558691);G = md5_gg(G, J, I, H, K[C + 10], 9, 38016083);H = md5_gg(H, G, J, I, K[C + 15], 14, -660478335);I = md5_gg(I, H, G, J, K[C + 4], 20, -405537848);"
							+ "J = md5_gg(J, I, H, G, K[C + 9], 5, 568446438);G = md5_gg(G, J, I, H, K[C + 14], 9, -1019803690);H = md5_gg(H, G, J, I, K[C + 3], 14, -187363961);I = md5_gg(I, H, G, J, K[C + 8], 20, 1163531501);"
							+ "J = md5_gg(J, I, H, G, K[C + 13], 5, -1444681467);G = md5_gg(G, J, I, H, K[C + 2], 9, -51403784);H = md5_gg(H, G, J, I, K[C + 7], 14, 1735328473);I = md5_gg(I, H, G, J, K[C + 12], 20, -1926607734);"
							+ "J = md5_hh(J, I, H, G, K[C + 5], 4, -378558);G = md5_hh(G, J, I, H, K[C + 8], 11, -2022574463);H = md5_hh(H, G, J, I, K[C + 11], 16, 1839030562);I = md5_hh(I, H, G, J, K[C + 14], 23, -35309556);"
							+ "J = md5_hh(J, I, H, G, K[C + 1], 4, -1530992060);G = md5_hh(G, J, I, H, K[C + 4], 11, 1272893353);H = md5_hh(H, G, J, I, K[C + 7], 16, -155497632);I = md5_hh(I, H, G, J, K[C + 10], 23, -1094730640);"
							+ "J = md5_hh(J, I, H, G, K[C + 13], 4, 681279174);G = md5_hh(G, J, I, H, K[C + 0], 11, -358537222);H = md5_hh(H, G, J, I, K[C + 3], 16, -722521979);I = md5_hh(I, H, G, J, K[C + 6], 23, 76029189);J = md5_hh(J, I, H, G, K[C + 9], 4, -640364487);G = md5_hh(G, J, I, H, K[C + 12], 11, -421815835);H = md5_hh(H, G, J, I, K[C + 15], 16, 530742520);"
							+ "I = md5_hh(I, H, G, J, K[C + 2], 23, -995338651);J = md5_ii(J, I, H, G, K[C + 0], 6, -198630844);G = md5_ii(G, J, I, H, K[C + 7], 10, 1126891415);H = md5_ii(H, G, J, I, K[C + 14], 15, -1416354905);"
							+ "I = md5_ii(I, H, G, J, K[C + 5], 21, -57434055);J = md5_ii(J, I, H, G, K[C + 12], 6, 1700485571);G = md5_ii(G, J, I, H, K[C + 3], 10, -1894986606);H = md5_ii(H, G, J, I, K[C + 10], 15, -1051523);I = md5_ii(I, H, G, J, K[C + 1], 21, -2054922799);"
							+ "J = md5_ii(J, I, H, G, K[C + 8], 6, 1873313359);G = md5_ii(G, J, I, H, K[C + 15], 10, -30611744);H = md5_ii(H, G, J, I, K[C + 6], 15, -1560198380);I = md5_ii(I, H, G, J, K[C + 13], 21, 1309151649);"
							+ "J = md5_ii(J, I, H, G, K[C + 4], 6, -145523070);G = md5_ii(G, J, I, H, K[C + 11], 10, -1120210379);H = md5_ii(H, G, J, I, K[C + 2], 15, 718787259);I = md5_ii(I, H, G, J, K[C + 9], 21, -343485551);"
							+ "J = safe_add(J, E);I = safe_add(I, D);H = safe_add(H, B);G = safe_add(G, A)}if (mode == 16) {return Array(I, H)} else {return Array(J, I, H, G)}}function md5_cmn(F, C, B, A, E, D) {"
							+ "return safe_add(bit_rol(safe_add(safe_add(C, F), safe_add(A, D)), E), B)}function md5_ff(C, B, G, F, A, E, D) {return md5_cmn((B & G) | ((~B) & F), C, B, A, E, D)}"
							+ "function md5_gg(C, B, G, F, A, E, D) {return md5_cmn((B & F) | (G & (~F)), C, B, A, E, D)}function md5_hh(C, B, G, F, A, E, D) {return md5_cmn(B ^ G ^ F, C, B, A, E, D)}"
							+ "function md5_ii(C, B, G, F, A, E, D) {return md5_cmn(G ^ (B | (~F)), C, B, A, E, D)}function core_hmac_md5(C, F) {var E = str2binl(C);if (E.length > 16) {E = core_md5(E, C.length * chrsz)}"
							+ "var A = Array(16), D = Array(16);for (var B = 0; B < 16; B++) {A[B] = E[B] ^ 909522486;D[B] = E[B] ^ 1549556828}var G = core_md5(A.concat(str2binl(F)), 512 + F.length * chrsz);"
							+ "return core_md5(D.concat(G), 512 + 128)}function safe_add(A, D) {var C = (A & 65535) + (D & 65535);var B = (A >> 16) + (D >> 16) + (C >> 16);return (B << 16) | (C & 65535)}"
							+ "function bit_rol(A, B) {return (A << B) | (A >>> (32 - B))}function str2binl(D) {var C = Array();var A = (1 << chrsz) - 1;for (var B = 0; B < D.length * chrsz; B += chrsz) {"
							+ "C[B >> 5] |= (D.charCodeAt(B / chrsz) & A) << (B % 32)}return C}function binl2str(C) {var D = \"\";var A = (1 << chrsz) - 1;for (var B = 0; B < C.length * 32; B += chrsz) {"
							+ "D += String.fromCharCode((C[B >> 5] >>> (B % 32)) & A)}return D}function binl2hex(C) {var B = hexcase ? \"0123456789ABCDEF\" : \"0123456789abcdef\";var D = \"\";"
							+ "for (var A = 0; A < C.length * 4; A++) {D += B.charAt((C[A >> 2] >> ((A % 4) * 8 + 4)) & 15)+ B.charAt((C[A >> 2] >> ((A % 4) * 8)) & 15)}return D}"
							+ "function binl2b64(D) {var C = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";var F = \"\";for (var B = 0; B < D.length * 4; B += 3) {var E = (((D[B >> 2] >> 8 * (B % 4)) & 255) << 16)"
							+ "| (((D[B + 1 >> 2] >> 8 * ((B + 1) % 4)) & 255) << 8)| ((D[B + 2 >> 2] >> 8 * ((B + 2) % 4)) & 255);for (var A = 0; A < 4; A++) {if (B * 8 + A * 6 > D.length * 32) {F += b64pad"
							+ "} else {F += C.charAt((E >> 6 * (3 - A)) & 63)}}}return F}function hexchar2bin(str) {var arr = [];for (var i = 0; i < str.length; i = i + 2) {arr.push(\"\\\\x\" + str.substr(i, 2))"
							+ "}arr = arr.join(\"\");eval(\"var temp = '\" + arr + \"'\");return temp}function QXWEncodePwd(pt_uin, p, vc) {var I = hexchar2bin(md5(p));var H = md5(I + TTescapechar2bin(pt_uin));"
							+ "var G = md5(H + vc.toUpperCase());return G}function TTescapechar2bin(str) {eval(\"var temp = '\" + str + \"'\");return temp}");
			if (engine instanceof Invocable) {
				Invocable invoke = (Invocable) engine;
				// 调用preprocess方法，并传入两个参数密码和验证码

				pass = invoke.invokeFunction("QXWEncodePwd",
						checkNum[2].trim(), p, checkNum[1].trim()).toString();
				System.out.println("c = " + pass);
			}

			/************************* 登录 ****************************/
			get = new HttpGet(
					"http://ptlogin2.qq.com/login?ptlang=2052&u="
							+ uin
							+ "&p="
							+ pass
							+ "&verifycode="
							+ checkNum[1]
							+ "&aid=46000101&u1=http%3A%2F%2Ft.qq.com&ptredirect=1&h=1&from_ui=1&dumy=&fp=loginerroralert&action=4-12-14683&g=1&t=1&dummy=");
			get.setHeader("Connection", "keep-alive");
			get.setHeader("Host", "ptlogin2.qq.com");
			get.setHeader("Referer", "http://t.qq.com/?from=11");
			get
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0");
			response = client.execute(get);
			entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);

			if (entity.indexOf("登录成功") > -1) {
				/************************* 分享 *************************/
				HttpPost post = null;
				if (pic != null && pic.length > 0) {
					post = new HttpPost(
							"http://upload.t.qq.com/asyn/uploadpic.php");
					MultipartEntity reqEntity = new MultipartEntity();
					ByteArrayBody bin = new ByteArrayBody(pic, "show.jpg");

					reqEntity.addPart("pic", bin);

					post.setEntity(reqEntity);
					response = client.execute(post);
					entity = EntityUtils.toString(response.getEntity());
					System.out.println(entity);
					if (entity.indexOf("result:0") > -1) {

						JSONObject json = JSONObject.fromString(entity
								.substring(entity.indexOf("result") - 1, entity
										.indexOf("}}") + 2));

						String picURL = JSONObject.fromObject(json.get("info"))
								.get("image").toString();
						System.out.println(picURL);

						post = new HttpPost(
								"http://api.t.qq.com/old/publish.php");
						post.setHeader("Host", "api.t.qq.com");
						post.setHeader("Pragma", "no-cache");
						post.setHeader("Referer",
								"http://api.t.qq.com/proxy.html");
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						nvps.add(new BasicNameValuePair("apiType", "8"));
						nvps.add(new BasicNameValuePair("attips", ""));
						nvps.add(new BasicNameValuePair("content", content));
						nvps.add(new BasicNameValuePair("countType", ""));
						nvps.add(new BasicNameValuePair("endTime", ""));
						nvps.add(new BasicNameValuePair("pic", picURL));
						nvps.add(new BasicNameValuePair("startTime", "0"));
						nvps.add(new BasicNameValuePair("syncQQSign", "0"));
						nvps.add(new BasicNameValuePair("syncQzone", "0"));
						nvps.add(new BasicNameValuePair("viewModel", "0"));
						post.setEntity(new UrlEncodedFormEntity(nvps,
								HTTP.UTF_8));
						response = client.execute(post);
						entity = EntityUtils.toString(response.getEntity());
						System.out.println(entity);
						if (entity.replace("\"", "").indexOf("result:0") > -1)
							addOk = true;

					}
				} else {
					post = new HttpPost("http://api.t.qq.com/old/publish.php");
					post.setHeader("Host", "api.t.qq.com");
					post.setHeader("Pragma", "no-cache");
					post.setHeader("Referer", "http://api.t.qq.com/proxy.html");
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("apiType", "8"));
					nvps.add(new BasicNameValuePair("attips", ""));
					nvps.add(new BasicNameValuePair("content", content));
					nvps.add(new BasicNameValuePair("countType", ""));
					nvps.add(new BasicNameValuePair("endTime", ""));
					nvps.add(new BasicNameValuePair("pic", ""));
					nvps.add(new BasicNameValuePair("startTime", "0"));
					nvps.add(new BasicNameValuePair("syncQQSign", "0"));
					nvps.add(new BasicNameValuePair("syncQzone", "0"));
					nvps.add(new BasicNameValuePair("viewModel", "0"));
					post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					response = client.execute(post);
					entity = EntityUtils.toString(response.getEntity());
					System.out.println(entity);
					if (entity.replace("\"", "").indexOf("result:0") > -1)
						addOk = true;
				}

			} else {
				addOk = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			addOk = false;
		}
		return addOk;
	}

}
