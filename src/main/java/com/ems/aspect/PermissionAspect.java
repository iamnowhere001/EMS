package com.ems.aspect;

import com.ems.common.AuthContext;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(com.ems.common.RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> permissions = AuthContext.getPermissions();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        String[] required = annotation.value();

        if (permissions != null && permissions.contains("*")) {
            return joinPoint.proceed();
        }

        boolean hasPermission = false;
        if (permissions != null) {
            for (String r : required) {
                if (permissions.contains(r)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletResponse response = attrs.getResponse();
                if (response != null) {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"code\":403,\"message\":\"\\u6743\\u9650\\u4e0d\\u8db3\\uff0c\\u65e0\\u6cd5\\u6267\\u884c\\u6b64\\u64cd\\u4f5c\",\"data\":null}");
                }
            }
            return null;
        }

        return joinPoint.proceed();
    }
}
