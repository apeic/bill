package com.apeic.bill.controller;

import com.apeic.bill.annotation.AuthCheck;
import com.apeic.bill.common.BaseResponse;
import com.apeic.bill.common.DeleteRequest;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.common.ResultUtils;
import com.apeic.bill.constant.UserConstant;
import com.apeic.bill.exception.BusinessException;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.model.dto.bill.BillAddRequest;
import com.apeic.bill.model.dto.bill.BillEditRequest;
import com.apeic.bill.model.dto.bill.BillQueryRequest;
import com.apeic.bill.model.dto.bill.BillUpdateRequest;
import com.apeic.bill.model.entity.Bill;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillVO;
import com.apeic.bill.service.BillService;
import com.apeic.bill.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 账单本接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/bill")
@Slf4j
public class BillController {

    @Resource
    private BillService billService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建账单本
     *
     * @param billAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBill(@RequestBody BillAddRequest billAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(billAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Bill bill = new Bill();
        BeanUtils.copyProperties(billAddRequest, bill);
        // 数据校验
        billService.validBill(bill, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        bill.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = billService.save(bill);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newBillId = bill.getId();
        return ResultUtils.success(newBillId);
    }

    /**
     * 删除账单本
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBill(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Bill oldBill = billService.getById(id);
        ThrowUtils.throwIf(oldBill == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldBill.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新账单本（仅管理员可用）
     *
     * @param billUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateBill(@RequestBody BillUpdateRequest billUpdateRequest) {
        if (billUpdateRequest == null || billUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Bill bill = new Bill();
        BeanUtils.copyProperties(billUpdateRequest, bill);
        // 数据校验
        billService.validBill(bill, false);
        // 判断是否存在
        long id = billUpdateRequest.getId();
        Bill oldBill = billService.getById(id);
        ThrowUtils.throwIf(oldBill == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = billService.updateById(bill);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取账单本（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BillVO> getBillVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Bill bill = billService.getById(id);
        ThrowUtils.throwIf(bill == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(billService.getBillVO(bill, request));
    }

    /**
     * 分页获取账单本列表（仅管理员可用）
     *
     * @param billQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Bill>> listBillByPage(@RequestBody BillQueryRequest billQueryRequest) {
        long current = billQueryRequest.getCurrent();
        long size = billQueryRequest.getPageSize();
        // 查询数据库
        Page<Bill> billPage = billService.page(new Page<>(current, size),
                billService.getQueryWrapper(billQueryRequest));
        return ResultUtils.success(billPage);
    }

    /**
     * 分页获取账单本列表（封装类）
     *
     * @param billQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BillVO>> listBillVOByPage(@RequestBody BillQueryRequest billQueryRequest,
                                                               HttpServletRequest request) {
        long current = billQueryRequest.getCurrent();
        long size = billQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Bill> billPage = billService.page(new Page<>(current, size),
                billService.getQueryWrapper(billQueryRequest));
        // 获取封装类
        return ResultUtils.success(billService.getBillVOPage(billPage, request));
    }

    /**
     * 分页获取当前登录用户创建的账单本列表
     *
     * @param billQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BillVO>> listMyBillVOByPage(@RequestBody BillQueryRequest billQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(billQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        billQueryRequest.setUserId(loginUser.getId());
        long current = billQueryRequest.getCurrent();
        long size = billQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Bill> billPage = billService.page(new Page<>(current, size),
                billService.getQueryWrapper(billQueryRequest));
        // 获取封装类
        return ResultUtils.success(billService.getBillVOPage(billPage, request));
    }

    /**
     * 编辑账单本（给用户使用）
     *
     * @param billEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editBill(@RequestBody BillEditRequest billEditRequest, HttpServletRequest request) {
        if (billEditRequest == null || billEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Bill bill = new Bill();
        BeanUtils.copyProperties(billEditRequest, bill);
        // 数据校验
        billService.validBill(bill, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = billEditRequest.getId();
        Bill oldBill = billService.getById(id);
        ThrowUtils.throwIf(oldBill == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldBill.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billService.updateById(bill);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
