package com.loan

import grails.transaction.Transactional
import org.apache.commons.codec.binary.Hex

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Transactional
class Md5Service
{

    private final
    static String[] hexDigits = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"]

    public static String encode(String origin, String charsetname)
    {
        String resultString = null;
        resultString = new String(origin);
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        if (charsetname == null || "".equals(charsetname))
        {
            resultString = Hex.encodeHexString(md.digest(resultString.getBytes()));
        }
        else
        {
            try
            {
                resultString = Hex.encodeHexString(md.digest(resultString.getBytes(charsetname)));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return resultString;
    }

    public static String MD5Encode(String origin, String charsetName)
    {
        origin = origin.trim();
        String resultString = null;
        try
        {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                .getBytes(charsetName)));
        }
        catch (Exception ex)
        {
        }
        return resultString;
    }

    public static String byteArrayToHexString(byte[] b)
    {
        StringBuffer resultSb = new StringBuffer();
        for (
            int i = 0;
                i < b.length;
                i++)
        {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * J ×ª»»byteµ½16½øÖÆ
     *
     * @param b
     * @return
     */
    private static String byteToHexString(byte b)
    {
        int n = b;
        if (n < 0)
        {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
