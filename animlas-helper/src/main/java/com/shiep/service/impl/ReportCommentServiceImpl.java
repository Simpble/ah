package com.shiep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiep.entity.ReportComment;
import com.shiep.entity.User;
import com.shiep.mapper.IReportCommentMapper;
import com.shiep.mapper.IUserMapper;
import com.shiep.service.IReportCommentService;
import com.shiep.vo.ReportCommentVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportCommentServiceImpl implements IReportCommentService {
    @Resource
    private IReportCommentMapper reportCommentMapper;

    @Resource
    private IUserMapper userMapper;

    @Override
    /*保存报道的评论*/
    public Boolean saveReportComment(ReportComment reportComment) {
        if (reportComment == null) {
            return null;
        }
        return this.reportCommentMapper.insert(reportComment) > 0 ;
    }
    /*将ReportComment类转换为ReportCommentVo类*/
    public ReportCommentVo reportCommentConvertToVo(ReportComment reportComment){
        ReportCommentVo vo = new ReportCommentVo();
        vo.setReportComment(reportComment);
        QueryWrapper<User> qwUser = new QueryWrapper<>();
        qwUser.lambda().eq(User::getDeleteStatus, 0).eq(User::getId, reportComment.getCommentUserId());
        vo.setUser(this.userMapper.selectOne(qwUser));
        return vo;
    }
    @Override
    /*根据报道查询当前报道的所有评论*/
    public List<ReportCommentVo> queryReportCommentByReportId(Long reportId) {
        List<ReportCommentVo> reportCommentVos = new ArrayList<>();
        if (reportId == null) {
            return null;
        }
        QueryWrapper<ReportComment> qw = new QueryWrapper<>();
        qw.lambda().eq(ReportComment::getDeleteStatus, 0).eq(ReportComment::getReportId, reportId);
        List<ReportComment> reportComments = this.reportCommentMapper.selectList(qw);
        if (CollectionUtils.isEmpty(reportComments)) {
            return null;
        }
        for (ReportComment reportComment : reportComments) {
            reportCommentVos.add(reportCommentConvertToVo(reportComment));
        }
        return reportCommentVos;
    }
    /*根据评论id，查询评论的用户信息以及评论信息*/
    @Override
    public ReportCommentVo ReportCommentByCommentId(Long commentId) {
        if (commentId == null) {
            return null;
        }
        QueryWrapper<ReportComment> qw = new QueryWrapper<>();
        qw.lambda().eq(ReportComment::getDeleteStatus, 0).eq(ReportComment::getId, commentId);
        ReportComment reportComment = this.reportCommentMapper.selectOne(qw);
        return reportCommentConvertToVo(reportComment);
    }

}
