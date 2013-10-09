/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Jörg Thalheim, Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.skubware.opentraining.activity.settings.sync;

import java.io.*;
import java.net.URI;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import static org.apache.http.HttpStatus.*;

/**
 * REST-Client for communicating with REST-server-API. This class has been
 * designed as a general REST-Client. It has not been designed for a particular
 * web service.
 * 
 */
class RestClient {
	/** Tag for logging */
	public static final String TAG = "RestClient";

	private static final String CONTENT_TYPE = "Content-Type";

	private final String mBaseUri;
	private final String mHostName;
	private DefaultHttpClient mClient;
	private final HttpContext mHttpContext = new BasicHttpContext();

	private static final String MIMETYPE_JSON = "application/json";
	private static String USER_AGENT;

	private final static RedirectHandler sRedirectHandler = new RedirectHandler() {
		@Override
		public boolean isRedirectRequested(HttpResponse httpResponse,
				HttpContext httpContext) {
			return false;
		}

		@Override
		public URI getLocationURI(HttpResponse httpResponse,
				HttpContext httpContext) throws ProtocolException {
			return null;
		}
	};

	/**
	 * Creates a rest client.
	 * 
	 * @param hostname
	 *            The host name of the server
	 * @param port
	 *            The TCP port of the server that should be addressed
	 * @param scheme
	 *            The used protocol scheme
	 * @param versionCode
	 *            The version of the app (used for user agent)
	 * 
	 */
	public RestClient(final String hostname, final int port,
			final String scheme, final int versionCode) {
		final StringBuilder uri = new StringBuilder(scheme);
		uri.append("://");
		uri.append(hostname);
		if (port > 0) {
			uri.append(":");
			uri.append(port);
		}
		mBaseUri = uri.toString();
		mHostName = hostname;

		
		
		//TODO Fix SSL problem before exchanging user data
		// workaround for SSL problems, may lower the security level (man-in-the-middle-attack possible)
		// Android does not use the correct SSL certificate for wger.de and throws the exception 
		// javax.net.ssl.SSLException: hostname in certificate didn't match: <wger.de> != <vela.uberspace.de> OR <vela.uberspace.de> OR <uberspace.de> OR <*.vela.uberspace.de>
		// issue is not too serious as no user data is exchanged at the moment
		Log.w(TAG, "OpenTraining will accept all SSL-certificates. This issue has to be fixed before exchanging real user data.");
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
     
		DefaultHttpClient client = new DefaultHttpClient();

		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		ClientConnectionManager mgr = new ThreadSafeClientConnManager(client.getParams(), registry);
		mClient = new DefaultHttpClient(mgr, client.getParams());

		
		mClient.setRedirectHandler(sRedirectHandler);
		mClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				USER_AGENT);
		
		// Set verifier     
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		

		// set user agent
		USER_AGENT = "opentraining/" + versionCode;
	}

	/**
	 * Sends the HTTP-request
	 * 
	 * @param request
	 *            the request to send
	 * @return the HTTP response
	 * @throws IOException
	 *             if HTTP status is NOT 'OK', 'CREATED' or 'ACCEPTED'
	 */
	protected HttpResponse execute(final HttpUriRequest request)
			throws IOException {
		request.setHeader(CONTENT_TYPE, MIMETYPE_JSON);

		HttpResponse resp = mClient.execute(request, mHttpContext);
		StatusLine status = resp.getStatusLine();
		switch (status.getStatusCode()) {
		case SC_OK:
		case SC_CREATED:
		case SC_ACCEPTED:
			return resp;
		case SC_UNPROCESSABLE_ENTITY:
			String json = readResponseBody(resp);
			throw new IOException(parseJsonError(json));
		default:
			String msg = status.getReasonPhrase() + " ("
					+ status.getStatusCode() + ")";
			throw new IOException(msg + "\n" + readResponseBody(resp));
		}
	}

	/**
	 * Sends a raw HTTP-post-request
	 */
	protected HttpResponse raw_post(final String path, final String data)
			throws IOException {
		HttpPost request = new HttpPost(createUri(path));
		request.setHeader(CONTENT_TYPE, MIMETYPE_JSON);
		request.setEntity(new StringEntity(data));
		return mClient.execute(request, mHttpContext);
	}

	/**
	 * Sends a raw HTTP-GET-request
	 */
	protected HttpResponse raw_get(final String path) throws IOException {
		HttpGet request = new HttpGet(createUri(path));
		request.setHeader(CONTENT_TYPE, MIMETYPE_JSON);
		return mClient.execute(request, mHttpContext);
	}

	/**
	 * Sends a HTTP-GET-request to the path <code>path</code>
	 * 
	 * @param path
	 *            the path of the resource
	 * @return the response body
	 * @throws IOException
	 */
	public String get(String path) throws IOException {
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		HttpResponse resp = execute(new HttpGet(createUri(path)));
		return readResponseBody(resp);
	}

	/**
	 * Sends a HTTP-PUT-Request to the path <code>path</code> with
	 * <code>data</code> as body
	 * 
	 * @param path
	 *            the path of the resource
	 * @param data
	 *            the data that has been sent
	 * @return the response body
	 * @throws IOException
	 */
	public String put(String path, String data) throws IOException {
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");
		HttpPut req = new HttpPut(createUri(path));
		req.setEntity(new StringEntity(data));
		HttpResponse resp = execute(req);
		return readResponseBody(resp);
	}

	/**
	 * Sends a HTTP-POST-request to the path <code>path</code> with
	 * <code>data</code> as body
	 * 
	 * @param path
	 *            the path of the resource
	 * @param data
	 *            the data that has been sent
	 * @return the response body
	 * @throws IOException
	 */
	public String post(String path, String data) throws IOException {
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");
		HttpPost req = new HttpPost(createUri(path));
		req.setEntity(new StringEntity(data));
		HttpResponse resp = execute(req);
		return readResponseBody(resp);
	}

	/**
	 * Sends a HTTP DELETE-request to the path <code>path</code>
	 * 
	 * @param path
	 *            the path of the resource
	 * @throws IOException
	 */
	public void delete(String path) throws IOException {
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		execute(new HttpDelete(createUri(path)));
	}

	/**
	 * Creates the full URI (including host) for path
	 * 
	 * @param path
	 *            the path
	 * @return the full URI
	 */
	protected String createUri(final String path) {
		return mBaseUri + path;
	}

	/**
	 * Returns the body of an HTTP request as String
	 * 
	 * @param response
	 *            the HTTP-response
	 * @return the body
	 * @throws IOException
	 */
	protected static String readResponseBody(HttpResponse response)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		InputStream in = null;
		BufferedReader buffer = null;
		try {
			in = response.getEntity().getContent();
			buffer = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while (true) {
				String s = buffer.readLine();
				if (s == null || s.length() == 0) {
					break;
				}
				builder.append(s);
			}
			return builder.toString();
		} finally {
			if (buffer != null) {
				buffer.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Get the first occurrence of a cookie named like this
	 * 
	 * @return the value if the cookie exists, otherwise null
	 */
	public String getCookie(String name) {
		List<Cookie> cookies = mClient.getCookieStore().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * Set a cookie, all previous cookies will be cleared.
	 * 
	 * @param name
	 *            the cookie name
	 * @param value
	 *            its value
	 */
	public void setCookie(String name, String value) {
		CookieStore store = mClient.getCookieStore();
		store.clear();
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(mHostName);
		store.addCookie(cookie);
	}

	private String parseJsonError(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return obj.getString("error");
		} catch (JSONException e) {
			Log.e(TAG, "JSONException", e);
			return e.getMessage();
		}
	}
}
