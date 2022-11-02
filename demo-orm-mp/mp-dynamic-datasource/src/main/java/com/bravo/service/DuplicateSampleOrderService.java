package com.bravo.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bravo.pojo.DuplicateSampleOrderPo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
@DS("oracle_1")
public interface DuplicateSampleOrderService extends IService<DuplicateSampleOrderPo> {

}
