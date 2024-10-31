package com.apeic.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.apeic.bill.model.dto.billuser.BillUserQueryRequest;
import com.apeic.bill.model.entity.BillUser;
import com.apeic.bill.model.vo.BillUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 账单本用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface BillUserService extends IService<BillUser> {

    /**
     * 校验数据
     *
     * @param billUser
     * @param add 对创建的数据进行校验
     */
    void validBillUser(BillUser billUser, boolean add);

    /**
     * 获取查询条件
     *
     * @param billUserQueryRequest
     * @return
     */
    QueryWrapper<BillUser> getQueryWrapper(BillUserQueryRequest billUserQueryRequest);
    
    /**
     * 获取账单本用户封装
     *
     * @param billUser
     * @param request
     * @return
     */
    BillUserVO getBillUserVO(BillUser billUser, HttpServletRequest request);

    /**
     * 分页获取账单本用户封装
     *
     * @param billUserPage
     * @param request
     * @return
     */
    Page<BillUserVO> getBillUserVOPage(Page<BillUser> billUserPage, HttpServletRequest request);
}
