package com.loan

import org.apache.log4j.Logger

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import grails.transaction.Transactional

@Transactional
class SignatureUtilService
{
    RsaService rsaService
    private static final Logger logger = Logger.getLogger(SignatureUtilService.class);

    def validate(Object bean, String key)
    {
        List<String> values = new ArrayList<String>();
        String signature = null;
        for (
            Method method :
                bean.getClass().getMethods())
        {
            try
            {
                if (!method.getName().startsWith("get") || "getClass".equalsIgnoreCase(method.getName()))
                {
                    continue
                };
                Object o = method.invoke(bean, null);
                if (o != null && StringUtils.isNotEmpty(o.toString()))
                {
                    if ("getSignature".equalsIgnoreCase(method.getName().toLowerCase()))
                    {
                        signature = o.toString();
                    }
                    else
                    {
                        values.add(o.toString());
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        String localSignature = hex(values, key);
        return localSignature.equalsIgnoreCase(signature);
    }

    /**
     * @author honcyGao 20160809
     * @param bean
     * @param key
     * @return
     */
    def validateRsa(Object bean, String key)
    {
        logger.debug("validateRsa  start ");
        List<String> values = new ArrayList<String>();
        String signature = null;
        for (
            Method method :
                bean.getClass().getMethods())
        {
            try
            {
                if (!method.getName().startsWith("get") || "getClass".equalsIgnoreCase(method.getName()))
                {
                    continue
                };
                Object o = method.invoke(bean, null);
                if (o != null && StringUtils.isNotEmpty(o.toString()))
                {
                    if ("getSignature".equalsIgnoreCase(method.getName().toLowerCase()))
                    {
                        signature = o.toString();
                    }
                    else
                    {
                        values.add(o.toString());
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        String[] strs = new String[values.size()];
        for (
            int i = 0;
                i < strs.length;
                i++)
        {
            strs[i] = values.get(i);
        }
        Arrays.sort(strs);
        StringBuffer source = new StringBuffer();
        for (
            String str :
                strs)
        {
            source.append(str).append("|");
        }
        String bigstr = source.substring(0, source.length() - 1);
        logger.debug("RSAbigstr:" + bigstr);
        try
        {
            return rsaService.verify(bigstr.getBytes(), key, signature);
        }
        catch (Exception e)
        {
            logger.debug("RSAbigstr验签出错");
            e.printStackTrace();
        }
        return false;
    }

    def hex(List<String> values, String key)
    {
        String[] strs = new String[values.size()];
        for (
            int i = 0;
                i < strs.length;
                i++)
        {
            strs[i] = values.get(i);
        }
        Arrays.sort(strs);
        StringBuffer source = new StringBuffer();
        for (
            String str :
                strs)
        {
            source.append(str).append("|");
        }
        String bigstr = source.substring(0, source.length() - 1);
        logger.debug("bigstr:" + bigstr);
        System.out.println(bigstr);
        String result = DigestUtils.shaHex(DigestUtils.shaHex(bigstr) + "|" + key);
        logger.debug("bigstr hex result:" + result);
        return result;
    }

}
