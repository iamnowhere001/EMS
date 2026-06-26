package com.ems.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class BatchOperationDTO {

    public static class Transfer {
        @NotEmpty(message = "员工ID列表不能为空")
        private List<Long> ids;

        @NotNull(message = "部门不能为空")
        private String department;

        @NotNull(message = "职位不能为空")
        private String position;

        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }

    public static class AdjustSalary {
        @NotEmpty(message = "员工ID列表不能为空")
        private List<Long> ids;

        // 调整模式: fixed(固定金额 +/-) percent(百分比 +/-) set(直接设置)
        @NotNull(message = "调整模式不能为空")
        private String mode;

        // 调整值（正数）
        @NotNull(message = "调整值不能为空")
        private BigDecimal amount;

        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}
