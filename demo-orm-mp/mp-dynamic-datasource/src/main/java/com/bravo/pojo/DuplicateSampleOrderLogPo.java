package com.bravo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("DUPLICATE_SAMPLE_ORDER_LOG")
public class DuplicateSampleOrderLogPo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId("ID")
    private String id;

    @TableField("ORDER_NO")
    private String orderNo;

    @TableField("OPERATION_TYPE")
    private Integer operationType;

    @TableField("DATE_TIME")
    private Date dateTime;

    @TableField("USER_ID")
    private String userId;

    @TableField("REMARK")
    private String remark;


}
