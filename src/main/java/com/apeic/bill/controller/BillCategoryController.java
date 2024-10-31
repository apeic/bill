package com.apeic.bill.controller;

import com.apeic.bill.annotation.AuthCheck;
import com.apeic.bill.common.BaseResponse;
import com.apeic.bill.common.DeleteRequest;
import com.apeic.bill.common.ErrorCode;
import com.apeic.bill.common.ResultUtils;
import com.apeic.bill.constant.UserConstant;
import com.apeic.bill.exception.BusinessException;
import com.apeic.bill.exception.ThrowUtils;
import com.apeic.bill.model.dto.billcategory.BillCategoryAddRequest;
import com.apeic.bill.model.dto.billcategory.BillCategoryEditRequest;
import com.apeic.bill.model.dto.billcategory.BillCategoryQueryRequest;
import com.apeic.bill.model.dto.billcategory.BillCategoryUpdateRequest;
import com.apeic.bill.model.entity.BillCategory;
import com.apeic.bill.model.entity.User;
import com.apeic.bill.model.vo.BillCategoryVO;
import com.apeic.bill.service.BillCategoryService;
import com.apeic.bill.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 账单明细类别接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/billCategory")
@Slf4j
public class BillCategoryController {

    @Resource
    private BillCategoryService billCategoryService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建账单明细类别
     *
     * @param billCategoryAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBillCategory(@RequestBody BillCategoryAddRequest billCategoryAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(billCategoryAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        BillCategory billCategory = new BillCategory();
        BeanUtils.copyProperties(billCategoryAddRequest, billCategory);
        // 数据校验
        billCategoryService.validBillCategory(billCategory, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
//        billCategory.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = billCategoryService.save(billCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newBillCategoryId = billCategory.getId();
        return ResultUtils.success(newBillCategoryId);
    }

    /**
     * 删除账单明细类别
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBillCategory(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        BillCategory oldBillCategory = billCategoryService.getById(id);
        ThrowUtils.throwIf(oldBillCategory == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
//        if (!oldBillCategory.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        // 操作数据库
        boolean result = billCategoryService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新账单明细类别（仅管理员可用）
     *
     * @param billCategoryUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateBillCategory(@RequestBody BillCategoryUpdateRequest billCategoryUpdateRequest) {
        if (billCategoryUpdateRequest == null || billCategoryUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillCategory billCategory = new BillCategory();
        BeanUtils.copyProperties(billCategoryUpdateRequest, billCategory);
        // 数据校验
        billCategoryService.validBillCategory(billCategory, false);
        // 判断是否存在
        long id = billCategoryUpdateRequest.getId();
        BillCategory oldBillCategory = billCategoryService.getById(id);
        ThrowUtils.throwIf(oldBillCategory == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = billCategoryService.updateById(billCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取账单明细类别（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BillCategoryVO> getBillCategoryVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        BillCategory billCategory = billCategoryService.getById(id);
        ThrowUtils.throwIf(billCategory == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(billCategoryService.getBillCategoryVO(billCategory, request));
    }

    /**
     * 分页获取账单明细类别列表（仅管理员可用）
     *
     * @param billCategoryQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<BillCategory>> listBillCategoryByPage(@RequestBody BillCategoryQueryRequest billCategoryQueryRequest) {
        long current = billCategoryQueryRequest.getCurrent();
        long size = billCategoryQueryRequest.getPageSize();
        // 查询数据库
        Page<BillCategory> billCategoryPage = billCategoryService.page(new Page<>(current, size),
                billCategoryService.getQueryWrapper(billCategoryQueryRequest));
        return ResultUtils.success(billCategoryPage);
    }

    /**
     * 分页获取账单明细类别列表（封装类）
     *
     * @param billCategoryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BillCategoryVO>> listBillCategoryVOByPage(@RequestBody BillCategoryQueryRequest billCategoryQueryRequest,
                                                               HttpServletRequest request) {
        long current = billCategoryQueryRequest.getCurrent();
        long size = billCategoryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillCategory> billCategoryPage = billCategoryService.page(new Page<>(current, size),
                billCategoryService.getQueryWrapper(billCategoryQueryRequest));
        // 获取封装类
        return ResultUtils.success(billCategoryService.getBillCategoryVOPage(billCategoryPage, request));
    }

    /**
     * 分页获取当前登录用户创建的账单明细类别列表
     *
     * @param billCategoryQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BillCategoryVO>> listMyBillCategoryVOByPage(@RequestBody BillCategoryQueryRequest billCategoryQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(billCategoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        billCategoryQueryRequest.setUserId(loginUser.getId());
        long current = billCategoryQueryRequest.getCurrent();
        long size = billCategoryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<BillCategory> billCategoryPage = billCategoryService.page(new Page<>(current, size),
                billCategoryService.getQueryWrapper(billCategoryQueryRequest));
        // 获取封装类
        return ResultUtils.success(billCategoryService.getBillCategoryVOPage(billCategoryPage, request));
    }

    /**
     * 编辑账单明细类别（给用户使用）
     *
     * @param billCategoryEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editBillCategory(@RequestBody BillCategoryEditRequest billCategoryEditRequest, HttpServletRequest request) {
        if (billCategoryEditRequest == null || billCategoryEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        BillCategory billCategory = new BillCategory();
        BeanUtils.copyProperties(billCategoryEditRequest, billCategory);
        // 数据校验
        billCategoryService.validBillCategory(billCategory, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = billCategoryEditRequest.getId();
        BillCategory oldBillCategory = billCategoryService.getById(id);
        ThrowUtils.throwIf(oldBillCategory == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
//        if (!oldBillCategory.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        // 操作数据库
        boolean result = billCategoryService.updateById(billCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
