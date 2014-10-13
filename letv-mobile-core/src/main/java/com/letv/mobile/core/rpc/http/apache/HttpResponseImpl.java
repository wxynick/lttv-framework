/**
 * 
 */
package com.letv.mobile.core.rpc.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.letv.mobile.core.rpc.api.DataEntity;
import com.letv.mobile.core.rpc.api.Status;
import com.letv.mobile.core.rpc.http.api.HttpResponse;
import com.letv.mobile.core.rpc.http.api.HttpStatus;

/**
 * @author  
 *
 */
public class HttpResponseImpl implements HttpResponse {

	private final org.apache.http.HttpResponse response;
	private Map<String, String> headers;

	public HttpResponseImpl(org.apache.http.HttpResponse res){
		this.response = res;
		this.headers = extractHeaders();
	}
	@Override
	public Status getStatus() {
		return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ? Status.OK : Status.FAILED;
	}

	@Override
	public DataEntity getResponseEntity() {
		final HttpEntity entity = response.getEntity();
		return entity == null ? null : new com.letv.mobile.core.rpc.http.api.HttpEntity() {

			@Override
			public boolean isRepeatable() {
				return entity.isRepeatable();
			}

			@Override
			public String getContentType() {
				return entity.getContentType().getValue();
			}

			@Override
			public long getContentLength() {
				return entity.getContentLength();
			}

			@Override
			public InputStream getContent() throws IOException{
				if("gzip".equalsIgnoreCase(getContentEncoding())){
					return new GZIPInputStream(entity.getContent());
				}else{
					return entity.getContent();
				}
			}

			@Override
			public String getContentEncoding() {
				Header h = entity.getContentEncoding();
				return h != null ? h.getValue() : null;
			}

			@Override
			public void consumeContent() throws IOException{
				entity.consumeContent();
			}
		};
	}

	@Override
	public String getStatusText() {
		return response.getStatusLine().toString();
	}

	@Override
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	private Map<String, String> extractHeaders() {
		org.apache.http.Header[] headers = response.getAllHeaders();
		HashMap<String, String> map = null;
		if((headers != null)&&(headers.length > 0)){
			map = new HashMap<String, String>();
			for (org.apache.http.Header header : headers) {
				String name = header.getName();
				if(map.containsKey(name)){
					continue;
				}
				map.put(name, extractHeader(name));
			}
		}
		return map;
	}

	private String extractHeader(String key) {
		org.apache.http.Header[] vals = response.getHeaders(key);
		if((vals == null)||(vals.length == 0)){
			return null;
		}
		StringBuffer buf = new StringBuffer();
		int cnt = 0;
		for (org.apache.http.Header h : vals) {
			String val = h.getValue();
			if(val != null){
				if(cnt > 0){
					buf.append(',');
				}
				buf.append(val);
				cnt++;
			}
		}
		return buf.toString();
	}

	@Override
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(this.headers);
	}

	@Override
	public String getHeader(String key) {
		return this.headers.get(key);
	}
}
