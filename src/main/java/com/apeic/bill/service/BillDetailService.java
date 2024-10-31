package com.apeic.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.apeic.bill.model.dto.billdetail.BillDetailQueryRequest;
import com.apeic.bill.model.entity.BillDetail;
import com.apeic.bill.model.vo.BillDetailVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 账单明细服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface BillDetailService extends IService<BillDetail> {

    /**
     * 校验数据
     *
     * @param billDetail
     * @param add 对创建的数据进行校验
     */
    void validBillDetail(BillDetail billDetail, boolean add);

    /**
     * 获取查询条件
     *
     * @param billDetailQueryRequest
     * @return
     */
    QueryWrapper<BillDetail> getQueryWrapper(BillDetailQueryRequest billDetailQueryRequest);
    
    /**
     * 获取账单明细封装
     *
     * @param billDetail
     * @param request
     * @return
     */
    BillDetailVO getBillDetailVO(BillDetail billDetail, HttpServletRequest request);

    /**
     * 分页获取账单明细封装
     *
     * @param billDetailPage
     * @param request
     * @return
     */
    Page<BillDetailVO> getBillDetailVOPage(Page<BillDetail> billDetailPage, HttpServletRequest request);
}
