package com.apeic.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.apeic.bill.model.dto.bill.BillQueryRequest;
import com.apeic.bill.model.entity.Bill;
import com.apeic.bill.model.vo.BillVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 账单本服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface BillService extends IService<Bill> {

    /**
     * 校验数据
     *
     * @param bill
     * @param add 对创建的数据进行校验
     */
    void validBill(Bill bill, boolean add);

    /**
     * 获取查询条件
     *
     * @param billQueryRequest
     * @return
     */
    QueryWrapper<Bill> getQueryWrapper(BillQueryRequest billQueryRequest);
    
    /**
     * 获取账单本封装
     *
     * @param bill
     * @param request
     * @return
     */
    BillVO getBillVO(Bill bill, HttpServletRequest request);

    /**
     * 分页获取账单本封装
     *
     * @param billPage
     * @param request
     * @return
     */
    Page<BillVO> getBillVOPage(Page<Bill> billPage, HttpServletRequest request);
}
