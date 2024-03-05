package com.example.zodeaccount.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.UUID;

@Component
public class PrintMethodHandler implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PrintMethodHandler.class);
    private static final int maxOutputLengthOfParaValue = 1024;
    private final ThreadLocal<PrintMethodHandler.Common> startThreadLocal = new ThreadLocal();

    public PrintMethodHandler() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            String uuid = UUID.randomUUID().toString();
            this.startThreadLocal.set(new PrintMethodHandler.Common(System.currentTimeMillis(), uuid));
            ThreadContext.put("_log_id", uuid);
            String methodName = ((HandlerMethod)handler).getMethod().getName();
            Class controller = ((HandlerMethod)handler).getBean().getClass();
            String queryStr = request.getQueryString();
            StringBuffer sb = new StringBuffer("----------------------\tDSF-SPRINGBOOT " + uuid + " start\t--------------------\n\n");
            sb.append("\tUrl         : " + request.getRequestURL().toString());
            if (StringUtils.isNotBlank(queryStr)) {
                sb.append("?").append(queryStr);
            }

            sb.append("\n").append("\tController  : ").append(controller.getName()).append(".(").append(controller.getSimpleName()).append(".java:1)").append("\n").append("\tMethod      : " + methodName).append("\n");
            Enumeration<String> e = request.getParameterNames();
            if (e.hasMoreElements()) {
                sb.append("\tParameter   : ");

                for(; e.hasMoreElements(); sb.append("  ")) {
                    String name = (String)e.nextElement();
                    String[] values = request.getParameterValues(name);
                    if (values.length == 1) {
                        sb.append(name).append("=");
                        if (values[0] != null && values[0].length() > 1024) {
                            sb.append(values[0], 0, 1024).append("...");
                        } else {
                            sb.append(values[0]);
                        }
                    } else {
                        sb.append(name).append("[]={");

                        for(int i = 0; i < values.length; ++i) {
                            if (i > 0) {
                                sb.append(",");
                            }

                            sb.append(values[i]);
                        }

                        sb.append("}");
                    }
                }

                sb.append("\n");
            }

            log.debug(sb.toString());
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            log.debug("总执行时间" + (System.currentTimeMillis() - ((PrintMethodHandler.Common)this.startThreadLocal.get()).getStart()) + "ms");
            log.debug("----------------------\tDSF-SPRINGBOOT " + ((PrintMethodHandler.Common)this.startThreadLocal.get()).getUuid() + " end\t\t--------------------\n");
            this.startThreadLocal.remove();
        }
    }

    private class Common {
        private long start;
        private String uuid;

        public Common(long start, String uuid) {
            this.start = start;
            this.uuid = uuid;
        }

        public long getStart() {
            return this.start;
        }

        public String getUuid() {
            return this.uuid;
        }
    }

}
