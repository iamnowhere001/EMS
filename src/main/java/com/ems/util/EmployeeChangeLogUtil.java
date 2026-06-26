package com.ems.util;

import com.ems.entity.Employee;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

public class EmployeeChangeLogUtil {

    private EmployeeChangeLogUtil() {
    }

    public static String buildChangedFields(Employee oldEmployee, Employee newEmployee) {
        if (oldEmployee == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        appendChanged(sb, "姓名", oldEmployee.getName(), newEmployee.getName(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "年龄", oldEmployee.getAge(), newEmployee.getAge(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "性别", oldEmployee.getGender(), newEmployee.getGender(), EmployeeChangeLogUtil::formatGender);
        appendChanged(sb, "手机号", oldEmployee.getPhone(), newEmployee.getPhone(), EmployeeChangeLogUtil::desensitizePhone);
        appendChanged(sb, "邮箱", oldEmployee.getEmail(), newEmployee.getEmail(), EmployeeChangeLogUtil::desensitizeEmail);
        appendChanged(sb, "身份证号", oldEmployee.getIdCard(), newEmployee.getIdCard(), EmployeeChangeLogUtil::desensitizeIdCard);
        appendChanged(sb, "银行卡号", oldEmployee.getBankCard(), newEmployee.getBankCard(), EmployeeChangeLogUtil::desensitizeBankCard);
        appendChanged(sb, "入职日期", oldEmployee.getHireDate(), newEmployee.getHireDate(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "部门", oldEmployee.getDepartment(), newEmployee.getDepartment(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "职位", oldEmployee.getPosition(), newEmployee.getPosition(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "职级", oldEmployee.getRank(), newEmployee.getRank(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "薪资", oldEmployee.getSalary(), newEmployee.getSalary(), EmployeeChangeLogUtil::formatSalary);
        appendChanged(sb, "状态", oldEmployee.getStatus(), newEmployee.getStatus(), EmployeeChangeLogUtil::formatStatus);
        appendChanged(sb, "紧急联系人", oldEmployee.getEmergencyContact(), newEmployee.getEmergencyContact(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "紧急电话", oldEmployee.getEmergencyPhone(), newEmployee.getEmergencyPhone(), EmployeeChangeLogUtil::desensitizePhone);
        appendChanged(sb, "现住址", oldEmployee.getCurrentAddress(), newEmployee.getCurrentAddress(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "户籍地址", oldEmployee.getHukouAddress(), newEmployee.getHukouAddress(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "政治面貌", oldEmployee.getPoliticalStatus(), newEmployee.getPoliticalStatus(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "婚姻状况", oldEmployee.getMaritalStatus(), newEmployee.getMaritalStatus(), EmployeeChangeLogUtil::formatMarital);
        appendChanged(sb, "民族", oldEmployee.getNation(), newEmployee.getNation(), EmployeeChangeLogUtil::formatValue);
        appendChanged(sb, "籍贯", oldEmployee.getNativePlace(), newEmployee.getNativePlace(), EmployeeChangeLogUtil::formatValue);
        return sb.toString();
    }

    private static void appendChanged(StringBuilder sb, String fieldName, Object oldValue, Object newValue, Function<Object, String> formatter) {
        if (oldValue == null && newValue == null) {
            return;
        }
        boolean changed;
        if (oldValue == null || newValue == null) {
            changed = true;
        } else if (oldValue instanceof BigDecimal && newValue instanceof BigDecimal) {
            changed = ((BigDecimal) oldValue).compareTo((BigDecimal) newValue) != 0;
        } else {
            changed = !Objects.equals(oldValue, newValue);
        }
        if (changed) {
            if (!sb.isEmpty()) {
                sb.append("、");
            }
            String oldText = oldValue == null ? "空" : formatter.apply(oldValue);
            String newText = newValue == null ? "空" : formatter.apply(newValue);
            sb.append(fieldName).append(": ").append(oldText).append(" → ").append(newText);
        }
    }

    private static String formatValue(Object value) {
        return value == null ? "空" : value.toString();
    }

    private static String formatGender(Object value) {
        if (value == null) return "空";
        return Integer.valueOf(1).equals(value) ? "男" : "女";
    }

    private static String formatStatus(Object value) {
        if (value == null) return "空";
        return Integer.valueOf(1).equals(value) ? "在职" : "离职";
    }

    private static String formatMarital(Object value) {
        if (value == null) return "空";
        Integer code = (Integer) value;
        switch (code) {
            case 1: return "已婚";
            case 2: return "离异";
            case 3: return "丧偶";
            default: return "未婚";
        }
    }

    private static String formatSalary(Object value) {
        if (value == null) return "空";
        return "¥" + String.format("%,.2f", new BigDecimal(value.toString()));
    }

    private static String desensitizePhone(Object value) {
        String phone = value == null ? "" : value.toString();
        if (phone.length() != 11) return phone.isEmpty() ? "空" : phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private static String desensitizeEmail(Object value) {
        String email = value == null ? "" : value.toString();
        if (!email.contains("@")) return email.isEmpty() ? "空" : email;
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) return email;
        return name.substring(0, 2) + "****@" + domain;
    }

    private static String desensitizeIdCard(Object value) {
        String idCard = value == null ? "" : value.toString();
        if (idCard.length() <= 8) return idCard.isEmpty() ? "空" : idCard;
        return idCard.substring(0, 4) + "********" + idCard.substring(idCard.length() - 4);
    }

    private static String desensitizeBankCard(Object value) {
        String card = value == null ? "" : value.toString();
        if (card.length() <= 8) return card.isEmpty() ? "空" : card;
        return card.substring(0, 4) + " **** **** " + card.substring(card.length() - 4);
    }
}
