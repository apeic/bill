package com.apeic.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.constant.CommonConstant;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.mapper.BillCategoryMapper;
import com.apeic.bill.model.dto.billcategory.BillCategoryQueryRequest;
import com.apeic.bill.model.entity.BillCategory;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillCategoryVO;
import com.apeic.bill.model.vo.UserVO;
import com.apeic.bill.service.BillCategoryService;
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
 * 账单明细类别服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class BillCategoryServiceImpl extends ServiceImpl<BillCategoryMapper, BillCategory> implements BillCategoryService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param billCategory
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validBillCategory(BillCategory billCategory, boolean add) {
        ThrowUtils.throwIf(billCategory == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = billCategory.getName();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param billCategoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<BillCategory> getQueryWrapper(BillCategoryQueryRequest billCategoryQueryRequest) {
        QueryWrapper<BillCategory> queryWrapper = new QueryWrapper<>();
        if (billCategoryQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = billCategoryQueryRequest.getId();

        String sortField = billCategoryQueryRequest.getSortField();
        String sortOrder = billCategoryQueryRequest.getSortOrder();
        Long userId = billCategoryQueryRequest.getUserId();
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
     * 获取账单明细类别封装
     *
     * @param billCategory
     * @param request
     * @return
     */
    @Override
    public BillCategoryVO getBillCategoryVO(BillCategory billCategory, HttpServletRequest request) {
        // 对象转封装类
        BillCategoryVO billCategoryVO = BillCategoryVO.objToVo(billCategory);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选


        // endregion

        return billCategoryVO;
    }

    /**
     * 分页获取账单明细类别封装
     *
     * @param billCategoryPage
     * @param request
     * @return
     */
    @Override
    public Page<BillCategoryVO> getBillCategoryVOPage(Page<BillCategory> billCategoryPage, HttpServletRequest request) {
        List<BillCategory> billCategoryList = billCategoryPage.getRecords();
        Page<BillCategoryVO> billCategoryVOPage = new Page<>(billCategoryPage.getCurrent(), billCategoryPage.getSize(), billCategoryPage.getTotal());
        if (CollUtil.isEmpty(billCategoryList)) {
            return billCategoryVOPage;
        }
        // 对象列表 => 封装对象列表
        List<BillCategoryVO> billCategoryVOList = billCategoryList.stream().map(billCategory -> {
            return BillCategoryVO.objToVo(billCategory);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选

        // endregion

        billCategoryVOPage.setRecords(billCategoryVOList);
        return billCategoryVOPage;
    }

}
