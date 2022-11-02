package com.bravo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bravo.pojo.MpUserPojo;
import com.bravo.service.MpUserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 1.Service层更加易用，对Mapper做了一层封装
 *
 * 2.Service层的命名方式和Mapper不同
 * select --> get
 * insert --> save
 * delete --> remove
 * update --> update
 *
 * 3.支持了批量操作，而且可以指定一次操作多少个，比如 saveBatch(Collection<T> entityList, int batchSize);
 *
 * 4.对于增删改，返回值统一为boolean，而不是int（修改行数）
 *
 * 5.getOne(Wrapper, boolean)与BaseMapper的selectOne(Wrapper)不同，如果传false不会抛异常，有多个值则list.get(0)
 *   T getOne(Wrapper<T> queryWrapper, boolean throwEx);
 *
 * 6.Service层链式调用更顺手
 *
 * @author qiyu
 * @date 2020-09-13 16:41
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
public class ServiceTest {

    @Autowired
    private MpUserService userService;

    @Test
    public void testServiceSave() {
        // ---------- 增 -----------
        // 插入
        MpUserPojo userPojo = new MpUserPojo();
        userPojo.setName("test Service");
        userPojo.setAge(18);
        userService.save(userPojo);

        userPojo.setId(null);

        // 批量插入，即使不传入batchSize，默认也是1000条。比如实际由1w条，内部会按每次1000条批量插入
        boolean save = userService.saveBatch(Collections.singletonList(userPojo), 1000);
    }

    /**
     * Mysql连接驱动参数需要添加:rewriteBatchedStatements=true
     * 不加：是多条insert
     * 加： values后拼接(?,?),(?,?)....
     */
    @Test
    public void testServiceBatchSaveByZ() {
        truncateTable("mp_user");
        ArrayList<MpUserPojo> pos = new ArrayList<>();
        for (int i = 0; i < 1200; i++) {
            pos.add(new MpUserPojo().setName("wangwu" + i).setAge(new Double(Math.random()* 100).intValue()));
        }
        StopWatch sw = new StopWatch("测试Mysql批量插入数据");
        sw.start("saveBatch begin");
        // 批量插入，即使不传入batchSize，默认也是1000条。比如实际由1w条，内部会按每次1000条批量插入
        boolean save = userService.saveBatch(pos, 1000);
        sw.stop();
        log.info(sw.prettyPrint()); // &rewriteBatchedStatements=true  测试50000条数据 2582704700 ns
    }

    /**
     * 特别注意，虽然Wrapper的条件设置为null不影响，但Wrapper本身不设置任何条件还是会触发全表更新
     */
    @Test
    public void testServiceUpdate() {
        // ---------- 改 LambdaWrapper和普通Wrapper的唯一区别是 一个用POJO的字段，一个用数据库的字段表示条件-----------
        // 根据id更新
        MpUserPojo updateUser = new MpUserPojo();
        updateUser.setId(1L);
        updateUser.setName("bravo2020");
        userService.updateById(updateUser);

        // 条件更新 方式1 lambdaUpdate，用DO的字段
        boolean update1 = userService.lambdaUpdate().eq(MpUserPojo::getName, "bravo").set(MpUserPojo::getAge, 23).update();

        // 条件更新 方式2 普通update，用数据库字段
        boolean update2 = userService.update().eq("name", "bravo").set("age", 18).update();

        // 条件更新 方式3 传入LambdaUpdateWrapper，用DO的字段
        boolean update3 = userService.update(new LambdaUpdateWrapper<MpUserPojo>().eq(MpUserPojo::getName, "bravo").set(MpUserPojo::getAge, 18));

        // 条件更新 方式4 传入UpdateWrapper，用数据库字段
        boolean update4 = userService.update(new UpdateWrapper<MpUserPojo>().eq("name", "bravo").set("age", 18));

        // 条件更新 方式5 传入Entity表示更新的字段，QueryWrapper表示条件
        boolean bravo5 = userService.update(updateUser, new QueryWrapper<MpUserPojo>().lambda().eq(MpUserPojo::getName, "bravo"));

        // 批量更新。如果要根据条件批量更新还是自己写吧，注意SQL。
        boolean update = userService.updateBatchById(Collections.singletonList(updateUser), 1000);
    }

    /**
     * 如何把数据库的表字段更新为null?
     *
     * 方式一： 在字段上加注解
     * `@TableField`注解的属性：Strategy（新版本根据操作做了区分insert update）,
     *      IGNORED：忽略。不管有没有有设置属性，所有的字段都会设置到sql语句中，如果没设置值会更新为null；
     *      NOT_NULL：非 NULL，默认策略。也就是忽略null的字段，不忽略""，所以sql语句中对于null的字段不会拼接
     *      NOT_EMPTY：非空。为null，为空串的忽略，就是如果设置值为null，""，不会插入数据库；
     * 方式二： @TableField(fill = FieldFill.UPDATE)   https://baomidou.com/pages/4c6bcf/
     *      当字段为空时，会设置为null
     *      sql: UPDATE mp_user SET name=?, age=?, user_type=?, create_time=?, update_time=?, version=? WHERE id=? AND version=? AND deleted=0
     *
     * 方式三： 全局配置
     *      mybatis-plus.global-config.update-strategy: ignored
     *
*    * 方式四：使用wrapper进行update
     */
    @Test
    public void testServiceUpdateByZ() {
        List<MpUserPojo> pojos = userService.list();
        if(pojos.size() > 0) {
            MpUserPojo mpUserPojo = pojos.get(0);
            mpUserPojo.setAge(null);
//            mpUserPojo.setCreateTime(null);
            // UPDATE mp_user SET name=?, user_type=?, update_time=?, version=? WHERE id=? AND version=? AND deleted=0
            boolean b = userService.updateById(mpUserPojo);

            /*
            方式四：
                以下两种写法：
                写法1：更新set的字段，可以设置为null
                写法2：第一个参数entity，set的字段根据mp默认的策略，忽略为空的字段，不为空的字段可以update，因此，为空的字段需要在wrapper中set

            */
//            userService.update(null, Wrappers.<MpUserPojo>lambdaUpdate().set(MpUserPojo::getAge, null).eq(MpUserPojo::getId, mpUserPojo.getId()));
//            userService.update(new MpUserPojo().setName("wag"), Wrappers.<MpUserPojo>lambdaUpdate().set(MpUserPojo::getAge, null).eq(MpUserPojo::getId, mpUserPojo.getId()));
        }
    }

    /**
     * 注意：saveOrUpdate(T entity)当字段存在@version乐观锁时, sql存在where version ,如果数据version不匹配就会更新失败，就会执行save操作，产生脏数据
     */
    @Test
    public void testServiceSaveOrUpdateByZ() {
        MpUserPojo mpUserPojo = new MpUserPojo().setName("test_one").setAge(new Double(Math.random() * 100).intValue());
        // id不存在 直接插入，id根据主键生成策略
        boolean b = userService.saveOrUpdate(mpUserPojo);

        /*
            执行逻辑
                if(根据updateWrapper条件更新) {
                    return;
                } else {
                    if(entity.getId() != null && getById(entity.getID())) {
                        update();
                    } else {
                        save();
                    }
                }
         */
        UpdateWrapper<MpUserPojo> wrapper = new UpdateWrapper<MpUserPojo>().eq("name", "wangwu").set("age", 24);
        userService.saveOrUpdate(new MpUserPojo().setName("zhaoniu").setAge(24), wrapper);
        // entity可以设置为null，但是不可以设置为空对象，save操作无值映射就会报错
        userService.saveOrUpdate(null, wrapper);
    }

    @Test
    public void testServiceGet() {
        // ---------- 查 -----------
        // 条件查询 方式1 lambdaQuery，用DO的字段
        List<MpUserPojo> queryList1 = userService.lambdaQuery().eq(MpUserPojo::getName, "bravo")
            .select(MpUserPojo::getName, MpUserPojo::getAge)
            .list();

        // 条件查询 方式2 普通Query，用数据库字段
        List<MpUserPojo> queryList2 = userService.query().ge("age", 19).select("name", "age").list();

        // 条件查询 方式3 传入LambdaQueryWrapper，用DO的字段
        List<MpUserPojo> queryList3 = userService.list(new LambdaQueryWrapper<MpUserPojo>().eq(MpUserPojo::getName, "bravo"));

        // 条件查询 方式4 传入QueryWrapper，用数据库字段
        List<MpUserPojo> queryList4 = userService.list(new QueryWrapper<MpUserPojo>().eq("name", "bravo"));

        // 条件查询 手动把sql拼接到最后(有sql注入的风险,请谨慎使用)
        List<MpUserPojo> queryList5 = userService.list(new QueryWrapper<MpUserPojo>().eq("name", "bravo")
            .last("limit 1"));

        // getOne条件查询，有个重载方法 getOne(Wrapper<T> queryWrapper, boolean throwEx);
        MpUserPojo getOne = userService.getOne(Wrappers.<MpUserPojo>lambdaQuery().eq(MpUserPojo::getName, "bravo"));

        // 批量查询
        List<MpUserPojo> listBatch = userService.listByIds(Arrays.asList(1L, 2L, 3L));

        // 分页
        Page<MpUserPojo> page = new Page<>();
        page.setPages(1);
        page.setSize(2);
        Page<MpUserPojo> pageList = userService.page(page, new QueryWrapper<MpUserPojo>().lambda().eq(MpUserPojo::getName, "bravo"));

        // count
        int count = userService.count(new LambdaQueryWrapper<MpUserPojo>().eq(MpUserPojo::getName, "bravo"));
    }

    @Test
    public void testServiceGetPage() {
        // 分页
        Page<MpUserPojo> page = new Page<>(1,2);
        List<OrderItem> orderCondition = Lists.newArrayList(
            new OrderItem("id", false),
            new OrderItem("create_time", false)
        );
        // 设置排序条件
        page.setOrders(orderCondition);

        // 两种查询都可以
        Page<MpUserPojo> pageList1 = userService.page(page, new QueryWrapper<MpUserPojo>().lambda().eq(MpUserPojo::getName, "bravo"));
        Page<MpUserPojo> pageList2 = userService.lambdaQuery().eq(MpUserPojo::getName, "bravo").page(page);
    }

    /**
     * 特别注意，虽然Wrapper的条件设置为null不影响，但Wrapper本身不设置任何条件还是会触发全表删除
     */
    @Test
    public void testServiceRemove() {
        // ---------- 删 和通用Mapper不同的是，@TableLogic对批量删除也是起作用的 -----------
        // 根据id删除
        userService.removeById(1L);
        // 条件删除，特别注意，Wrapper不设置任何条件还是会触发全表删除
        userService.remove(Wrappers.<MpUserPojo>lambdaQuery());
        // 根据ids批量删除 UPDATE mp_user SET deleted=1 WHERE id IN ( 1 , 2 , 3 ) AND deleted=0;
        boolean remove = userService.removeByIds(Arrays.asList(1L, 2L, 3L));
    }

    private void truncateTable(String tableName) {
        userService.clearData(tableName);
    }

}
