package com.bravo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.bravo.pojo.DuplicateSampleOrderPo;
import com.bravo.service.DuplicateSampleOrderLogService;
import com.bravo.service.DuplicateSampleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinyu.zhang
 * @since 2022/11/2 16:20
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceOracleTest {

    @Autowired
    private DuplicateSampleOrderService duplicateSampleOrderService;

    @Autowired
    private DuplicateSampleOrderLogService duplicateSampleOrderLogService;

    @Test
    public void batchOperateTest() {
        StopWatch sw = new StopWatch("======batchOperateTest======");

        sw.start("batchOperateTest SELECT");
        List<DuplicateSampleOrderPo> orderPos = duplicateSampleOrderService.list(
            new LambdaQueryWrapper<DuplicateSampleOrderPo>().select(DuplicateSampleOrderPo::getOrderNo).in(DuplicateSampleOrderPo::getState, Arrays.asList(0,1)));
        sw.stop();
        log.info("batchOperateTest SELECT, size:{}, time:{}", orderPos.size(), sw.getLastTaskTimeMillis());

        List<String> ids = orderPos.stream().map(DuplicateSampleOrderPo::getOrderNo).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(ids)) {
            sw.start("batchOperateTest UPDATE");
            duplicateSampleOrderService.update(
                new LambdaUpdateWrapper<DuplicateSampleOrderPo>().set(DuplicateSampleOrderPo::getState, 3).set(DuplicateSampleOrderPo::getUpdateTime, new Date())
                    .set(DuplicateSampleOrderPo::getUpdateUserType, 4).set(DuplicateSampleOrderPo::getUpdateUser, "system")
                    .in(DuplicateSampleOrderPo::getState, Arrays.asList(0,1))
            );
            sw.stop();
            log.info("batchOperateTest UPDATE, time:{}", sw.getLastTaskTimeMillis());

            sw.start("batchOperateTest, INSERT");
            duplicateSampleOrderLogService.batchSaveLog(ids,4, "system", "会客厅关闭");
            sw.stop();
            log.info("batchOperateTest INSERT, size:{},  time:{}", ids.size(), sw.getLastTaskTimeMillis());
        }

        log.info(sw.prettyPrint());
    }

    @Test
    public void batchUpdate() {

    }

    /**
     * 对list进行分组
     *
     * 比如sql的in语法，oracle的in变量是有限制的
     *
     * @param source
     * @param n
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {
        if (null == source || source.size() == 0 || n <= 0) {
            return null;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        int sourceSize = source.size();
        int size = (source.size() / n) + 1;
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<T>();
            for (int j = i * n; j < (i + 1) * n; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }

    public void initData() {

    }
}
