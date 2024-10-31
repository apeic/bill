package com.apeic.bill.model.vo;

import cn.hutool.json.JSONUtil;
import com.apeic.bill.model.entity.BillCategory;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 账单明细类别视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class BillCategoryVO implements Serializable {

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
     * @param billCategoryVO
     * @return
     */
    public static BillCategory voToObj(BillCategoryVO billCategoryVO) {
        if (billCategoryVO == null) {
            return null;
        }
        BillCategory billCategory = new BillCategory();
        BeanUtils.copyProperties(billCategoryVO, billCategory);

        return billCategory;
    }

    /**
     * 对象转封装类
     *
     * @param billCategory
     * @return
     */
    public static BillCategoryVO objToVo(BillCategory billCategory) {
        if (billCategory == null) {
            return null;
        }
        BillCategoryVO billCategoryVO = new BillCategoryVO();
        BeanUtils.copyProperties(billCategory, billCategoryVO);
        return billCategoryVO;
    }
}
