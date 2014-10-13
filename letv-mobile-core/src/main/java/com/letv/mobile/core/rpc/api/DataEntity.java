/**
 * 
 */
package com.letv.mobile.core.rpc.api;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author  
 *
 */
public interface DataEntity {
		
    /**
     * Tells the length of the content, if known.
     *
     * @return  the number of bytes of the content, or
     *          a negative number if unknown. If the content length is known
     *          but exceeds {@link java.lang.Long#MAX_VALUE Long.MAX_VALUE},
     *          a negative number is returned.
     */
	long getContentLength();
	
    /**
     * Obtains the Content-Type header, if known.
     * This is the header that should be used when sending the entity,
     * or the one that was received with the entity. It can include a
     * charset attribute.
     *
     * @return  the Content-Type header for this entity, or
     *          <code>null</code> if the content type is unknown
     */
	String getContentType();
	
    /**
     * Returns a content stream of the entity.
     * {@link #isRepeatable Repeatable} entities are expected
     * to create a new instance of {@link InputStream} for each invocation
     * of this method and therefore can be consumed multiple times.
     * Entities that are not {@link #isRepeatable repeatable} are expected
     * to return the same {@link InputStream} instance and therefore
     * may not be consumed more than once.
     * <p>
     * IMPORTANT: Please note all entity implementations must ensure that
     * all allocated resources are properly deallocated after
     * the {@link InputStream#close()} method is invoked.
     *
     * @return content stream of the entity.
     *
     * @throws IOException if the stream could not be created
     * @throws IllegalStateException
     *  if content stream cannot be created.
     *
     * @see #isRepeatable()
     */
	InputStream getContent() throws IOException ;
	
    /**
     * Tells if the entity is capable of producing its data more than once.
     * A repeatable entity's getContent() methods
     * can be called more than once whereas a non-repeatable entity's can not.
     * @return true if the entity is repeatable, false otherwise.
     */
    boolean isRepeatable();
    
    /**
     * Obtains the Content-Encoding header, if known.
     * This is the header that should be used when sending the entity,
     * or the one that was received with the entity.
     * Wrapping entities that modify the content encoding should
     * adjust this header accordingly.
     *
     * @return  the Content-Encoding String for this entity, or
     *          <code>null</code> if the content encoding is unknown
     */
    String getContentEncoding();
    
    void consumeContent() throws IOException;


}
