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
@TableName("DUPLICATE_SAMPLE_ORDER")
public class DuplicateSampleOrderPo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId("ORDER_NO")
    private String orderNo;

    @TableField("MEETING_ID")
    private String meetingId;

    @TableField("DIGITAL_RECEPTION_ROOM_ID")
    private String digitalReceptionRoomId;

    @TableField("RECEPTION_ROOM_VISITOR_ID")
    private String receptionRoomVisitorId;

    @TableField("PURCHASER_REQUIREMENT")
    private String purchaserRequirement;

    @TableField("BADGE_NO")
    private String badgeNo;

    @TableField("PRODUCT_NAME")
    private String productName;

    @TableField("PURCHASER_NUM")
    private Long purchaserNum;

    @TableField("PURCHASER_UNIT")
    private String purchaserUnit;

    @TableField("PAY")
    private Integer pay;

    @TableField("ORDER_TIME")
    private String orderTime;

    @TableField("REMARK")
    private String remark;

    @TableField("SAMPLE_PICTURE")
    private String samplePicture;

    @TableField("CREATE_TIME")
    private Date createTime;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("UPDATE_TIME")
    private Date updateTime;

    @TableField("UPDATE_USER")
    private String updateUser;

    @TableField("ORDER_TIME_DATE")
    private Date orderTimeDate;

    @TableField("SUPPLIER_ID")
    private String supplierId;

    @TableField("SUPPLIER_CODE")
    private String supplierCode;

    @TableField("EXHIBITION_ID")
    private String exhibitionId;

    @TableField("UNIT_PRICE")
    private Double unitPrice;

    @TableField("TOTAL_PRICE")
    private Double totalPrice;

    @TableField("CURRENCY_UNIT")
    private String currencyUnit;

    @TableField("STATE")
    private Integer state;

    @TableField("VERSION")
    private Long version;

    @TableField("PRODUCT_NAME_EN")
    private String productNameEn;

    @TableField("UPDATE_USER_TYPE")
    private Integer updateUserType;


}
