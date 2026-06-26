package com.ems.service.impl;

import com.ems.entity.Employee;
import com.ems.entity.EmployeeEducation;
import com.ems.mapper.EmployeeEducationMapper;
import com.ems.service.DashboardService;
import com.ems.service.DictionaryService;
import com.ems.vo.DashboardVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final com.ems.mapper.EmployeeMapper employeeMapper;
    private final EmployeeEducationMapper educationMapper;
    private final DictionaryService dictionaryService;

    public DashboardServiceImpl(com.ems.mapper.EmployeeMapper employeeMapper,
                                 EmployeeEducationMapper educationMapper,
                                 DictionaryService dictionaryService) {
        this.employeeMapper = employeeMapper;
        this.educationMapper = educationMapper;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public DashboardVO loadDashboard() {
        DashboardVO vo = new DashboardVO();
        List<Employee> all = employeeMapper.selectList(null);
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);

        // ========== KPI ==========
        DashboardVO.Kpi kpi = new DashboardVO.Kpi();
        kpi.setTotalCount(all.size());
        long active = all.stream().filter(e -> e.getStatus() != null && e.getStatus() == 1).count();
        kpi.setActiveCount((int) active);
        kpi.setLeftCount((int) (all.size() - active));
        kpi.setNewHireThisMonth((int) all.stream()
                .filter(e -> e.getHireDate() != null && YearMonth.from(e.getHireDate()).equals(thisMonth))
                .count());
        kpi.setLeftThisMonth(0); // 暂无离职日期字段，暂记 0
        long deptCount = all.stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().isEmpty())
                .map(Employee::getDepartment)
                .distinct()
                .count();
        kpi.setDepartmentCount((int) deptCount);

        BigDecimal totalSalary = all.stream()
                .filter(e -> e.getSalary() != null && e.getStatus() != null && e.getStatus() == 1)
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        kpi.setTotalSalary(totalSalary);
        if (active > 0) {
            kpi.setAvgSalary(totalSalary.divide(BigDecimal.valueOf(active), 2, RoundingMode.HALF_UP));
        } else {
            kpi.setAvgSalary(BigDecimal.ZERO);
        }
        // 平均工龄（在职）
        double avgTenure = all.stream()
                .filter(e -> e.getHireDate() != null && e.getStatus() != null && e.getStatus() == 1)
                .mapToLong(e -> ChronoUnit.DAYS.between(e.getHireDate(), today))
                .average().orElse(0);
        kpi.setAvgTenureYears(Math.round(avgTenure / 365.0 * 10.0) / 10.0);
        vo.setKpi(kpi);

        // ========== 部门分布 ==========
        Map<String, Long> deptGrouping = all.stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().isEmpty())
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        Map<String, String> deptNameMap = dictionaryService.listByType("department").stream()
                .collect(Collectors.toMap(d -> d.getCode(), d -> d.getName(), (a, b) -> a));
        List<DashboardVO.NameValueItem> deptDist = new ArrayList<>();
        deptGrouping.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(e -> deptDist.add(new DashboardVO.NameValueItem(
                        deptNameMap.getOrDefault(e.getKey(), e.getKey()), e.getValue())));
        vo.setDepartmentDistribution(deptDist);

        // ========== 年龄分布 ==========
        String[] ageBuckets = {"20-25", "26-30", "31-35", "36-40", "41-50", "50+"};
        int[] ageCounts = new int[ageBuckets.length];
        for (Employee e : all) {
            if (e.getAge() == null) continue;
            int age = e.getAge();
            int idx;
            if (age <= 25) idx = 0;
            else if (age <= 30) idx = 1;
            else if (age <= 35) idx = 2;
            else if (age <= 40) idx = 3;
            else if (age <= 50) idx = 4;
            else idx = 5;
            ageCounts[idx]++;
        }
        List<DashboardVO.NameValueItem> ageDist = new ArrayList<>();
        for (int i = 0; i < ageBuckets.length; i++) {
            ageDist.add(new DashboardVO.NameValueItem(ageBuckets[i], ageCounts[i]));
        }
        vo.setAgeDistribution(ageDist);

        // ========== 工龄分布（在职） ==========
        String[] tenureBuckets = {"<1年", "1-3年", "3-5年", "5-10年", "10年+"};
        int[] tenureCounts = new int[tenureBuckets.length];
        for (Employee e : all) {
            if (e.getHireDate() == null) continue;
            long days = ChronoUnit.DAYS.between(e.getHireDate(), today);
            double years = days / 365.0;
            int idx;
            if (years < 1) idx = 0;
            else if (years < 3) idx = 1;
            else if (years < 5) idx = 2;
            else if (years < 10) idx = 3;
            else idx = 4;
            tenureCounts[idx]++;
        }
        List<DashboardVO.NameValueItem> tenureDist = new ArrayList<>();
        for (int i = 0; i < tenureBuckets.length; i++) {
            tenureDist.add(new DashboardVO.NameValueItem(tenureBuckets[i], tenureCounts[i]));
        }
        vo.setTenureDistribution(tenureDist);

        // ========== 学历分布 ==========
        // 通过子表聚合（取每个员工的最高学历）
        List<EmployeeEducation> allEdu = educationMapper.selectList(null);
        Map<Long, String> empMaxEducation = new HashMap<>();
        // 学历等级排序
        Map<String, Integer> eduRank = Map.of("博士", 5, "硕士", 4, "本科", 3, "大专", 2, "高中", 1, "其他", 0);
        for (EmployeeEducation edu : allEdu) {
            String eduLevel = edu.getEducation() == null ? "" : edu.getEducation();
            Integer rank = eduRank.get(eduLevel);
            if (rank == null) continue;
            String prev = empMaxEducation.get(edu.getEmployeeId());
            Integer prevRank = prev == null ? -1 : eduRank.getOrDefault(prev, -1);
            if (rank > prevRank) {
                empMaxEducation.put(edu.getEmployeeId(), eduLevel);
            }
        }
        Map<String, Long> eduGrouping = empMaxEducation.values().stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        // 补全：没有教育经历的算"未知"
        long noEdu = all.stream().filter(e -> !empMaxEducation.containsKey(e.getId())).count();
        if (noEdu > 0) eduGrouping.put("未填写", noEdu);
        List<DashboardVO.NameValueItem> eduDist = new ArrayList<>();
        for (String k : List.of("博士", "硕士", "本科", "大专", "高中", "未填写")) {
            eduDist.add(new DashboardVO.NameValueItem(k, eduGrouping.getOrDefault(k, 0L)));
        }
        vo.setEducationDistribution(eduDist);

        // ========== 薪资分布 ==========
        String[] salaryBuckets = {"<5k", "5-10k", "10-15k", "15-20k", "20-30k", "30k+"};
        int[] salaryCounts = new int[salaryBuckets.length];
        for (Employee e : all) {
            if (e.getSalary() == null) continue;
            double sal = e.getSalary().doubleValue();
            int idx;
            if (sal < 5000) idx = 0;
            else if (sal < 10000) idx = 1;
            else if (sal < 15000) idx = 2;
            else if (sal < 20000) idx = 3;
            else if (sal < 30000) idx = 4;
            else idx = 5;
            salaryCounts[idx]++;
        }
        List<DashboardVO.NameValueItem> salDist = new ArrayList<>();
        for (int i = 0; i < salaryBuckets.length; i++) {
            salDist.add(new DashboardVO.NameValueItem(salaryBuckets[i], salaryCounts[i]));
        }
        vo.setSalaryDistribution(salDist);

        // ========== 性别比例 ==========
        Map<Integer, Long> genderGroup = all.stream()
                .filter(e -> e.getGender() != null)
                .collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
        List<DashboardVO.NameValueItem> genderDist = new ArrayList<>();
        genderDist.add(new DashboardVO.NameValueItem("男", genderGroup.getOrDefault(1, 0L)));
        genderDist.add(new DashboardVO.NameValueItem("女", genderGroup.getOrDefault(0, 0L)));
        vo.setGenderRatio(genderDist);

        // ========== 月度入离职趋势（最近 12 个月，按入职日期） ==========
        List<DashboardVO.MonthlyTrendItem> trend = new ArrayList<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth start = thisMonth.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            YearMonth ym = start.plusMonths(i);
            DashboardVO.MonthlyTrendItem item = new DashboardVO.MonthlyTrendItem();
            item.setMonth(ym.format(monthFmt));
            int onboard = (int) all.stream()
                    .filter(e -> e.getHireDate() != null && YearMonth.from(e.getHireDate()).equals(ym))
                    .count();
            item.setOnboardCount(onboard);
            item.setLeftCount(0);
            trend.add(item);
        }
        vo.setMonthlyTrend(trend);

        // ========== 部门薪资统计 ==========
        Map<String, List<Employee>> deptEmployees = all.stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().isEmpty()
                        && e.getStatus() != null && e.getStatus() == 1)
                .collect(Collectors.groupingBy(Employee::getDepartment));
        List<DashboardVO.DepartmentSalaryItem> deptSalary = new ArrayList<>();
        for (Map.Entry<String, List<Employee>> en : deptEmployees.entrySet()) {
            DashboardVO.DepartmentSalaryItem item = new DashboardVO.DepartmentSalaryItem();
            item.setDepartment(deptNameMap.getOrDefault(en.getKey(), en.getKey()));
            item.setHeadcount(en.getValue().size());
            BigDecimal sum = en.getValue().stream()
                    .map(Employee::getSalary)
                    .filter(s -> s != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setTotalSalary(sum);
            item.setAvgSalary(en.getValue().isEmpty() ? BigDecimal.ZERO
                    : sum.divide(BigDecimal.valueOf(en.getValue().size()), 2, RoundingMode.HALF_UP));
            deptSalary.add(item);
        }
        deptSalary.sort((a, b) -> b.getTotalSalary().compareTo(a.getTotalSalary()));
        vo.setDepartmentSalary(deptSalary);

        // ========== 职级分布 ==========
        Map<String, String> rankNameMap = dictionaryService.listByType("rank").stream()
                .collect(Collectors.toMap(d -> d.getCode(), d -> d.getName(), (a, b) -> a));
        Map<String, Long> rankGroup = all.stream()
                .filter(e -> e.getRank() != null && !e.getRank().isEmpty())
                .collect(Collectors.groupingBy(Employee::getRank, Collectors.counting()));
        List<DashboardVO.RankDistributionItem> rankDist = new ArrayList<>();
        rankGroup.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(e -> {
                    DashboardVO.RankDistributionItem item = new DashboardVO.RankDistributionItem();
                    item.setRank(rankNameMap.getOrDefault(e.getKey(), e.getKey()));
                    item.setCount(e.getValue().intValue());
                    rankDist.add(item);
                });
        vo.setRankDistribution(rankDist);

        return vo;
    }
}
