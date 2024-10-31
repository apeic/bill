package com.apeic.bill.model.vo;

import cn.hutool.json.JSONUtil;
import com.apeic.bill.model.entity.Bill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 账单本视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillVO implements Serializable {

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
     * 关联用户 id
     */
    private Long userId;

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
     * @param billVO
     * @return
     */
    public static Bill voToObj(BillVO billVO) {
        if (billVO == null) {
            return null;
        }
        Bill bill = new Bill();
        BeanUtils.copyProperties(billVO, bill);

        return bill;
    }

    /**
     * 对象转封装类
     *
     * @param bill
     * @return
     */
    public static BillVO objToVo(Bill bill) {
        if (bill == null) {
            return null;
        }
        BillVO billVO = new BillVO();
        BeanUtils.copyProperties(bill, billVO);
        return billVO;
    }
}
