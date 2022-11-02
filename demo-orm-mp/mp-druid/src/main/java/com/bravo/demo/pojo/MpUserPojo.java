package com.bravo.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 通用Mapper中叫 @Table(name = "tk_user")
 *
 * @author qiyu
 */
@Data
@TableName("mp_user")
@Accessors(chain = true)
public class MpUserPojo {

    /**
     * MyBatis-Plus默认名为'id'的字段是主键
     * 如果主键名不叫'id'，而是'userId'之类的，必须通过 @TableId 标识
     * 主键生成策略默认是无意义的long数值，可以指定@TableId的IdType属性为AUTO，根据数据库最大id自增
     * 加上 @TableId(type = IdType.AUTO)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
//    @TableField(updateStrategy = FieldStrategy.IGNORED)
//        @TableField(fill = FieldFill.UPDATE)
    private Integer age;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     * 设置表结构时，设置了on update CURRENT_TIMESTAMP, 表中的任意字段发生改变，时间会更新为当前时间戳，但是使用update没有字段发生改变，不会变化
     * 取消on update：alter table mp_dd change create_time create_time TIMESTAMP not null default CURRENT_TIMESTAMP
     *
     */
    private Date updateTime;

    /**
     * 是否删除，逻辑删除请用 @TableLogic
     */
//    @TableLogic
    private Boolean deleted;

    /**
     * 乐观锁版本号，需要乐观锁请用 @Version
     * 支持的字段类型:
     * long,
     * Long,
     * int,
     * Integer,
     * java.util.Date,
     * java.sql.Timestamp,
     * java.time.LocalDateTime
     */
    @Version
    private Integer version;

}
