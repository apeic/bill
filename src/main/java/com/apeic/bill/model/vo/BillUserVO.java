package com.apeic.bill.model.vo;

import cn.hutool.json.JSONUtil;
import com.apeic.bill.model.entity.BillUser;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 账单本用户视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillUserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关联账单本 id
     */
    private Long billId;

    private String billName;

    private String billDescription;

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
     * 封装类转对象
     *
     * @param billUserVO
     * @return
     */
    public static BillUser voToObj(BillUserVO billUserVO) {
        if (billUserVO == null) {
            return null;
        }
        BillUser billUser = new BillUser();
        BeanUtils.copyProperties(billUserVO, billUser);
        return billUser;
    }

    /**
     * 对象转封装类
     *
     * @param billUser
     * @return
     */
    public static BillUserVO objToVo(BillUser billUser) {
        if (billUser == null) {
            return null;
        }
        BillUserVO billUserVO = new BillUserVO();
        BeanUtils.copyProperties(billUser, billUserVO);
        return billUserVO;
    }
}
