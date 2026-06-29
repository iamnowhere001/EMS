package com.ems.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限校验注解
 * 用法：在 Controller 方法上标注 @RequiresPermission("employee:delete")
 *       可标注多个权限，满足任一即可通过：@RequiresPermission({"employee:delete", "employee:edit"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String[] value();
}
