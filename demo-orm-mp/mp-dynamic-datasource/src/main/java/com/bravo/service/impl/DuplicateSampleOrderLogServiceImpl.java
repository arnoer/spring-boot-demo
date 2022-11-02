package com.bravo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bravo.dao.DuplicateSampleOrderLogDao;
import com.bravo.pojo.DuplicateSampleOrderLogPo;
import com.bravo.service.DuplicateSampleOrderLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dell
 * @since 2022-10-27
 */
@Service
@Slf4j
public class DuplicateSampleOrderLogServiceImpl extends ServiceImpl<DuplicateSampleOrderLogDao, DuplicateSampleOrderLogPo> implements DuplicateSampleOrderLogService {

    @Autowired
    private DuplicateSampleOrderLogDao duplicateSampleOrderLogDao;

    @Override
    public void saveLog(String orderNo, Integer operationType, String userId, String remark) {
        DuplicateSampleOrderLogPo po = new DuplicateSampleOrderLogPo();
        po.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        po.setDateTime(new Date());
        po.setOperationType(operationType);
        po.setOrderNo(orderNo);
        po.setRemark(remark);
        po.setUserId(userId);
        save(po);
    }

    @Override
    public void batchSaveLog(List<String> orderNos, Integer operationType, String userId, String remark) {
		/*
			使用parallelStream并行流foreach操作list
			1、该流所有的任务完成，合并结果后才会执行到下一条语句
			2、添加po到list中,会有线程并发问题，list内部维护的size，add操作会出现覆盖问题，此外list扩容时，会出现list中出现null元素

			解决方案：
				1、使用并行流
				List<DuplicateSampleOrderLogPo> logPos =Collections.synchronizedList(new ArrayList<DuplicateSampleOrderLogPo >());
				2、如下：使用stream内部维护的collect，不会有这种问题

		 */
        List<DuplicateSampleOrderLogPo> logPos = orderNos.parallelStream().map(orderNo -> {
            log.info(Thread.currentThread().getName() + "--orderNo");
            DuplicateSampleOrderLogPo po = new DuplicateSampleOrderLogPo();
            po.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            po.setDateTime(new Date());
            po.setOperationType(operationType);
            po.setOrderNo(orderNo);
            po.setRemark(remark);
            po.setUserId(userId);
            return po;
        }).collect(Collectors.toList());
        log.info("batchSaveLog begin begin");

        /*
         ORACLE作为数据源，mybatisPlus的saveBatch是一条一条执行的，批量操作是无效的
              变量的数量不能超过64k=65536,N = 65536 / 变量数
         */
        if (logPos.size() <= 2000) {
            duplicateSampleOrderLogDao.batchSave(logPos);
        } else {
            int times = (int)Math.ceil(logPos.size() / 2000.0);
            for (int i = 0; i < times; i++) {
                duplicateSampleOrderLogDao.batchSave(logPos.subList(i * 2000, Math.min((i + 1) * 2000, logPos.size())));
            }
        }
    }
}
