package com.apeic.bill.model.vo;

import cn.hutool.json.JSONUtil;
import com.apeic.bill.model.entity.BillDetail;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 账单明细视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillDetailVO implements Serializable {

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param billDetailVO
     * @return
     */
    public static BillDetail voToObj(BillDetailVO billDetailVO) {
        if (billDetailVO == null) {
            return null;
        }
        BillDetail billDetail = new BillDetail();
        BeanUtils.copyProperties(billDetailVO, billDetail);
        return billDetail;
    }

    /**
     * 对象转封装类
     *
     * @param billDetail
     * @return
     */
    public static BillDetailVO objToVo(BillDetail billDetail) {
        if (billDetail == null) {
            return null;
        }
        BillDetailVO billDetailVO = new BillDetailVO();
        BeanUtils.copyProperties(billDetail, billDetailVO);
        return billDetailVO;
    }
}
