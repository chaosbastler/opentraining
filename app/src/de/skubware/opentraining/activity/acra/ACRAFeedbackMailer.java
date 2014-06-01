package de.skubware.opentraining.activity.acra;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.acra.collector.CrashReportData;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/** 
 * Based on acra-mailer(https://github.com/d-a-n/acra-mailer) of d-a-n. 
 */
public class ACRAFeedbackMailer implements ReportSender {
	private final static String BASE_URL = "http://skubware.de/opentraining/acra_feedback.php";
	private final static String SHARED_SECRET = "my_on_github_with_everyone_shared_secret";
	private Map<String, String> custom_data = null;

	public ACRAFeedbackMailer() {
	}

	public ACRAFeedbackMailer(HashMap<String, String> custom_data) {
		this.custom_data = custom_data;
	}

	@Override
	public void send(CrashReportData report) throws ReportSenderException {

		String url = getUrl();
		Log.e("xenim", url);

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();

			if (custom_data != null) {
				for (Map.Entry<String, String> entry : custom_data.entrySet()) {
					parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			parameters.add(new BasicNameValuePair("DATE", new Date().toString()));
			parameters.add(new BasicNameValuePair("APP_VERSION_NAME", report.get(ReportField.APP_VERSION_NAME)));
			parameters.add(new BasicNameValuePair("CUSTOM_DATA", report.get(ReportField.CUSTOM_DATA)));
			
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
			httpClient.execute(httpPost);
			
			
			// set the crash report sender again after sending the feedback
			ACRA.getErrorReporter().removeAllReportSenders();
	        ACRA.getErrorReporter().setReportSender(new ACRACrashReportMailer());
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUrl() {
		String token = getToken();
		String key = getKey(token);
		return String.format("%s?token=%s&key=%s&", BASE_URL, token, key);
	}

	private String getKey(String token) {
		return md5(String.format("%s+%s", SHARED_SECRET, token));
	}

	private String getToken() {
		return md5(UUID.randomUUID().toString());
	}

	public static String md5(String s) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(s.getBytes(), 0, s.length());
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}
}
