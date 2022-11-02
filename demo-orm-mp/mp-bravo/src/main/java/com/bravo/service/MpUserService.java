package com.bravo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bravo.pojo.MpUserPojo;

/**
 * 继承MyBatis-Plus提供的IService接口
 *
 * @author qiyu
 * @date 2020-09-13 16:38
 */
public interface MpUserService extends IService<MpUserPojo> {
    void clearData(String tableName);
}
