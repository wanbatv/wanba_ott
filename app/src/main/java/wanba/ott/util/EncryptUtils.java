package wanba.ott.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 
 */

/**
 * @author zz
 * 
 */
public class EncryptUtils {

    public static void main(String[] args) {
        String stbCode = "sptest";
        String tranId = stbCode + "20150330102800";
        String serviceCode = "testservice";
        String spCode = "sptest";
        String spKey = "fbbf042f25750e04";
        String value = stbCode + "#" + spCode + "#" + serviceCode + "#"
                + tranId;
        try {
            byte[] keys = hexStr(spKey);
            byte[] encrys = encrypt(value.getBytes(), keys);
            String result = MD5(encrys);
            System.out.println("@@@ encrypt = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String stbCode, String tranId, String serviceCode, String spCode, String spKey) {
        String value = stbCode + "#" + spCode + "#" + serviceCode + "#"
                + tranId;
        String result = null;
        try {
            byte[] keys = hexStr(spKey);
            byte[] encrys = encrypt(value.getBytes(), keys);
            result = MD5(encrys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static byte[] encrypt(byte[] str, byte[] key) throws Exception {
        String DES = "DES";
        String DES_WITH_NOPADDING = "DES/CBC/NoPadding";

        Cipher cipher = Cipher.getInstance(DES_WITH_NOPADDING);
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey skey = keyFactory.generateSecret(dks);
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        AlgorithmParameterSpec paramSpec = iv;
        cipher.init(Cipher.ENCRYPT_MODE, skey, paramSpec);
        ByteArrayInputStream in = new ByteArrayInputStream(str);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encrypt(in, out, cipher);
        return out.toByteArray();
    }

    private static void encrypt(InputStream in, OutputStream out, Cipher cipher) {
        try {
            // Bytes written to out will be encrypted
            out = new CipherOutputStream(out, cipher);
            byte[] buf = new byte[cipher.getBlockSize()];

            // Read in the cleartext bytes and write to out to encrypt
            int numRead = 0;
            while (true) {
                numRead = in.read(buf);
                boolean bBreak = false;
                if (numRead == -1 || numRead < buf.length) {
                    int pos = numRead == -1 ? 0 : numRead;
                    byte byteFill = (byte) (buf.length - pos);
                    for (int i = pos; i < buf.length; ++i) {
                        buf[i] = byteFill;
                    }
                    bBreak = true;
                }
                out.write(buf);

                if (bBreak)
                    break;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字节数组转换为16进制字符串.
     * 
     * @param data
     *            进行转换的字节数组
     * @return 16进制的字符串
     */
    private static String byte2HexString(byte[] data) {
        StringBuffer checksumSb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hexStr = Integer.toHexString(0x00ff & data[i]);
            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }
            checksumSb.append(hexStr);
        }
        return checksumSb.toString();
    }

    /**
     * 16进制字符串转换为字节数组.
     * 
     * @param digits
     *            16进制的字符串
     * @return 字符数组
     */
    private static byte[] hexStr(String digits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < digits.length(); i += 2) {
            char c1 = digits.charAt(i);
            if ((i + 1) >= digits.length())
                throw new IllegalArgumentException();
            char c2 = digits.charAt(i + 1);
            byte b = 0;
            if ((c1 >= '0') && (c1 <= '9'))
                b += ((c1 - '0') * 16);
            else if ((c1 >= 'a') && (c1 <= 'f'))
                b += ((c1 - 'a' + 10) * 16);
            else if ((c1 >= 'A') && (c1 <= 'F'))
                b += ((c1 - 'A' + 10) * 16);
            else
                throw new IllegalArgumentException();
            if ((c2 >= '0') && (c2 <= '9'))
                b += (c2 - '0');
            else if ((c2 >= 'a') && (c2 <= 'f'))
                b += (c2 - 'a' + 10);
            else if ((c2 >= 'A') && (c2 <= 'F'))
                b += (c2 - 'A' + 10);
            else
                throw new IllegalArgumentException();
            baos.write(b);
        }
        return (baos.toByteArray());
    }

    /**
     * 生成MD5摘要.
     * 
     * @param str
     *            生进行摘要的内容
     * @return 返回摘要16进制串
     */
    private static String MD5(final byte[] str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return byte2HexString(messageDigest.digest(str));

        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage());
        }
    }
}
