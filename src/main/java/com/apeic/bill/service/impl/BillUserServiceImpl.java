package com.apeic.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.constant.CommonConstant;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.mapper.BillUserMapper;
import com.apeic.bill.model.dto.billuser.BillUserQueryRequest;
import com.apeic.bill.model.entity.BillUser;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillUserVO;
import com.apeic.bill.model.vo.UserVO;
import com.apeic.bill.service.BillUserService;
import com.apeic.bill.service.UserService;
import com.apeic.bill.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账单本用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class BillUserServiceImpl extends ServiceImpl<BillUserMapper, BillUser> implements BillUserService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param billUser
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBillUser(BillUser billUser, boolean add) {
        ThrowUtils.throwIf(billUser == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值

    }

    /**
     * 获取查询条件
     *
     * @param billUserQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<BillUser> getQueryWrapper(BillUserQueryRequest billUserQueryRequest) {
        QueryWrapper<BillUser> queryWrapper = new QueryWrapper<>();
        if (billUserQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = billUserQueryRequest.getId();

        String sortField = billUserQueryRequest.getSortField();
        String sortOrder = billUserQueryRequest.getSortOrder();
        Long userId = billUserQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 模糊查询

        // JSON 数组查询

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
     * 获取账单本用户封装
     *
     * @param billUser
     * @param request
     * @return
     */
    @Override
    public BillUserVO getBillUserVO(BillUser billUser, HttpServletRequest request) {
        // 对象转封装类
        BillUserVO billUserVO = BillUserVO.objToVo(billUser);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        return billUserVO;
    }

    /**
     * 分页获取账单本用户封装
     *
     * @param billUserPage
     * @param request
     * @return
     */
    @Override
    public Page<BillUserVO> getBillUserVOPage(Page<BillUser> billUserPage, HttpServletRequest request) {
        List<BillUser> billUserList = billUserPage.getRecords();
        Page<BillUserVO> billUserVOPage = new Page<>(billUserPage.getCurrent(), billUserPage.getSize(), billUserPage.getTotal());
        if (CollUtil.isEmpty(billUserList)) {
            return billUserVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BillUserVO> billUserVOList = billUserList.stream().map(billUser -> {
            return BillUserVO.objToVo(billUser);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        billUserVOPage.setRecords(billUserVOList);
        return billUserVOPage;
    }

}
