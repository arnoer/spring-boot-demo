package com.bravo.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bravo.pojo.DuplicateSampleOrderLogPo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
@DS("oracle_1")
public interface DuplicateSampleOrderLogService extends IService<DuplicateSampleOrderLogPo> {

	public void saveLog(String orderNo, Integer operationType, String userId, String remark);

	public void batchSaveLog(List<String> orderNos, Integer operationType, String userId, String remark);

}
