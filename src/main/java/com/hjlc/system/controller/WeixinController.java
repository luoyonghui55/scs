package com.hjlc.system.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.marker.weixin.DefaultSession;
import org.marker.weixin.HandleMessageAdapter;
import org.marker.weixin.MySecurity;
import org.marker.weixin.msg.Data4Item;
import org.marker.weixin.msg.Msg4ImageText;
import org.marker.weixin.msg.Msg4Text;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hjlc.base.controller.BaseController;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.Tools;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * 类名称：WeixinController.java 类描述： 微信公共平台开发
 * 注：由于此类开发时间较久，官方或已有更新，最好重新导入相关jar包，重新开发
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/weixin")
public class WeixinController extends BaseController {

	/**
	 * 接口验证,总入口
	 * 
	 * @param out
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/index")
	public void index(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logBefore(logger, "微信接口");
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			String signature = pd.getString("signature"); // 微信加密签名
			String timestamp = pd.getString("timestamp"); // 时间戳
			String nonce = pd.getString("nonce"); // 随机数
			String echostr = pd.getString("echostr"); // 字符串

			if (null != signature && null != timestamp && null != nonce && null != echostr) {/* 接口验证 */
				System.out.println("进入身份验证");
				List<String> list = new ArrayList<String>(3) {
					private static final long serialVersionUID = 2621444383666420433L;

					public String toString() { // 重写toString方法，得到三个参数的拼接字符串
						return this.get(0) + this.get(1) + this.get(2);
					}
				};
				list.add(Tools.readTxtFile(Constants.WEIXIN)); // 读取Token(令牌)
				list.add(timestamp);
				list.add(nonce);
				Collections.sort(list); // 排序
				String tmpStr = new MySecurity().encode(list.toString(), MySecurity.SHA_1); // SHA-1加密

				if (signature.equals(tmpStr)) {
					out.write(echostr); // 请求验证成功，返回随机码
				} else {
					out.write("");
				}
				out.flush();
				out.close();
			} else {/* 消息处理 */
				System.out.println("进入消息处理");
				response.reset();
				sendMsg(request, response);
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	/**
	 * 处理微信服务器发过来的各种消息，包括：文本、图片、地理位置、音乐等等
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void sendMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {

		InputStream is = request.getInputStream();
		OutputStream os = response.getOutputStream();

		final DefaultSession session = DefaultSession.newInstance();
		session.addOnHandleMessageListener(new HandleMessageAdapter() {

			@Override
			public void onTextMsg(Msg4Text msg) {
				System.out.println("收到微信消息：" + msg.getContent());
				Process p;
				// 文字消息
				if ("1".equals(msg.getContent())) {
					Msg4Text rmsg = new Msg4Text();
					rmsg.setFromUserName(msg.getToUserName());
					rmsg.setToUserName(msg.getFromUserName());
					// rmsg.setFuncFlag("0");
					rmsg.setContent("你好!我是FHadmin");
					session.callback(rmsg);
					return;
				} else if ("2".equals(msg.getContent())) {
					// 回复图文消息
					Data4Item d1 = new Data4Item("测试1", "测试描述",
							"http://joythink.duapp.com/images/suzhouScenic/pingjianglu.jpg", "http://www.1b23.com");
					Data4Item d2 = new Data4Item("测试2", "测试描述",
							"http://joythink.duapp.com/images/suzhouScenic/pingjianglu.jpg", "http://www.1b23.com");

					Msg4ImageText mit = new Msg4ImageText();

					mit.setFromUserName(msg.getToUserName());
					mit.setToUserName(msg.getFromUserName());
					mit.setCreateTime(msg.getCreateTime());

					mit.addItem(d1);
					mit.addItem(d2);
					// mit.setFuncFlag("0");
					session.callback(mit);
				} else if ("打开QQ".equals(msg.getContent()) || "打开qq".equals(msg.getContent())) {
					Runtime runtime = Runtime.getRuntime();
					try {
						p = runtime.exec("D:/SOFT/QQ/QQ/Bin/qq");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if ("关闭QQ".equals(msg.getContent()) || "关闭qq".equals(msg.getContent())) {
					Runtime runtime = Runtime.getRuntime();
					try {
						// runtime.exec("taskkill /f /im calc.exe");
						runtime.exec("taskkill /f /im qq.exe");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if ("打开浏览器".equals(msg.getContent())) {
					Runtime runtime = Runtime.getRuntime();
					try {
						// p = runtime.exec("calc");
						p = runtime.exec("C:/Program Files/Internet Explorer/iexplore.exe");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if ("关闭浏览器".equals(msg.getContent())) {
					Runtime runtime = Runtime.getRuntime();
					try {
						// runtime.exec("taskkill /f /im calc.exe");
						runtime.exec("taskkill /f /im iexplore.exe");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if ("锁定计算机".equals(msg.getContent())) {
					Runtime runtime = Runtime.getRuntime();
					try {
						// runtime.exec("taskkill /f /im calc.exe");
						runtime.exec("rundll32.exe user32.dll,LockWorkStation");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		/* 必须调用这两个方法 如果不调用close方法，将会出现响应数据串到其它Servlet中。 */
		session.process(is, os); // 处理微信消息
		session.close(); // 关闭Session
	}

	// ================================================获取关注列表==============================================================
	public final static String gz_url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=";

	// 获取access_token
	@RequestMapping(value = "/getGz")
	public void getGz(PrintWriter out) {
		logBefore(logger, "获取关注列表");
		try {
			String access_token = readTxtFile("e:/access_token.txt");

			System.out.println(access_token + "============");

			String requestUrl = gz_url.replace("ACCESS_TOKEN", access_token);

			System.out.println(requestUrl + "============");

			JSONObject jsonObject = httpRequst(requestUrl, "GET", null);
			System.out.println(jsonObject);
			// System.out.println(jsonObject.getString("total")+"============");

			/*
			 * PrintWriter pw; try { pw = new PrintWriter( new FileWriter(
			 * "e:/gz.txt" ) ); pw.print(jsonObject.getString("total"));
			 * pw.close(); } catch (IOException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 * 
			 * out.write("success"); out.close();
			 */
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	// 读取文件
	public String readTxtFile(String filePath) {
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					return lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return "";
	}

	// ================================================获取access_token==============================================================
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?"
			+ "grant_type=client_credential&appid=APPID&secret=APPSECRET";

	// 获取access_token
	@RequestMapping(value = "/getAt")
	public void getAt(PrintWriter out) {
		logBefore(logger, "获取access_token");
		try {
			String appid = "wx9f43c8daa1c13934";
			String appsecret = "2c7f6552a5a845b49d47f65dd90beb50";

			String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
			JSONObject jsonObject = httpRequst(requestUrl, "GET", null);

			// System.out.println(jsonObject.getString("access_token")+"============");

			PrintWriter pw;
			try {
				pw = new PrintWriter(new FileWriter("e:/access_token.txt"));
				pw.print(jsonObject.getString("access_token"));
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			out.write("success");
			out.close();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	public JSONObject httpRequst(String requestUrl, String requetMethod, String outputStr) {
		JSONObject jsonobject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的新人管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
			sslcontext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocktFactory对象
			SSLSocketFactory ssf = sslcontext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requetMethod);

			if ("GET".equalsIgnoreCase(requetMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonobject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			// TODO: handle exception
		} catch (Exception e) {
		}
		return jsonobject;
	}
	// ================================================获取access_token==============================================================
}

// ================================================获取access_token==============================================================
class MyX509TrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub

	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub

	}

	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}
}
// ================================================获取access_token==============================================================