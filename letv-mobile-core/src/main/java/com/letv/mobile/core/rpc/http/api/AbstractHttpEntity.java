package com.letv.mobile.core.rpc.http.api;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for entities.
 * Provides the commonly used attributes for streamed and self-contained
 * implementations of {@link HttpEntity HttpEntity}.
 *
 * @since 4.0
 */
public abstract class AbstractHttpEntity implements HttpEntity {

    protected String contentType;
    protected String contentEncoding;
    protected boolean chunked;

    /**
     * Protected default constructor.
     * The contentType, contentEncoding and chunked attributes of the created object are set to
     * <code>null</code>, <code>null</code> and <code>false</code>, respectively.
     */
    protected AbstractHttpEntity() {
        super();
    }


    /**
     * Obtains the Content-Type header.
     * The default implementation returns the value of the
     * {@link #contentType contentType} attribute.
     *
     * @return  the Content-Type header, or <code>null</code>
     */
    public String getContentType() {
        return this.contentType;
    }


    /**
     * Obtains the Content-Encoding header.
     * The default implementation returns the value of the
     * {@link #contentEncoding contentEncoding} attribute.
     *
     * @return  the Content-Encoding header, or <code>null</code>
     */
    public String getContentEncoding() {
        return this.contentEncoding;
    }

    /**
     * Obtains the 'chunked' flag.
     * The default implementation returns the value of the
     * {@link #chunked chunked} attribute.
     *
     * @return  the 'chunked' flag
     */
    public boolean isChunked() {
        return this.chunked;
    }


    /**
     * Specifies the Content-Type header.
     * The default implementation sets the value of the
     * {@link #contentType contentType} attribute.
     *
     * @param contentType       the new Content-Encoding header, or
     *                          <code>null</code> to unset
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }



    /**
     * Specifies the Content-Encoding header.
     * The default implementation sets the value of the
     * {@link #contentEncoding contentEncoding} attribute.
     *
     * @param contentEncoding   the new Content-Encoding header, or
     *                          <code>null</code> to unset
     */
    public void setContentEncoding(final String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }



    /**
     * Specifies the 'chunked' flag.
     * <p>
     * Note that the chunked setting is a hint only.
     * If using HTTP/1.0, chunking is never performed.
     * Otherwise, even if chunked is false, HttpClient must
     * use chunk coding if the entity content length is
     * unknown (-1).
     * <p>
     * The default implementation sets the value of the
     * {@link #chunked chunked} attribute.
     *
     * @param b         the new 'chunked' flag
     */
    public void setChunked(boolean b) {
        this.chunked = b;
    }


    /**
     * The default implementation does not consume anything.
     *
     * @deprecated Either use {@link #getContent()} and call {@link java.io.InputStream#close()} on that;
     * otherwise call {@link #writeTo(OutputStream)} which is required to free the resources.
     */
    public void consumeContent() throws IOException {
    }

}