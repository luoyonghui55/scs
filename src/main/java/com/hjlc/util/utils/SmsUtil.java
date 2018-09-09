package com.hjlc.util.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;

/**
 * 通过短信接口发送短信
 */
public class SmsUtil {
	private static String SMS_DOMAIN = "api01.monyun.cn";//短信平台域名
	private static String SMS_PORT = "7901";
	private static String REQUEST_PATH= "/sms/v2/std/";
	private static final String SMS_PWD = "bEq7n0";//加密密码
	private static final String SMS_USER_ID = "E1054O";
	//private static final String SMS_API_KEY = "f4397ac209b8570d7d323b285040bc02";
	private static PoolingHttpClientConnectionManager pool;
	// 请求配置
	private static RequestConfig requestConfig;

	static {
		try {
			// System.out.println("初始化HttpClientTest~~~开始");
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
			// 配置同时支持 HTTP 和 HTPPS
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
			// 初始化连接管理器
			pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			// 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
			pool.setMaxTotal(200);
			// 设置最大路由
			pool.setDefaultMaxPerRoute(2);
			// 根据默认超时限制初始化requestConfig
			int socketTimeout = 10000;
			int connectTimeout = 10000;
			int connectionRequestTimeout = 10000;
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
					.setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();

			// System.out.println("初始化HttpClientTest~~~结束");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		// 设置请求超时时间
		requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000).build();
	}

	/*public static void main(String [] args) {
		//注：“您的验证码是7994，在20分钟内有效。如非本人操作请忽略本短信。”为模板中的内容，需在平台管理页面中修改。
		singleSend("18670787994","您的验证码是7994，在20分钟内有效。如非本人操作请忽略本短信。");
		//sendSms2("199","您的验证码是：1111。请不要把验证码泄露给其他人。");
		//sendSmsAll(List<PageData> list)
		//sendSms1();
	}*/
	//暂时考虑使用短信平台，相对优惠一些，报价查看地址：http://www.monyun.cn/portalpage/ser_msgVerf.html#section3
	/**
	 *  单条短信发送 到单个手机上
	 * @param userid  用户账号
	 * @param pwd 用户密码
	 * @param isEncryptPwd 密码是否加密   true：密码加密;false：密码不加密
	 */
	public static int singleSend(String phone,String content){
		// 返回值
		int result = -310099;
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try	{
			// 设置内容
			content = URLEncoder.encode(content, "GBK");
			String timestamp = sdf.format(Calendar.getInstance().getTime());
			String ip = InetAddress.getByName(SMS_DOMAIN).getHostAddress();
			String httpUrl = "http://" + ip + ":" + SMS_PORT + REQUEST_PATH + "single_send";
			
			String encryptPassword = encryptPwd(SMS_USER_ID, SMS_PWD, timestamp);
			String params = "userid="+SMS_USER_ID+"&pwd="+encryptPassword+"&mobile="+phone+
					"&content="+content+"&timestamp=" + timestamp;
			HttpPost httpPost = new HttpPost(httpUrl);
			httpPost.setHeader("Content-Type", "text/x-www-form-urlencoded");
			StringEntity paramsEntity = new StringEntity(params, "UTF-8");
			httpPost.setEntity(paramsEntity);

			httpClient = org.apache.http.impl.client.HttpClients.custom()
		                 // 设置连接池管理
		                 .setConnectionManager(pool)
		                 // 设置请求配置
		                 .setDefaultRequestConfig(requestConfig)
		                 // 设置重试次数
		                 .setRetryHandler(new DefaultHttpRequestRetryHandler(1, false))
		                 .build();

			httpPost.setConfig(requestConfig);
			
			// 向网关请求
			httpResponse = httpClient.execute(httpPost);
			// 若状态码为200，则代表请求成功
			if(httpResponse!=null && httpResponse.getStatusLine().getStatusCode()==200)	{
				return 0;
			}
			//打印错误信息
			InputStream inputStream = httpResponse.getEntity().getContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8.name());
			String httpResult = writer.toString();
			System.out.println("sms请求失败："+httpResponse.getStatusLine().toString() + ",httpResult=" + httpResult);
			return result;
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			// 关闭连接
			if(httpResponse != null){
				try	{
					httpResponse.close();
				}catch (Exception e2){
					e2.printStackTrace();
				}
			}
		}
		return result;
	}
	/**
	 * 加密密码
	 * @param userid
	 * @param pwd
	 * @param timestamp
	 * @return
	 */
	public static String encryptPwd(String userid, String pwd, String timestamp){
		// 加密后的字符串
		StringBuffer md5StrBuff = new StringBuffer();
		try	{
			String passwordStr = userid.toUpperCase() + "00000000" + pwd + timestamp;
			// 对密码进行加密
			//MessageDigest messageDigest = null;
			// 加密前的准备
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(passwordStr.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();
			for (int i = 0; i < byteArray.length; i++){
				if(Integer.toHexString(0xFF & byteArray[i]).length() == 1){
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				}else{ 
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		// 返回加密字符串
		return md5StrBuff.toString();
	}
	 //短信商 一  http://www.dxton.com/
	/**
	 * 给一个人发送单条短信
	 * @param mobile 手机号
	 * @param code  短信内容
	 */
 	public static void sendSms1(String mobile,String code){
 		
	    String account = "", password = "";
	    String strSMS1 = Tools.readTxtFile(Constants.SMS1);			//读取短信1配置
		if(null != strSMS1 && !"".equals(strSMS1)){
			String strS1[] = strSMS1.split(",fh,");
			if(strS1.length == 2){
				account = strS1[0];
				password = strS1[1];
			}
		}
 		String PostData = "";
		try {
			PostData = "account="+account+"&password="+password+"&mobile="+
					mobile+"&content="+URLEncoder.encode(code,"utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("短信提交失败");
		}
		 //System.out.println(PostData);
 	     String ret = SMS(PostData, "http://sms.106jiekou.com/utf8/sms.aspx");
 	     System.out.println(ret);
 	   /*  
 	   100			发送成功
 	   101			验证失败
 	   102			手机号码格式不正确
 	   103			会员级别不够
 	   104			内容未审核
 	   105			内容过多
 	   106			账户余额不足
 	   107			Ip受限
 	   108			手机号码发送太频繁，请换号或隔天再发
 	   109			帐号被锁定
 	   110			发送通道不正确
 	   111			当前时间段禁止短信发送
 	   120			系统升级
		*/
 	     
	}
	
	public static String SMS(String postData, String postUrl) {
		try {
			// 发送POST请求
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(postData);
			out.flush();
			out.close();

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("connect failed!");
				return "";
			}
			// 获取响应内容体
			String line, result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			in.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return "";
	}
	 //===================================================================================================================

	/**
	 * 短信商 二  http://www.ihuyi.com/ =====================================================================================
	 * 
	 */
	private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

	/**
	 * 给一个人发送单条短信
	 * @param mobile 手机号
	 * @param code  短信内容
	 */
	public static void sendSms2(String mobile,String code){
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url); 
			
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

	    String content = new String(code);  
	    
	    String account = "", password = "";
	    String strSMS2 = Tools.readTxtFile(Constants.SMS2);	//读取短信2配置
		if(null != strSMS2 && !"".equals(strSMS2)){
			String strS2[] = strSMS2.split(",fh,");
			if(strS2.length == 2){
				account = strS2[0];
				password = strS2[1];
			}
		}
	    
		NameValuePair[] data = {//提交短信
		    new NameValuePair("account", account), 
		    new NameValuePair("password", password), //密码可以使用明文密码或使用32位MD5加密
		    new NameValuePair("mobile", mobile), 
		    new NameValuePair("content", content),
		};
		
		method.setRequestBody(data);
		
		try {
			client.executeMethod(method);
			String SubmitResult =method.getResponseBodyAsString();
			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();

			code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");
			
			
			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);
			
			if(code == "2"){
				System.out.println("短信提交成功");
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 给多个人发送单条短信
	 * @param list 手机号验证码
	 */
	public static void sendSmsAll(List<PageData> list){
		String code;
		String mobile;
		for(int i=0;i<list.size();i++){
			code=list.get(i).get("code").toString();
			mobile=list.get(i).get("mobile").toString();
			sendSms2(mobile,code);
		}
	}
	// ================================
}