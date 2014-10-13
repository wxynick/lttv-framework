/**
 * 
 */
package com.letv.mobile.core.security.api;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author  
 *
 */
public interface ISiteSecurityService {
    /**
     * Get the hostname verifier configured in the client or {@code null} in case
     * no hostname verifier has been configured.
     *
     * @return client hostname verifier or {@code null} if not set.
     */
    public HostnameVerifier getHostnameVerifier();
    
    /**
     * Return the client-side trust store. Trust store is expected to contain certificates from other parties
     * the peer is you expect to communicate with, or from Certificate Authorities that are trusted to
     * identify other parties.
     */
    KeyStore getTrustKeyStore();
    
    
    /**
     * Return the client-side key store. Key store contains client's private keys, and the certificates with their
     * corresponding public keys.
     */
    KeyStore getSiteKeyStore();

}
