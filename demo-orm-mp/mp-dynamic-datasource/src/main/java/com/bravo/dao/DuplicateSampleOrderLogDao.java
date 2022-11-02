package com.bravo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bravo.pojo.DuplicateSampleOrderLogPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
public interface DuplicateSampleOrderLogDao extends BaseMapper<DuplicateSampleOrderLogPo> {

//	@SqlParser(filter=true)
	void batchSave(@Param("logPos") List<DuplicateSampleOrderLogPo> logPos);

}
