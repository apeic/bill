package com.apeic.bill.controller;

import com.apeic.bill.annotation.AuthCheck;
import com.apeic.bill.common.BaseResponse;
import com.apeic.bill.common.DeleteRequest;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.common.ResultUtils;
import com.apeic.bill.constant.UserConstant;
import com.apeic.bill.exception.BusinessException;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.model.dto.billdetail.BillDetailAddRequest;
import com.apeic.bill.model.dto.billdetail.BillDetailEditRequest;
import com.apeic.bill.model.dto.billdetail.BillDetailQueryRequest;
import com.apeic.bill.model.dto.billdetail.BillDetailUpdateRequest;
import com.apeic.bill.model.entity.BillDetail;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillDetailVO;
import com.apeic.bill.service.BillDetailService;
import com.apeic.bill.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 账单明细接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/billDetail")
@Slf4j
public class BillDetailController {

    @Resource
    private BillDetailService billDetailService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建账单明细
     *
     * @param billDetailAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBillDetail(@RequestBody BillDetailAddRequest billDetailAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(billDetailAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        BillDetail billDetail = new BillDetail();
        BeanUtils.copyProperties(billDetailAddRequest, billDetail);
        // 数据校验
        billDetailService.validBillDetail(billDetail, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        billDetail.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = billDetailService.save(billDetail);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newBillDetailId = billDetail.getId();
        return ResultUtils.success(newBillDetailId);
    }

    /**
     * 删除账单明细
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBillDetail(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        BillDetail oldBillDetail = billDetailService.getById(id);
        ThrowUtils.throwIf(oldBillDetail == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldBillDetail.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billDetailService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新账单明细（仅管理员可用）
     *
     * @param billDetailUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateBillDetail(@RequestBody BillDetailUpdateRequest billDetailUpdateRequest) {
        if (billDetailUpdateRequest == null || billDetailUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillDetail billDetail = new BillDetail();
        BeanUtils.copyProperties(billDetailUpdateRequest, billDetail);
        // 数据校验
        billDetailService.validBillDetail(billDetail, false);
        // 判断是否存在
        long id = billDetailUpdateRequest.getId();
        BillDetail oldBillDetail = billDetailService.getById(id);
        ThrowUtils.throwIf(oldBillDetail == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = billDetailService.updateById(billDetail);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取账单明细（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BillDetailVO> getBillDetailVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        BillDetail billDetail = billDetailService.getById(id);
        ThrowUtils.throwIf(billDetail == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(billDetailService.getBillDetailVO(billDetail, request));
    }

    /**
     * 分页获取账单明细列表（仅管理员可用）
     *
     * @param billDetailQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<BillDetail>> listBillDetailByPage(@RequestBody BillDetailQueryRequest billDetailQueryRequest) {
        long current = billDetailQueryRequest.getCurrent();
        long size = billDetailQueryRequest.getPageSize();
        // 查询数据库
        Page<BillDetail> billDetailPage = billDetailService.page(new Page<>(current, size),
                billDetailService.getQueryWrapper(billDetailQueryRequest));
        return ResultUtils.success(billDetailPage);
    }

    /**
     * 分页获取账单明细列表（封装类）
     *
     * @param billDetailQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BillDetailVO>> listBillDetailVOByPage(@RequestBody BillDetailQueryRequest billDetailQueryRequest,
                                                               HttpServletRequest request) {
        long current = billDetailQueryRequest.getCurrent();
        long size = billDetailQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillDetail> billDetailPage = billDetailService.page(new Page<>(current, size),
                billDetailService.getQueryWrapper(billDetailQueryRequest));
        // 获取封装类
        return ResultUtils.success(billDetailService.getBillDetailVOPage(billDetailPage, request));
    }

    /**
     * 分页获取当前登录用户创建的账单明细列表
     *
     * @param billDetailQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BillDetailVO>> listMyBillDetailVOByPage(@RequestBody BillDetailQueryRequest billDetailQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(billDetailQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        billDetailQueryRequest.setUserId(loginUser.getId());
        long current = billDetailQueryRequest.getCurrent();
        long size = billDetailQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillDetail> billDetailPage = billDetailService.page(new Page<>(current, size),
                billDetailService.getQueryWrapper(billDetailQueryRequest));
        // 获取封装类
        return ResultUtils.success(billDetailService.getBillDetailVOPage(billDetailPage, request));
    }

    /**
     * 编辑账单明细（给用户使用）
     *
     * @param billDetailEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editBillDetail(@RequestBody BillDetailEditRequest billDetailEditRequest, HttpServletRequest request) {
        if (billDetailEditRequest == null || billDetailEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillDetail billDetail = new BillDetail();
        BeanUtils.copyProperties(billDetailEditRequest, billDetail);
        // 数据校验
        billDetailService.validBillDetail(billDetail, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = billDetailEditRequest.getId();
        BillDetail oldBillDetail = billDetailService.getById(id);
        ThrowUtils.throwIf(oldBillDetail == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldBillDetail.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billDetailService.updateById(billDetail);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
