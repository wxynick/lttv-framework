package com.letv.mobile.core.rpc.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;

import com.letv.mobile.core.rpc.api.DataEntity;

/**
 * A streamed, non-repeatable entity that obtains its content from 
 * an {@link InputStream}.
 *
 *
 * @version $Revision$
 * 
 * @since 4.0
 */
public class EntityWrapper extends AbstractHttpEntity {

    private final static int BUFFER_SIZE = 2048;

    private final DataEntity content;

    public EntityWrapper(final DataEntity input) {
        super();        
        if (input == null) {
            throw new IllegalArgumentException("Source input data entity may not be null");
        }
        this.content = input;
    }

    public boolean isRepeatable() {
        return this.content.isRepeatable();
    }

    public long getContentLength() {
        return this.content.getContentLength();
    }

    public InputStream getContent() throws IOException {
        return this.content.getContent();
    }
        
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = this.content.getContent();
        byte[] buffer = new byte[BUFFER_SIZE];
        int l;
        while ((l = instream.read(buffer)) != -1) {
            outstream.write(buffer, 0, l);
        }
     }

    // non-javadoc, see interface HttpEntity
    public boolean isStreaming() {
        return false;
    }

    // non-javadoc, see interface HttpEntity
    public void consumeContent() throws IOException {
    }

	/* (non-Javadoc)
	 * @see org.apache.http.entity.AbstractHttpEntity#getContentType()
	 */
	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", content.getContentType());
	}
    
} 