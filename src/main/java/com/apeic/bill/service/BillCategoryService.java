package com.apeic.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.apeic.bill.model.dto.billcategory.BillCategoryQueryRequest;
import com.apeic.bill.model.entity.BillCategory;
import com.apeic.bill.model.vo.BillCategoryVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 账单明细类别服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface BillCategoryService extends IService<BillCategory> {

    /**
     * 校验数据
     *
     * @param billCategory
     * @param add 对创建的数据进行校验
     */
    void validBillCategory(BillCategory billCategory, boolean add);

    /**
     * 获取查询条件
     *
     * @param billCategoryQueryRequest
     * @return
     */
    QueryWrapper<BillCategory> getQueryWrapper(BillCategoryQueryRequest billCategoryQueryRequest);
    
    /**
     * 获取账单明细类别封装
     *
     * @param billCategory
     * @param request
     * @return
     */
    BillCategoryVO getBillCategoryVO(BillCategory billCategory, HttpServletRequest request);

    /**
     * 分页获取账单明细类别封装
     *
     * @param billCategoryPage
     * @param request
     * @return
     */
    Page<BillCategoryVO> getBillCategoryVOPage(Page<BillCategory> billCategoryPage, HttpServletRequest request);
}
