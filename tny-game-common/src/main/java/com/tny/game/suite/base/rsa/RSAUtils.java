package com.tny.game.suite.base.rsa;

import com.tny.game.suite.base.Logs;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtils {

    private static final KeyFactory keyFactory;

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 数字签名
     * 签名/验证算法
     */
    public static final String SIGN_MD5_ALGORITHM = "MD5withRSA";
    public static final String SIGN_SHA1_ALGORITHMS = "SHA1WithRSA";

    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用模和指数生成RSA公钥
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger mod = new BigInteger(modulus);
            BigInteger exp = new BigInteger(exponent);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger mod = new BigInteger(modulus);
            BigInteger exp = new BigInteger(exponent);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(mod, exp);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * key 转换 base64 字符串
     *
     * @param key 转换的key
     * @return 返回字符串
     */
    public static String key2Base64(Key key) {
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 私钥字符串转私钥
     *
     * @param key 私钥字符串
     * @return 返回私钥
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey toPrivateKey(String key) throws InvalidKeySpecException {
        byte[] data = Base64.decodeBase64(key);
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(data));
    }

    /**
     * 公钥字符串转工钥
     *
     * @param key 公钥字符串
     * @return 返回公钥
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey toPublicKey(String key) throws InvalidKeySpecException {
        byte[] data = Base64.decodeBase64(key);
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(data));
    }

    /**
     * 通过私钥key对data内容进行解密<br>
     *
     * @param data 加密的内容
     * @param key  私钥
     * @return 返回解密内容
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)
            throws Exception {
        RSAPrivateKey rsaKey = toPrivateKey(key);
        return decrypt(data, rsaKey);
    }

    public static byte[] decrypt(byte[] data, Key key)
            throws Exception {
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return decryptByCipher(data, cipher);
    }

    /**
     * 通过公钥key对data内容进行解密<br>
     * 用私钥解密
     *
     * @param data 加密的内容
     * @param key  公钥
     * @return 返回解密内容
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        RSAPublicKey rsaKey = toPublicKey(key);
        return decrypt(data, rsaKey);
    }

    public static byte[] encrypt(byte[] data, Key key)
            throws Exception {
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptByCipher(data, cipher);
    }

    /**
     * 通过私钥key对data内容进行加密<br>
     * 用私钥解密
     *
     * @param data 要加密的内容
     * @param key  私钥
     * @return 返回加密内容
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        RSAPrivateKey rsaKey = toPrivateKey(key);
        return encrypt(data, rsaKey);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static String sign(String data, String privateKeyWord) throws Exception {
        return sign(data, privateKeyWord, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static String sign(String data, String privateKeyWord, String algorithm) throws Exception {
        PrivateKey privateKey = RSAUtils.toPrivateKey(privateKeyWord);
        return sign(data, privateKey, algorithm);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 密钥
     * @return byte[] 数字签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        return sign(data, privateKey, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 密钥
     * @return byte[] 数字签名
     */
    public static String sign(String data, PrivateKey privateKey, String algorithm) throws Exception {
        return Base64.encodeBase64String(sign(data.getBytes(), privateKey, algorithm));
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 密钥
     * @return byte[] 数字签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        return sign(data, privateKey, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 密钥
     * @return byte[] 数字签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 签名
     *
     * @param data      待签名数据
     * @param publicKey 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(byte[] sign, byte[] data, PublicKey publicKey) throws Exception {
        return verify(sign, data, publicKey, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param publicKey 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(byte[] sign, byte[] data, PublicKey publicKey, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    /**
     * 签名
     *
     * @param data      待签名数据
     * @param publicKey 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, PublicKey publicKey) throws Exception {
        return verify(sign, data, publicKey, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param publicKey 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, PublicKey publicKey, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * 签名
     *
     * @param data      待签名数据
     * @param publicKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, String publicKeyWord) throws Exception {
        return verify(sign, data, publicKeyWord, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param publicKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, String publicKeyWord, String algorithm) throws Exception {
        PublicKey publicKey = toPublicKey(publicKeyWord);
        return verify(sign, data, publicKey, algorithm);
    }

    public static RSAKeyPair getKeyPair(int size) throws Exception {
        if (size % 64 != 0)
            throw new IllegalArgumentException(Logs.format("密码长度 {} 不为64的倍数", size));
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKeyPair(privateKey, publicKey);
    }

    public static RSAKeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKeyPair(privateKey, publicKey);
    }

    private static byte[] encryptByCipher(byte[] data, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, IOException, NoSuchAlgorithmException {
        int maxEncryptBlock = cipher.getOutputSize(data.length) - 11;
        int inputLen = data.length;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > maxEncryptBlock) {
                    cache = cipher.doFinal(data, offSet, maxEncryptBlock);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * maxEncryptBlock;
            }
            byte[] encryptedData = out.toByteArray();
            return encryptedData;
        }
    }

    private static byte[] decryptByCipher(byte[] data, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, IOException {
        int maxdecryptBlock = cipher.getOutputSize(data.length);
        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > maxdecryptBlock) {
                    cache = cipher.doFinal(data, offSet, maxdecryptBlock);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * maxdecryptBlock;
            }
            byte[] decryptedData = out.toByteArray();
            return decryptedData;
        }
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey1 = (RSAPublicKey) keyPair.getPublic();
        byte[] pubKeyBytes1 = publicKey1.getEncoded();
        System.out.println("public Key: ");
        System.out.println(Base64.encodeBase64String(pubKeyBytes1));
        RSAPrivateKey privateKey1 = (RSAPrivateKey) keyPair.getPrivate();
        byte[] priKeyBytes1 = privateKey1.getEncoded();
        System.out.println("private Key: ");
        System.out.println(Base64.encodeBase64String(priKeyBytes1));

        String context = "你是猴子请来的逗逼吗?";
        keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512);
        keyPair = keyPairGen.generateKeyPair();
        publicKey1 = (RSAPublicKey) keyPair.getPublic();
        pubKeyBytes1 = publicKey1.getEncoded();
        System.out.println("public Key: ");
        System.out.println(Base64.encodeBase64String(pubKeyBytes1));
        privateKey1 = (RSAPrivateKey) keyPair.getPrivate();
        priKeyBytes1 = privateKey1.getEncoded();
        System.out.println("private Key: ");
        System.out.println(Base64.encodeBase64String(priKeyBytes1));
        byte[] publicRsaData = encrypt(context.getBytes(), publicKey1);
        byte[] privateRsaData = encrypt(context.getBytes(), privateKey1);

        System.out.println(publicRsaData.length);
        System.out.println(Base64.encodeBase64String(publicRsaData).length());
        System.out.println(privateRsaData.length);
        System.out.println(Base64.encodeBase64String(privateRsaData).length());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey2 = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyBytes1));
        RSAPrivateKey privateKey2 = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(priKeyBytes1));
        System.out.println(new String(decrypt(publicRsaData, privateKey2)));
        System.out.println(new String(decrypt(privateRsaData, publicKey2)));

        byte[] priSign = sign(context.getBytes(), privateKey1);
        System.out.println(verify(context.getBytes(), priSign, publicKey1));

    }
}
