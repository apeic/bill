package com.apeic.bill.model.dto.billdetail;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 更新账单明细请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillDetailUpdateRequest implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账单日期
     */
    private Date recordDate;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联的账单本id
     */
    private Long billId;

    /**
     * 记录的用户id
     */
    private Long userId;

    /**
     * 账单明细类别id
     */
    private Long categoryId;

    /**
     * 类别名称
     */
    private String categoryName;

    private static final long serialVersionUID = 1L;
}