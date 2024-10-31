package com.apeic.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.constant.CommonConstant;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.mapper.BillMapper;
import com.apeic.bill.model.dto.bill.BillQueryRequest;
import com.apeic.bill.model.entity.Bill;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillVO;
import com.apeic.bill.model.vo.UserVO;
import com.apeic.bill.service.BillService;
import com.apeic.bill.service.UserService;
import com.apeic.bill.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账单本服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param bill
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBill(Bill bill, boolean add) {
        ThrowUtils.throwIf(bill == null, ErrorCode.PARAMS_ERROR);
        Long id = bill.getId();
        String name = bill.getName();
        String description = bill.getDescription();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(name)) {
            ThrowUtils.throwIf(name.length() > 20, ErrorCode.PARAMS_ERROR, "账单名称过长");
        }
        if (StringUtils.isNotBlank(name)) {
            ThrowUtils.throwIf(description.length() > 40, ErrorCode.PARAMS_ERROR, "账单描述过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param billQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Bill> getQueryWrapper(BillQueryRequest billQueryRequest) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        if (billQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = billQueryRequest.getId();
        String name = billQueryRequest.getName();
        String description = billQueryRequest.getDescription();
        Long userId = billQueryRequest.getUserId();
        String sortField = billQueryRequest.getSortField();
        String sortOrder = billQueryRequest.getSortOrder();
        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取账单本封装
     *
     * @param bill
     * @param request
     * @return
     */
    @Override
    public BillVO getBillVO(Bill bill, HttpServletRequest request) {
        // 对象转封装类
        BillVO billVO = BillVO.objToVo(bill);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        return billVO;
    }

    /**
     * 分页获取账单本封装
     *
     * @param billPage
     * @param request
     * @return
     */
    @Override
    public Page<BillVO> getBillVOPage(Page<Bill> billPage, HttpServletRequest request) {
        List<Bill> billList = billPage.getRecords();
        Page<BillVO> billVOPage = new Page<>(billPage.getCurrent(), billPage.getSize(), billPage.getTotal());
        if (CollUtil.isEmpty(billList)) {
            return billVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BillVO> billVOList = billList.stream().map(bill -> {
            return BillVO.objToVo(bill);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        billVOPage.setRecords(billVOList);
        return billVOPage;
    }

}
