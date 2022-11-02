package com.bravo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bravo.pojo.MpUserPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 继承MyBatis-Plus提供的BaseMapper，提供了增删改查及分页方法，基本已经完全满足日常开发需求
 *
 * @author qiyu
 */
public interface MpUserMapper extends BaseMapper<MpUserPojo> {
    MpUserPojo myGetById(long l);

    void truncateTable(String tableName);

    void insertBatch(@Param("pos") List<MpUserPojo> pos);
}
