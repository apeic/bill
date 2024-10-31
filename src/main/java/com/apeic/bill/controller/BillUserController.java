package com.apeic.bill.controller;

import com.apeic.bill.annotation.AuthCheck;
import com.apeic.bill.common.BaseResponse;
import com.apeic.bill.common.DeleteRequest;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.common.ResultUtils;
import com.apeic.bill.constant.UserConstant;
import com.apeic.bill.exception.BusinessException;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.model.dto.billuser.BillUserAddRequest;
import com.apeic.bill.model.dto.billuser.BillUserEditRequest;
import com.apeic.bill.model.dto.billuser.BillUserQueryRequest;
import com.apeic.bill.model.dto.billuser.BillUserUpdateRequest;
import com.apeic.bill.model.entity.BillUser;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillUserVO;
import com.apeic.bill.service.BillUserService;
import com.apeic.bill.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 账单本用户接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/billUser")
@Slf4j
public class BillUserController {

    @Resource
    private BillUserService billUserService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建账单本用户
     *
     * @param billUserAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBillUser(@RequestBody BillUserAddRequest billUserAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(billUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        BillUser billUser = new BillUser();
        BeanUtils.copyProperties(billUserAddRequest, billUser);
        // 数据校验
        billUserService.validBillUser(billUser, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        billUser.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = billUserService.save(billUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newBillUserId = billUser.getId();
        return ResultUtils.success(newBillUserId);
    }

    /**
     * 删除账单本用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBillUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        BillUser oldBillUser = billUserService.getById(id);
        ThrowUtils.throwIf(oldBillUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldBillUser.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billUserService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新账单本用户（仅管理员可用）
     *
     * @param billUserUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateBillUser(@RequestBody BillUserUpdateRequest billUserUpdateRequest) {
        if (billUserUpdateRequest == null || billUserUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillUser billUser = new BillUser();
        BeanUtils.copyProperties(billUserUpdateRequest, billUser);
        // 数据校验
        billUserService.validBillUser(billUser, false);
        // 判断是否存在
        long id = billUserUpdateRequest.getId();
        BillUser oldBillUser = billUserService.getById(id);
        ThrowUtils.throwIf(oldBillUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = billUserService.updateById(billUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取账单本用户（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BillUserVO> getBillUserVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        BillUser billUser = billUserService.getById(id);
        ThrowUtils.throwIf(billUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(billUserService.getBillUserVO(billUser, request));
    }

    /**
     * 分页获取账单本用户列表（仅管理员可用）
     *
     * @param billUserQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<BillUser>> listBillUserByPage(@RequestBody BillUserQueryRequest billUserQueryRequest) {
        long current = billUserQueryRequest.getCurrent();
        long size = billUserQueryRequest.getPageSize();
        // 查询数据库
        Page<BillUser> billUserPage = billUserService.page(new Page<>(current, size),
                billUserService.getQueryWrapper(billUserQueryRequest));
        return ResultUtils.success(billUserPage);
    }

    /**
     * 分页获取账单本用户列表（封装类）
     *
     * @param billUserQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BillUserVO>> listBillUserVOByPage(@RequestBody BillUserQueryRequest billUserQueryRequest,
                                                               HttpServletRequest request) {
        long current = billUserQueryRequest.getCurrent();
        long size = billUserQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillUser> billUserPage = billUserService.page(new Page<>(current, size),
                billUserService.getQueryWrapper(billUserQueryRequest));
        // 获取封装类
        return ResultUtils.success(billUserService.getBillUserVOPage(billUserPage, request));
    }

    /**
     * 分页获取当前登录用户创建的账单本用户列表
     *
     * @param billUserQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BillUserVO>> listMyBillUserVOByPage(@RequestBody BillUserQueryRequest billUserQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(billUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        billUserQueryRequest.setUserId(loginUser.getId());
        long current = billUserQueryRequest.getCurrent();
        long size = billUserQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillUser> billUserPage = billUserService.page(new Page<>(current, size),
                billUserService.getQueryWrapper(billUserQueryRequest));
        // 获取封装类
        return ResultUtils.success(billUserService.getBillUserVOPage(billUserPage, request));
    }

    /**
     * 编辑账单本用户（给用户使用）
     *
     * @param billUserEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editBillUser(@RequestBody BillUserEditRequest billUserEditRequest, HttpServletRequest request) {
        if (billUserEditRequest == null || billUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillUser billUser = new BillUser();
        BeanUtils.copyProperties(billUserEditRequest, billUser);
        // 数据校验
        billUserService.validBillUser(billUser, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = billUserEditRequest.getId();
        BillUser oldBillUser = billUserService.getById(id);
        ThrowUtils.throwIf(oldBillUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldBillUser.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = billUserService.updateById(billUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
