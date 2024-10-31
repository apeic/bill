package com.apeic.bill.model.dto.billcategory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新账单明细类别请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillCategoryUpdateRequest implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联账单本 id
     */
    private Long billId;

    /**
     * 名称
     */
    private String name;

    /**
     * 用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}