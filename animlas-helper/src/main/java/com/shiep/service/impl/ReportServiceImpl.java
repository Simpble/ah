package com.shiep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.jdbc.CacheAdapter;
import com.shiep.entity.Report;
import com.shiep.entity.ReportType;
import com.shiep.entity.User;
import com.shiep.mapper.ILocationDao;
import com.shiep.mapper.IReportMapper;
import com.shiep.mapper.IReportTypeMapper;
import com.shiep.mapper.IUserMapper;
import com.shiep.service.IReportService;
import com.shiep.service.IUserService;
import com.shiep.vo.AnimalSearchVo;
import com.shiep.vo.Location;
import com.shiep.vo.ReportVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements IReportService {
    @Resource
    private IReportTypeMapper reportTypeMapper;

    @Resource
    private IReportMapper reportMapper;

    @Resource
    private IUserMapper userMapper;

    @Resource
    private ILocationDao locationDao;

    @Override
    /*查询所有的report类型*/
    public List<ReportType> queryAllReportType() {
        List<ReportType> reportTypes = this.reportTypeMapper.selectList(null);
        if (CollectionUtils.isEmpty(reportTypes)) {
            return null;
        }
        return reportTypes;
    }

    @Override
    /*保存报道*/
    public boolean saveReport(Report report) {
        if (report == null) {
            return false;
        }
        return  this.reportMapper.insert(report) > 0 ;
    }

    /*该方法提供的作用为根据时间范围，城市，报道的类型，进行查询。如一个月内在上海发布的日常经验的报道*/
    public List<ReportVo> queryReportByTypeOrTimeInPage(Integer typeId, Integer timeType, Long cityId) {
        List<Report> reports = null;
        if (timeType == null){
            reports = reportMapper.queryReport(cityId, typeId);
        }else{
            switch (timeType){
                case 1:
                    reports = reportMapper.queryReportInDay(cityId,typeId);
                    break;
                case 2:
                    reports = reportMapper.queryReportInWeek(cityId,typeId);
                    break;
                case 3:
                    reports = reportMapper.queryReportInMonth(cityId, typeId);
                    break;
                default:
                    break;
            }
        }
        if(reports == null || CollectionUtils.isEmpty(reports)){
            return null;
        }
        return buildReportVo(reports);
    }

    public ReportVo reportConvertToVo(Report report){
        ReportVo vo = new ReportVo();
        vo.setReport(report);
        QueryWrapper<ReportType> qwType = new QueryWrapper<>();
        qwType.lambda().eq(ReportType::getId, report.getTypeId());
        vo.setReportType(this.reportTypeMapper.selectOne(qwType));
        QueryWrapper<User> qwUser = new QueryWrapper<>();
        qwUser.lambda().eq(User::getId, report.getUserId());
        vo.setUser(this.userMapper.selectOne(qwUser));
        Location location = this.locationDao.getLocationByCityId(report.getCityId());
        vo.setLocation(location);
        return vo;
    }

    @Override
    public ReportVo queryReportById(Long id) {
        if (id == null) {
            return null;
        }
        QueryWrapper<Report> qwReport = new QueryWrapper<>();
        qwReport.lambda().eq(Report::getVerifyStatus, 1).eq(Report::getDeleteStatus, 0)
                    .eq(Report::getId, id);
        Report report = this.reportMapper.selectOne(qwReport);
        return reportConvertToVo(report);
    }

    public List<ReportVo> buildReportVo(List<Report> reports) {
        List<ReportVo> reportVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(reports)) {
            return null;
        }
        for(Report report : reports) {
            reportVos.add(reportConvertToVo(report));
        }
        return reportVos;
    }


//    @Override
    /*查询报道,typeId*/
//    public List<ReportVo> queryReportByTypeOrTimeInPage(Integer typeId, Integer timeType, Long cityId) {
//        List<Report> reports = null;
//        // 1、）什么都没选的时候
//       if (typeId == null && timeType == null && cityId == null) {
//            QueryWrapper<Report> qw = new QueryWrapper<>();
//             // 没有被删除，并且审核通过
//            qw.lambda().eq(Report::getDeleteStatus, 0).eq(Report::getVerifyStatus, 1);
//            reports = this.reportMapper.selectList(qw);
//            List<ReportVo> reportVos = this.bubildReportVo(reports);
//            if (CollectionUtils.isEmpty(reportVos)) {
//                return null;
//            }
//            return reportVos;
//        }
//       /*只选择了报道类型*/
//        if (typeId != null && timeType == null && cityId == null) {
//            QueryWrapper<Report> qw = new QueryWrapper<>();
//            // 没有被删除，并且审核通过
//            qw.lambda().eq(Report::getDeleteStatus, 0).eq(Report::getVerifyStatus, 1)
//                        .eq(Report::getTypeId, typeId);
//            reports = this.reportMapper.selectList(qw);
//            List<ReportVo> reportVos = this.bubildReportVo(reports);
//            if (CollectionUtils.isEmpty(reportVos)) {
//                return null;
//            }
//            return reportVos;
//        }
//        /*只选择了城市*/
//        if (cityId != null && typeId == null && timeType == null) {
//            // 首先过滤城市
//            QueryWrapper<Report> qw = new QueryWrapper<>();
//            qw.lambda().eq(Report::getDeleteStatus, 0).eq(Report::getVerifyStatus, 1)
//                    .eq(Report::getCityId, cityId);
//            reports = this.reportMapper.selectList(qw);
//            List<ReportVo> reportVos = this.bubildReportVo(reports);
//            if (CollectionUtils.isEmpty(reportVos)) {
//                return null;
//            }
//            return reportVos;
//        }
//        /*只选择了查询时间范围*/
//        if (typeId == null && timeType != null && cityId == null) {
//            switch (timeType) {
//                case 1 : {
//                    reports = this.reportMapper.queryReportInDay();
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 2 : {
//                    reports = this.reportMapper.queryReportInWeek();
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 3 : {
//                    reports = this.reportMapper.queryReportInMonth();
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//            }
//        }
//        /*----------------------------单个选择------------------------*/
//
//        /*选择了报道类型和时间范围*/
//        if (typeId != null && timeType != null && cityId == null) {
//            switch (timeType) {
//                case 1 : {
//                    reports = this.reportMapper.queryReportByTypeIdAndInDay(typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 2 : {
//                    reports = this.reportMapper.queryReportByTypeIdAndInWeek(typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 3 : {
//                    reports = this.reportMapper.queryReportByTypeIdAndInMonth(typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//            }
//        }
//        /*选择城市id和报道类型*/
//        if (cityId != null && typeId != null && timeType == null) {
//            QueryWrapper<Report> qw = new QueryWrapper<>();
//            qw.lambda().eq(Report::getDeleteStatus, 0).eq(Report::getVerifyStatus, 1)
//                    .eq(Report::getCityId, cityId).eq(Report::getTypeId, typeId);
//            reports = this.reportMapper.selectList(qw);
//            List<ReportVo> reportVos = this.bubildReportVo(reports);
//            if (CollectionUtils.isEmpty(reportVos)) {
//                return null;
//            }
//            return reportVos;
//        }
//        /*选择了城市和时间范围*/
//        if(cityId != null && typeId == null && timeType != null) {
//            switch (timeType) {
//                // 一天内的
//                case 1 : {
//                    reports = this.reportMapper.queryReportByCityIdAndInDay(cityId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 2 : {
//                    reports = this.reportMapper.queryReportByCityIdAndInWeek(cityId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 3 : {
//                    reports = this.reportMapper.queryReportByCityIdAndInMonth(cityId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//            }
//        }
//        /*----------------------------双选择------------------------*/
//
//        if (cityId != null && typeId != null && timeType != null) {
//            switch (timeType) {
//                case 1 : {
//                    reports = this.reportMapper.queryReportByCityIdAndTypeIdAndInDay(cityId, typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 2 : {
//                    reports = this.reportMapper.queryReportByCityIdAndTypeIdAndInWeek(cityId, typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//                case 3 : {
//                    reports = this.reportMapper.queryReportByCityIdAndTypeIdAndInMonth(cityId, typeId);
//                    List<ReportVo> reportVos = this.bubildReportVo(reports);
//                    if (CollectionUtils.isEmpty(reportVos)) {
//                        return null;
//                    }
//                    return reportVos;
//                }
//            }
//        }
//        /*----------------------------全选择------------------------*/
//        return null;
//    }
}
