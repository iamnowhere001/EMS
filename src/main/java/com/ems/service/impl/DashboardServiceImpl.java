package com.ems.service.impl;

import com.ems.entity.Employee;
import com.ems.mapper.DashboardMapper;
import com.ems.service.DashboardService;
import com.ems.service.DictionaryService;
import com.ems.vo.DashboardVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;
    private final DictionaryService dictionaryService;

    public DashboardServiceImpl(DashboardMapper dashboardMapper, DictionaryService dictionaryService) {
        this.dashboardMapper = dashboardMapper;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public DashboardVO loadDashboard() {
        DashboardVO vo = new DashboardVO();

        // ========== KPI ==========
        DashboardVO.Kpi kpi = new DashboardVO.Kpi();
        kpi.setTotalCount(dashboardMapper.countTotal());
        kpi.setActiveCount(dashboardMapper.countActive());
        kpi.setLeftCount(dashboardMapper.countLeft());
        kpi.setNewHireThisMonth(dashboardMapper.countNewHireThisMonth());
        kpi.setLeftThisMonth(0);
        kpi.setDepartmentCount(dashboardMapper.countDepartment());
        kpi.setTotalSalary(dashboardMapper.sumTotalSalary());
        kpi.setAvgSalary(dashboardMapper.avgSalary());
        
        BigDecimal avgTenureDays = dashboardMapper.avgTenureDays();
        if (avgTenureDays != null && avgTenureDays.compareTo(BigDecimal.ZERO) > 0) {
            kpi.setAvgTenureYears(Math.round(avgTenureDays.doubleValue() / 365.0 * 10.0) / 10.0);
        } else {
            kpi.setAvgTenureYears(0.0);
        }
        vo.setKpi(kpi);

        // ========== 部门分布 ==========
        Map<String, String> deptNameMap = dictionaryService.listByType("department").stream()
                .collect(Collectors.toMap(d -> d.getCode(), d -> d.getName(), (a, b) -> a));
        List<DashboardVO.NameValueItem> deptDist = dashboardMapper.countByDepartment().stream()
                .map(item -> {
                    DashboardVO.NameValueItem newItem = new DashboardVO.NameValueItem();
                    newItem.setName(deptNameMap.getOrDefault(item.getName(), item.getName()));
                    newItem.setValue(item.getValue());
                    return newItem;
                })
                .collect(Collectors.toList());
        vo.setDepartmentDistribution(deptDist);

        // ========== 年龄分布 ==========
        Map<String, Long> ageMap = dashboardMapper.countByAge().stream()
                .collect(Collectors.toMap(DashboardVO.NameValueItem::getName, DashboardVO.NameValueItem::getValue));
        String[] ageBuckets = {"20-25", "26-30", "31-35", "36-40", "41-50", "50+"};
        List<DashboardVO.NameValueItem> ageDist = new ArrayList<>();
        for (String bucket : ageBuckets) {
            ageDist.add(new DashboardVO.NameValueItem(bucket, ageMap.getOrDefault(bucket, 0L)));
        }
        vo.setAgeDistribution(ageDist);

        // ========== 工龄分布 ==========
        Map<String, Long> tenureMap = dashboardMapper.countByTenure().stream()
                .collect(Collectors.toMap(DashboardVO.NameValueItem::getName, DashboardVO.NameValueItem::getValue));
        String[] tenureBuckets = {"<1年", "1-3年", "3-5年", "5-10年", "10年+"};
        List<DashboardVO.NameValueItem> tenureDist = new ArrayList<>();
        for (String bucket : tenureBuckets) {
            tenureDist.add(new DashboardVO.NameValueItem(bucket, tenureMap.getOrDefault(bucket, 0L)));
        }
        vo.setTenureDistribution(tenureDist);

        // ========== 学历分布 ==========
        Map<String, Long> eduMap = dashboardMapper.countByHighestEducation().stream()
                .collect(Collectors.toMap(DashboardVO.NameValueItem::getName, DashboardVO.NameValueItem::getValue));
        long noEdu = dashboardMapper.countNoEducation();
        if (noEdu > 0) {
            eduMap.put("未填写", noEdu);
        }
        List<DashboardVO.NameValueItem> eduDist = new ArrayList<>();
        for (String k : List.of("博士", "硕士", "本科", "大专", "高中", "未填写")) {
            eduDist.add(new DashboardVO.NameValueItem(k, eduMap.getOrDefault(k, 0L)));
        }
        vo.setEducationDistribution(eduDist);

        // ========== 薪资分布 ==========
        Map<String, Long> salaryMap = dashboardMapper.countBySalary().stream()
                .collect(Collectors.toMap(DashboardVO.NameValueItem::getName, DashboardVO.NameValueItem::getValue));
        String[] salaryBuckets = {"<5k", "5-10k", "10-15k", "15-20k", "20-30k", "30k+"};
        List<DashboardVO.NameValueItem> salDist = new ArrayList<>();
        for (String bucket : salaryBuckets) {
            salDist.add(new DashboardVO.NameValueItem(bucket, salaryMap.getOrDefault(bucket, 0L)));
        }
        vo.setSalaryDistribution(salDist);

        // ========== 性别比例 ==========
        Map<String, Long> genderMap = dashboardMapper.countByGender().stream()
                .collect(Collectors.toMap(DashboardVO.NameValueItem::getName, DashboardVO.NameValueItem::getValue));
        List<DashboardVO.NameValueItem> genderDist = new ArrayList<>();
        genderDist.add(new DashboardVO.NameValueItem("男", genderMap.getOrDefault("男", 0L)));
        genderDist.add(new DashboardVO.NameValueItem("女", genderMap.getOrDefault("女", 0L)));
        vo.setGenderRatio(genderDist);

        // ========== 月度入离职趋势 ==========
        Map<String, Integer> monthOnboardMap = dashboardMapper.countMonthlyOnboard().stream()
                .collect(Collectors.toMap(DashboardVO.MonthlyTrendItem::getMonth, DashboardVO.MonthlyTrendItem::getOnboardCount));
        List<DashboardVO.MonthlyTrendItem> trend = new ArrayList<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth thisMonth = YearMonth.from(LocalDate.now());
        YearMonth start = thisMonth.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            YearMonth ym = start.plusMonths(i);
            String monthStr = ym.format(monthFmt);
            DashboardVO.MonthlyTrendItem item = new DashboardVO.MonthlyTrendItem();
            item.setMonth(monthStr);
            item.setOnboardCount(monthOnboardMap.getOrDefault(monthStr, 0));
            item.setLeftCount(0);
            trend.add(item);
        }
        vo.setMonthlyTrend(trend);

        // ========== 部门薪资统计 ==========
        List<DashboardVO.DepartmentSalaryItem> deptSalary = dashboardMapper.getDepartmentSalaryStats().stream()
                .map(item -> {
                    DashboardVO.DepartmentSalaryItem newItem = new DashboardVO.DepartmentSalaryItem();
                    newItem.setDepartment(deptNameMap.getOrDefault(item.getDepartment(), item.getDepartment()));
                    newItem.setHeadcount(item.getHeadcount());
                    newItem.setTotalSalary(item.getTotalSalary());
                    newItem.setAvgSalary(item.getAvgSalary());
                    return newItem;
                })
                .collect(Collectors.toList());
        vo.setDepartmentSalary(deptSalary);

        // ========== 职级分布 ==========
        Map<String, String> rankNameMap = dictionaryService.listByType("rank").stream()
                .collect(Collectors.toMap(d -> d.getCode(), d -> d.getName(), (a, b) -> a));
        List<DashboardVO.RankDistributionItem> rankDist = dashboardMapper.countByRank().stream()
                .map(item -> {
                    DashboardVO.RankDistributionItem newItem = new DashboardVO.RankDistributionItem();
                    newItem.setRank(rankNameMap.getOrDefault(item.getRank(), item.getRank()));
                    newItem.setCount(item.getCount());
                    return newItem;
                })
                .collect(Collectors.toList());
        vo.setRankDistribution(rankDist);

        return vo;
    }
}