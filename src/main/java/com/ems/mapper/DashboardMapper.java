package com.ems.mapper;

import com.ems.vo.DashboardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM employee WHERE deleted = 0")
    int countTotal();

    @Select("SELECT COUNT(*) FROM employee WHERE deleted = 0 AND status = 1")
    int countActive();

    @Select("SELECT COUNT(*) FROM employee WHERE deleted = 0 AND status = 0")
    int countLeft();

    @Select("SELECT COUNT(*) FROM employee WHERE deleted = 0 AND DATE_FORMAT(hire_date, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    int countNewHireThisMonth();

    @Select("SELECT COUNT(DISTINCT department) FROM employee WHERE deleted = 0 AND department IS NOT NULL AND department != ''")
    int countDepartment();

    @Select("SELECT COALESCE(SUM(salary), 0) FROM employee WHERE deleted = 0 AND status = 1 AND salary IS NOT NULL")
    BigDecimal sumTotalSalary();

    @Select("SELECT COALESCE(AVG(salary), 0) FROM employee WHERE deleted = 0 AND status = 1 AND salary IS NOT NULL")
    BigDecimal avgSalary();

    @Select("SELECT COALESCE(AVG(DATEDIFF(CURDATE(), hire_date)), 0) FROM employee WHERE deleted = 0 AND status = 1 AND hire_date IS NOT NULL")
    BigDecimal avgTenureDays();

    @Select("SELECT department as name, COUNT(*) as value FROM employee WHERE deleted = 0 AND department IS NOT NULL AND department != '' GROUP BY department ORDER BY value DESC")
    List<DashboardVO.NameValueItem> countByDepartment();

    @Select("<script>" +
            "SELECT " +
            "  CASE " +
            "    WHEN age BETWEEN 20 AND 25 THEN '20-25' " +
            "    WHEN age BETWEEN 26 AND 30 THEN '26-30' " +
            "    WHEN age BETWEEN 31 AND 35 THEN '31-35' " +
            "    WHEN age BETWEEN 36 AND 40 THEN '36-40' " +
            "    WHEN age BETWEEN 41 AND 50 THEN '41-50' " +
            "    WHEN age > 50 THEN '50+' " +
            "  END as name, " +
            "  COUNT(*) as value " +
            "FROM employee WHERE deleted = 0 AND age IS NOT NULL " +
            "AND age >= 20 " +
            "GROUP BY CASE " +
            "    WHEN age BETWEEN 20 AND 25 THEN '20-25' " +
            "    WHEN age BETWEEN 26 AND 30 THEN '26-30' " +
            "    WHEN age BETWEEN 31 AND 35 THEN '31-35' " +
            "    WHEN age BETWEEN 36 AND 40 THEN '36-40' " +
            "    WHEN age BETWEEN 41 AND 50 THEN '41-50' " +
            "    WHEN age > 50 THEN '50+' " +
            "  END " +
            "ORDER BY name" +
            "</script>")
    List<DashboardVO.NameValueItem> countByAge();

    @Select("<script>" +
            "SELECT " +
            "  CASE " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) &lt; 365 THEN '&lt;1年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 365 AND 1095 THEN '1-3年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 1096 AND 1825 THEN '3-5年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 1826 AND 3650 THEN '5-10年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) > 3650 THEN '10年+' " +
            "  END as name, " +
            "  COUNT(*) as value " +
            "FROM employee WHERE deleted = 0 AND hire_date IS NOT NULL " +
            "GROUP BY CASE " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) &lt; 365 THEN '&lt;1年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 365 AND 1095 THEN '1-3年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 1096 AND 1825 THEN '3-5年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) BETWEEN 1826 AND 3650 THEN '5-10年' " +
            "    WHEN DATEDIFF(CURDATE(), hire_date) > 3650 THEN '10年+' " +
            "  END " +
            "ORDER BY name" +
            "</script>")
    List<DashboardVO.NameValueItem> countByTenure();

    @Select("<script>" +
            "SELECT " +
            "  CASE " +
            "    WHEN salary &lt; 5000 THEN '&lt;5k' " +
            "    WHEN salary BETWEEN 5000 AND 10000 THEN '5-10k' " +
            "    WHEN salary BETWEEN 10001 AND 15000 THEN '10-15k' " +
            "    WHEN salary BETWEEN 15001 AND 20000 THEN '15-20k' " +
            "    WHEN salary BETWEEN 20001 AND 30000 THEN '20-30k' " +
            "    WHEN salary > 30000 THEN '30k+' " +
            "  END as name, " +
            "  COUNT(*) as value " +
            "FROM employee WHERE deleted = 0 AND salary IS NOT NULL " +
            "GROUP BY CASE " +
            "    WHEN salary &lt; 5000 THEN '&lt;5k' " +
            "    WHEN salary BETWEEN 5000 AND 10000 THEN '5-10k' " +
            "    WHEN salary BETWEEN 10001 AND 15000 THEN '10-15k' " +
            "    WHEN salary BETWEEN 15001 AND 20000 THEN '15-20k' " +
            "    WHEN salary BETWEEN 20001 AND 30000 THEN '20-30k' " +
            "    WHEN salary > 30000 THEN '30k+' " +
            "  END " +
            "ORDER BY name" +
            "</script>")
    List<DashboardVO.NameValueItem> countBySalary();

    @Select("SELECT CASE gender WHEN 1 THEN '男' WHEN 0 THEN '女' END as name, COUNT(*) as value FROM employee WHERE deleted = 0 AND gender IS NOT NULL GROUP BY gender")
    List<DashboardVO.NameValueItem> countByGender();

    @Select("SELECT DATE_FORMAT(hire_date, '%Y-%m') as month, COUNT(*) as onboard_count FROM employee WHERE deleted = 0 AND hire_date IS NOT NULL AND hire_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH) GROUP BY DATE_FORMAT(hire_date, '%Y-%m') ORDER BY month")
    List<DashboardVO.MonthlyTrendItem> countMonthlyOnboard();

    @Select("<script>" +
            "SELECT " +
            "  t.education as name, " +
            "  COUNT(*) as value " +
            "FROM (" +
            "  SELECT employee_id, education, " +
            "    ROW_NUMBER() OVER (PARTITION BY employee_id ORDER BY " +
            "      CASE education WHEN '博士' THEN 5 WHEN '硕士' THEN 4 WHEN '本科' THEN 3 WHEN '大专' THEN 2 WHEN '高中' THEN 1 ELSE 0 END DESC" +
            "    ) as rn " +
            "  FROM employee_education" +
            ") t " +
            "WHERE rn = 1 AND t.education IS NOT NULL " +
            "GROUP BY t.education " +
            "ORDER BY value DESC" +
            "</script>")
    List<DashboardVO.NameValueItem> countByHighestEducation();

    @Select("SELECT COUNT(*) FROM employee WHERE deleted = 0 AND id NOT IN (SELECT DISTINCT employee_id FROM employee_education)")
    int countNoEducation();

    @Select("SELECT e.department, COUNT(*) as headcount, COALESCE(SUM(e.salary), 0) as total_salary, COALESCE(AVG(e.salary), 0) as avg_salary FROM employee e WHERE deleted = 0 AND status = 1 AND department IS NOT NULL AND department != '' GROUP BY e.department ORDER BY total_salary DESC")
    List<DashboardVO.DepartmentSalaryItem> getDepartmentSalaryStats();

    @Select("SELECT e.rank, COUNT(*) as count FROM employee e WHERE deleted = 0 AND e.rank IS NOT NULL AND e.rank != '' GROUP BY e.rank ORDER BY count DESC")
    List<DashboardVO.RankDistributionItem> countByRank();
}