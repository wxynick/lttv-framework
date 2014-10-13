package com.letv.datastatistics.util;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


public class DEs {
	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	/**
	 * 
	 * @param key 必须长度必须是8的倍数
	 * @param data
	 * @return
	 * @throws Exception
	 */
    public static String encode(String key, byte[] data) throws Exception
    {
        try
        {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);

            byte[] bytes = cipher.doFinal(data);
            return LetvBase64.encode(bytes);
//            return Base64.encodeToString(bytes, 3);
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }
    }
    
    private byte[] desKey;
    
    
    
    public DEs(String desKey) {
		super();
		this.desKey = desKey.getBytes();
	}



	/**
     * ����
     */
    
    public byte[] desEncrypt(byte[] plainText) throws Exception {  
        SecureRandom sr = new SecureRandom();  
        byte rawKeyData[] = desKey;  
        DESKeySpec dks = new DESKeySpec(rawKeyData);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        SecretKey key = keyFactory.generateSecret(dks);  
        Cipher cipher = Cipher.getInstance("DES");  
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);  
        byte data[] = plainText;  
        byte encryptedData[] = cipher.doFinal(data);  
        return encryptedData;  
    }  
    
    /**
     * ����
     * @param encryptText
     * @return
     * @throws Exception
     * 
     */
    
    public byte[] desDecrypt(byte[] encryptText) throws Exception {  
        SecureRandom sr = new SecureRandom();  
        byte rawKeyData[] = desKey;  
        DESKeySpec dks = new DESKeySpec(rawKeyData);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        SecretKey key = keyFactory.generateSecret(dks);  
        Cipher cipher = Cipher.getInstance("DES");  
        cipher.init(Cipher.DECRYPT_MODE, key, sr);  
        byte encryptedData[] = encryptText;  
        byte decryptedData[] = cipher.doFinal(encryptedData);  
        return decryptedData;  
    }  
    
    
    public String encrypt(String input) throws Exception {  
        return LetvBase64.encode(desEncrypt(input.getBytes()));  
    }  
  
    public String decrypt(String input) throws Exception {  
        byte[] result = LetvBase64.decode(input);  
        return new String(desDecrypt(result));  
    }  
    

}
