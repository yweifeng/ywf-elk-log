package com.ywf.ywfelklog.aop;

import com.alibaba.fastjson.JSONObject;
import com.ywf.ywfelklog.util.WebUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

/**
 * @Author:ywf
 */

@Component
@Aspect
public class LogAspect {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 方法执行之前保存操作日志
     * 本次使用多个切点自定义日志注解，不用切面
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("@annotation(com.ywf.ywfelklog.annotation.SystemLog)")
    public void Log(JoinPoint joinPoint) {
        System.out.println("do elk log ");

        // 获取请求参数
        JSONObject message = new JSONObject();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //获取方法参数
        Enumeration<String> eParams = request.getParameterNames();
        JSONObject params = new JSONObject();
        while (eParams.hasMoreElements()) {
            String key = eParams.nextElement();
            String value = request.getParameter(key);
            params.put(key, value);
        }
        String ip = WebUtil.getIpAddr(request);
        String requestURL = request.getRequestURL().toString();
        message.put("requestURL", requestURL);
        message.put("class", joinPoint.getTarget().getClass().getName());
        message.put("request_method", joinPoint.getSignature().getName());
        message.put("ip", ip);
        message.put("systemName", "ywf-elk-log");
        message.put("params", params.toJSONString());
        String msg = message.toJSONString();
        try {
            System.out.println("======= system log  ======");
            System.out.println("msg:" + msg);
            SendResult<String, String> result = kafkaTemplate.send("ywf-system-log", msg).get();
            System.out.println("发送日志到kafka, result= " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
