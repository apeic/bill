package com.apeic.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.constant.CommonConstant;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.mapper.BillDetailMapper;
import com.apeic.bill.model.dto.billdetail.BillDetailQueryRequest;
import com.apeic.bill.model.entity.BillDetail;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillDetailVO;
import com.apeic.bill.model.vo.UserVO;
import com.apeic.bill.service.BillDetailService;
import com.apeic.bill.service.UserService;
import com.apeic.bill.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账单明细服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class BillDetailServiceImpl extends ServiceImpl<BillDetailMapper, BillDetail> implements BillDetailService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param billDetail
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBillDetail(BillDetail billDetail, boolean add) {
        ThrowUtils.throwIf(billDetail == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long id = billDetail.getId();
        Date recordDate = billDetail.getRecordDate();
        BigDecimal amount = billDetail.getAmount();
        String description = billDetail.getDescription();
        Long billId = billDetail.getBillId();
        Long userId = billDetail.getUserId();
        Long categoryId = billDetail.getCategoryId();

        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(ObjectUtils.isEmpty(recordDate), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(amount), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(billId), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(categoryId), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(description)) {
            ThrowUtils.throwIf(description.length() > 30, ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param billDetailQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<BillDetail> getQueryWrapper(BillDetailQueryRequest billDetailQueryRequest) {
        QueryWrapper<BillDetail> queryWrapper = new QueryWrapper<>();
        if (billDetailQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = billDetailQueryRequest.getId();
        Date recordDate = billDetailQueryRequest.getRecordDate();
        BigDecimal amount = billDetailQueryRequest.getAmount();
        String description = billDetailQueryRequest.getDescription();
        Long billId = billDetailQueryRequest.getBillId();
        Long userId = billDetailQueryRequest.getUserId();
        Long categoryId = billDetailQueryRequest.getCategoryId();
        String categoryName = billDetailQueryRequest.getCategoryName();

        String sortField = billDetailQueryRequest.getSortField();
        String sortOrder = billDetailQueryRequest.getSortOrder();
        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // JSON 数组查询

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(recordDate), "recordDate", recordDate);
        queryWrapper.eq(ObjectUtils.isNotEmpty(billId), "billId", billId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(categoryId), "categoryId", categoryId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取账单明细封装
     *
     * @param billDetail
     * @param request
     * @return
     */
    @Override
    public BillDetailVO getBillDetailVO(BillDetail billDetail, HttpServletRequest request) {
        // 对象转封装类
        BillDetailVO billDetailVO = BillDetailVO.objToVo(billDetail);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        return billDetailVO;
    }

    /**
     * 分页获取账单明细封装
     *
     * @param billDetailPage
     * @param request
     * @return
     */
    @Override
    public Page<BillDetailVO> getBillDetailVOPage(Page<BillDetail> billDetailPage, HttpServletRequest request) {
        List<BillDetail> billDetailList = billDetailPage.getRecords();
        Page<BillDetailVO> billDetailVOPage = new Page<>(billDetailPage.getCurrent(), billDetailPage.getSize(), billDetailPage.getTotal());
        if (CollUtil.isEmpty(billDetailList)) {
            return billDetailVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BillDetailVO> billDetailVOList = billDetailList.stream().map(billDetail -> {
            return BillDetailVO.objToVo(billDetail);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        billDetailVOPage.setRecords(billDetailVOList);
        return billDetailVOPage;
    }

}
