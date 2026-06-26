package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    @Select("SELECT MAX(CAST(employee_no AS UNSIGNED)) FROM employee")
    Integer selectMaxEmployeeNo();

    @Select("SELECT MAX(sort_order) FROM employee")
    Integer selectMaxSortOrder();

    @Select("SELECT department AS `name`, COUNT(*) AS `value` FROM employee WHERE department IS NOT NULL AND department != '' GROUP BY department")
    List<Map<String, Object>> countByDepartment();

    @Select("SELECT position AS `name`, COUNT(*) AS `value` FROM employee WHERE position IS NOT NULL AND position != '' GROUP BY position")
    List<Map<String, Object>> countByPosition();

    @Select("SELECT `rank` AS `name`, COUNT(*) AS `value` FROM employee WHERE `rank` IS NOT NULL AND `rank` != '' GROUP BY `rank`")
    List<Map<String, Object>> countByRank();

    @Select("SELECT gender AS `name`, COUNT(*) AS `value` FROM employee GROUP BY gender")
    List<Map<String, Object>> countByGender();

    @Select("SELECT DATE_FORMAT(hire_date, '%Y-%m') AS `month`, COUNT(*) AS `count` FROM employee WHERE hire_date IS NOT NULL AND hire_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) GROUP BY `month` ORDER BY `month`")
    List<Map<String, Object>> countByHireMonth();

    @Select("SELECT CASE WHEN age < 25 THEN '<25' WHEN age < 35 THEN '25-34' WHEN age < 45 THEN '35-44' ELSE '45+' END AS `range`, COUNT(*) AS `count` FROM employee WHERE age IS NOT NULL GROUP BY `range` ORDER BY `range`")
    List<Map<String, Object>> countByAgeRange();
}
