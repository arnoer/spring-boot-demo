package com.bravo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bravo.pojo.DuplicateSampleOrderPo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
public interface DuplicateSampleOrderDao extends BaseMapper<DuplicateSampleOrderPo> {

//	@Select({"<script>",
//		"select \"order_no\"from duplicate_sample_order where \"state\" IN(",
//		"<foreach collection='states' item='item' separator=','>" ,
//		"#{item}",
//		"</foreach>",
//		")</script>"})

	@Select({
		"<script>",
		"select order_no",
		"from duplicate_sample_order",
		"where state in",
		"<foreach collection='list' item='item' open='(' separator=',' close=')'>",
		"#{item}",
		"</foreach>",
		"</script>"
	})
	List<DuplicateSampleOrderPo> selectAllByState(@Param("list") List<Integer> list);

}
