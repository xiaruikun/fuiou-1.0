package com.loan

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

import javax.crypto.Cipher
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.HashMap
import java.util.Map

@Transactional
class RsaService
{
    def base64Service
    /**
     * 加密算法RSA*/
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法*/
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 获取公钥的key*/
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key*/
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小*/
    private static final int MAX_ENCRYPT_BLOCK = 234;

    /**
     * RSA最大解密密文大小*/
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * RSA规定版本(3.0及3.0以上)*/
    private static final String RSA_VER = "3.0";
    /**
     * 生成密钥对(公钥和私钥)
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception
    {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 用私钥对信息生成数字签名
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    def sign(byte[] data, String privateKey) throws Exception
    {
        byte[] keyBytes = base64Service.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return base64Service.encode(signature.sign());
    }

    /**
     * 校验数字签名
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * @return
     * @throws Exception
     */
    def verify(byte[] data, String publicKey, String sign) throws Exception
    {
        byte[] keyBytes = base64Service.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(base64Service.decode(sign));
    }

    /**
     * 私钥解密
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    def decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception
    {
        byte[] keyBytes = base64Service.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0)
        {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK)
            {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            }
            else
            {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    def decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception
    {
        byte[] keyBytes = base64Service.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0)
        {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK)
            {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            }
            else
            {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    def encryptByPublicKey(byte[] data, String publicKey) throws Exception
    {
        byte[] keyBytes = base64Service.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0)
        {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK)
            {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            }
            else
            {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥加密
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    def encryptByPrivateKey(byte[] data, String privateKey) throws Exception
    {
        byte[] keyBytes = base64Service.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0)
        {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK)
            {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            }
            else
            {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 获取私钥
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    def getPrivateKey(Map<String, Object> keyMap) throws Exception
    {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return base64Service.encode(key.getEncoded());
    }

    /**
     * 获取公钥
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    def getPublicKey(Map<String, Object> keyMap) throws Exception
    {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return base64Service.encode(key.getEncoded());
    }

    /**
     *
     * @param reqVer 请求版本
     * @return 是RSA版本返回true，否则返回false
     */
    def isRSA(String reqVer)
    {
        boolean flag = false;
        if (StringUtils.isNotEmpty(reqVer) && (RSA_VER.compareTo(reqVer) == 0 || RSA_VER.compareTo(reqVer) <= -1))
        {
            flag = true;
        }
        return flag;
    }
}
