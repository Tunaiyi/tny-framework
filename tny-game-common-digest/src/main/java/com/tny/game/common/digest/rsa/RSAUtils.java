/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.digest.rsa;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;

import static com.tny.game.common.utils.StringAide.*;

public class RSAUtils {

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

    /**
     * KeyFactory 实例按 JCA 规范非线程安全，逐次创建而非共享静态实例。
     */
    private static KeyFactory newKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(KEY_ALGORITHM);
    }

    /**
     * 使用模和指数生成RSA公钥。
     * 失败方向与 {@link #toPublicKey(String)} 对表：非法输入当场显式失败，不返回空引用。
     *
     * @param modulus  模
     * @param exponent 指数
     * @return 重建的公钥
     * @throws IllegalArgumentException 数值文本非法或模/指数非有效密钥材料（cause 透传原始失败）
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        BigInteger mod = parseKeyComponent(modulus, "RSA 公钥重建失败：模不是合法数值文本（环节：BigInteger 解析）");
        BigInteger exp = parseKeyComponent(exponent, "RSA 公钥重建失败：指数不是合法数值文本（环节：BigInteger 解析）");
        try {
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
            return (RSAPublicKey) newKeyFactory().generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("RSA 公钥重建失败：模/指数不是有效密钥材料（环节：generatePublic）", e);
        }
    }

    /**
     * 使用模和指数生成RSA私钥。
     * 失败方向与 {@link #toPrivateKey(String)} 对表：非法输入当场显式失败，不返回空引用。
     *
     * @param modulus  模
     * @param exponent 指数
     * @return 重建的私钥
     * @throws IllegalArgumentException 数值文本非法或模/指数非有效密钥材料（cause 透传原始失败）
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        BigInteger mod = parseKeyComponent(modulus, "RSA 私钥重建失败：模不是合法数值文本（环节：BigInteger 解析）");
        BigInteger exp = parseKeyComponent(exponent, "RSA 私钥重建失败：指数不是合法数值文本（环节：BigInteger 解析）");
        try {
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(mod, exp);
            return (RSAPrivateKey) newKeyFactory().generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("RSA 私钥重建失败：模/指数不是有效密钥材料（环节：generatePrivate）", e);
        }
    }

    /**
     * 密钥分量的数值文本解析——失败显式抛并指明环节，不回空（消息不带入密钥材料本身）。
     */
    private static BigInteger parseKeyComponent(String value, String failureMessage) {
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(failureMessage, e);
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
     */
    public static RSAPrivateKey toPrivateKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] data = Base64.decodeBase64(key);
        return (RSAPrivateKey) newKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(data));
    }

    /**
     * 公钥字符串转工钥
     *
     * @param key 公钥字符串
     * @return 返回公钥
     */
    public static RSAPublicKey toPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] data = Base64.decodeBase64(key);
        return (RSAPublicKey) newKeyFactory().generatePublic(new X509EncodedKeySpec(data));
    }

    /**
     * 通过私钥key对data内容进行解密<br>
     *
     * @param data 加密的内容
     * @param key  私钥
     * @return 返回解密内容
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)
            throws Exception {
        RSAPrivateKey rsaKey = toPrivateKey(key);
        return decrypt(data, rsaKey);
    }

    public static byte[] decrypt(byte[] data, Key key)
            throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return decryptByCipher(data, cipher, keySizeBytes(key));
    }

    /**
     * 通过公钥key对data内容进行解密<br>
     * 用私钥解密
     *
     * @param data 加密的内容
     * @param key  公钥
     * @return 返回解密内容
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        RSAPublicKey rsaKey = toPublicKey(key);
        return decrypt(data, rsaKey);
    }

    public static byte[] encrypt(byte[] data, Key key)
            throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptByCipher(data, cipher, keySizeBytes(key));
    }

    /**
     * 密钥模数所需字节数——分段块大小的可靠来源（不依赖 provider 的 getBlockSize/getOutputSize 推算）。
     */
    private static int keySizeBytes(Key key) {
        BigInteger modulus;
        if (key instanceof RSAPublicKey publicKey) {
            modulus = publicKey.getModulus();
        } else if (key instanceof RSAPrivateKey privateKey) {
            modulus = privateKey.getModulus();
        } else {
            throw new IllegalArgumentException("非RSA密钥: " + key.getAlgorithm());
        }
        return (modulus.bitLength() + 7) / 8;
    }

    /**
     * 通过私钥key对data内容进行加密<br>
     * 用私钥解密
     *
     * @param data 要加密的内容
     * @param key  私钥
     * @return 返回加密内容
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        RSAPrivateKey rsaKey = toPrivateKey(key);
        return encrypt(data, rsaKey);
    }

    /**
     * 签名
     *
     * @param data           待签名数据
     * @param privateKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static String sign(String data, String privateKeyWord) throws Exception {
        return sign(data, privateKeyWord, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data           待签名数据
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
     * @param data      待签名数据
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
     * @param data      待签名数据
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
     * @param data          待签名数据
     * @param publicKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, String publicKeyWord) throws Exception {
        return verify(sign, data, publicKeyWord, SIGN_MD5_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param data          待签名数据
     * @param publicKeyWord 密钥
     * @return byte[] 数字签名
     */
    public static boolean verify(String sign, String data, String publicKeyWord, String algorithm) throws Exception {
        PublicKey publicKey = toPublicKey(publicKeyWord);
        return verify(sign, data, publicKey, algorithm);
    }

    public static RSAKeyPair getKeyPair(int size) throws Exception {
        if (size % 64 != 0) {
            throw new IllegalArgumentException(format("密码长度 {} 不为64的倍数", size));
        }
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(size);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new RSAKeyPair(keyPair);
    }

    public static RSAKeyPair getKeyPair() throws Exception {
        return getKeyPair(KEY_SIZE);
    }

    /**
     * PKCS1 填充每块占用 11 字节，明文块上限 = 密钥字节数 - 11。
     * （原实现用 getOutputSize(data.length)-11 推算，数据跨块时块长超过密钥上限，
     * doFinal 必抛 IllegalBlockSizeException。）
     */
    private static byte[] encryptByCipher(byte[] data, Cipher cipher, int keySizeBytes)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        int maxEncryptBlock = keySizeBytes - 11;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            return getBytes(data, cipher, maxEncryptBlock, out);
        }
    }

    private static byte[] getBytes(byte[] data, Cipher cipher, int block, ByteArrayOutputStream out)
            throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = data.length;
        int offSet = 0;
        int i = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > block) {
                cache = cipher.doFinal(data, offSet, block);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * block;
        }
        return out.toByteArray();
    }

    /**
     * 密文按密钥模数字节数整块切分（原实现用 getOutputSize(data.length) 推算会得到大于密钥块的长度）。
     */
    private static byte[] decryptByCipher(byte[] data, Cipher cipher, int keySizeBytes)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        // 对数据分段解密
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            return getBytes(data, cipher, keySizeBytes, out);
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
