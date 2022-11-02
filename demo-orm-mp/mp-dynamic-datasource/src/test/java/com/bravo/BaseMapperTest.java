package com.bravo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bravo.dao.MpUserMapper;
import com.bravo.pojo.MpUserPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行的时候注意观察控制台打印的SQL
 * <p>
 * 困惑一：
 * QueryWrapper和UpdateWrapper啥区别？
 * <p>
 * 你可以把QueryWrapper等同于通用Mapper的Example，是用来构造条件的。
 * 而UpdateWrapper几乎和QueryWrapper是一样的，只不过多了set()方法，我们可以通过set把要更新的值也一并设置进去。
 * 比如你用UpdateWrapper.eq()等方法构造了一堆条件，如果不想额外构造entity，则可以直接set("name", "bravo")
 * 也就是说 UpdateWrapper ≈ QueryWrapper + 要设置的值
 * <p>
 * 困惑二：
 * LambdaQuery这种Lambda开头的Wrapper又是啥？
 * <p>
 * QueryWrapper/UpdateWrapper默认是这样的wrapper.eq("user_type", 1)，条件字段是数据库Column
 * 这样的坏处是容易写错，而且编译器没法校验。
 * LambdaWrapper则支持POJO字段作为条件，比如wrapper.eq(User::getUserType, 1)，可以编译期发现错误。
 *
 * @author qiyu
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseMapperTest {

    @Autowired
    private MpUserMapper userMapper;

    @Test
    void contextLoads() {
    }

    /**
     * 插入，默认类似于insertSelective()效果。相比通用Mapper，默认名字为id的字段为主键，不需要主键注解
     * INSERT INTO mp_user ( id, name, age, user_type ) VALUES ( 1301805567879794689, '测试1', 18, 1 );
     * 你会发现插入时已经有主键了，MyBatis-Plus默认会自动生成long数值，如需改变请修改MpUserPojo的id
     */
    @Test
    public void testInsert() {
        MpUserPojo mpUserPojo = new MpUserPojo();
        mpUserPojo.setName("测试1");
        mpUserPojo.setAge(18);
        mpUserPojo.setUserType(1);
        // 插入
        userMapper.insert(mpUserPojo);
    }

    @Test
    public void testSelect() {
        /**
         * 根据主键查询
         * SELECT id,name,age,user_type,create_time,update_time,deleted,version
         * FROM mp_user
         * WHERE id=1;
         */
        MpUserPojo mpUserPojo = userMapper.selectById(null);

        /**
         * 条件查询，如果查出多个则抛异常，个人觉得很鸡肋，不推荐使用
         * SELECT id,name,age,user_type,create_time,update_time,deleted,version
         * FROM mp_user
         * WHERE (age = 18);
         */
        //        QueryWrapper<MpUserPojo> oneQuery = new QueryWrapper<>();
        //        oneQuery.eq("age", 18);
        //        MpUserPojo one = userMapper.selectOne(oneQuery);

        /**
         * 条件查询，本质上和selectOne()一样，推荐
         * SELECT id,name,age,user_type,create_time,update_time,deleted,version
         * FROM mp_user
         * WHERE (age = 18);
         */
        QueryWrapper<MpUserPojo> listQuery = new QueryWrapper<>();
        listQuery.eq("age", 18);
        List<MpUserPojo> list = userMapper.selectList(listQuery);

        /**
         * 条件查询 likeRight ==>  like '测试%'，还可以指定查询的字段，避免全列返回
         * SELECT name,age
         * FROM mp_user
         * WHERE (name LIKE 'null%');
         * 即使条件为null，也不会像通用Mapper一样忽略条件导致查询全部
         */
        QueryWrapper<MpUserPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "age").likeRight("name", null);
        List<MpUserPojo> mpUserPojoList2 = userMapper.selectList(queryWrapper);

        /**
         * 不查询指定列
         */
        QueryWrapper<MpUserPojo> specifiedQuery = new QueryWrapper<>();
        specifiedQuery.select(MpUserPojo.class, (e) -> !e.getColumn().equals("name") && !e.getColumn().equals("age"));
        List<MpUserPojo> mpUserPojos = userMapper.selectList(specifiedQuery);

        /**
         * 批量查询，如果ids==null 或 ids.size()==0，会报错，而不是像通用Mapper一样查询全部
         * SELECT id,name,age,user_type,create_time,update_time,deleted,version
         * FROM mp_user
         * WHERE id IN ( );
         * 其实你点进源码，会发现人家已经提醒了ids不能为空
         */
        //        List<MpUserPojo> mpUserList = userMapper.selectBatchIds(new ArrayList<>());

        /**
         * 分页查询，需要额外配置MyBatis-Plus提供的分页插件，{@link com.bravo.demo.config.MybatisPlusConfig}
         *
         * 会发送两条SQL：
         *
         * 1.查询总数量
         * SELECT COUNT(1)
         * FROM mp_user
         * WHERE (name LIKE '测试1%');
         *
         * 2.查询分页数据
         * SELECT name,age
         * FROM mp_user
         * WHERE (name LIKE '测试1%')
         * LIMIT 1;
         *
         * list打印：[MpUserPojo(id=null, name=测试1, age=18, userType=null, createTime=null, updateTime=null,
         * deleted=null, version=null)]
         */
        // 分页
        Page<MpUserPojo> page = new Page<>();
        page.setPages(1);
        page.setSize(1);
        // 条件
        QueryWrapper<MpUserPojo> pageQuery = new QueryWrapper<>();
        pageQuery.select("name", "age").likeRight("name", "测试1");
        Page<MpUserPojo> pageList = userMapper.selectPage(page, pageQuery);
        System.out.println(pageList.getRecords());

        /**
         * 如果你仔细观察上面SQL，会发现虽然指定查询name、age，但实际上是用整个MpUserPojo对象接收的，其他都为null
         * 如果你觉得这样太难看，可以使用以下方法，让返回值是map。
         * 然而个人觉得，没太大卵用，因为实际开发还是建议返回对象，而不是map
         *
         * SELECT name,age
         * FROM mp_user
         * WHERE (name LIKE '测试1%');
         *
         * list打印：[{name=测试1, age=18}, {name=测试1, age=18}]
         */
        List<Map<String, Object>> maps = userMapper.selectMaps(pageQuery);
        System.out.println(maps);

        /**
         * 和上面的一样，只不过这次是分页。我不是很喜欢这种。
         *
         * 1.查询总数量
         * SELECT COUNT(1)
         * FROM mp_user
         * WHERE (name LIKE '测试1%');
         *
         * 2.查询分页数据
         * SELECT name,age
         * FROM mp_user
         * WHERE (name LIKE '测试1%')
         * LIMIT 1;
         *
         * list打印：[{name=测试1, age=18}]
         */
        Page<Map<String, Object>> mapPage = new Page<>();
        mapPage.setPages(1);
        mapPage.setSize(1);
        Page<Map<String, Object>> mapPageList = userMapper.selectMapsPage(mapPage, pageQuery);
        System.out.println(mapPageList.getRecords());

        /**
         * COUNT方法
         *
         * SELECT COUNT( 1 )
         * FROM mp_user
         * WHERE (name = '测试1');
         */
        QueryWrapper<MpUserPojo> countQuery = new QueryWrapper<>();
        countQuery.eq("name", "测试1");
        Integer count = userMapper.selectCount(countQuery);
        System.out.println(count);

        /**
         * 查询条件除了用QueryWrapper构造以外，还可以用Map，不过Map构造的条件都是等值条件，而queryWrapper可以使用like等
         * 就用QueryWrapper即可，太多选择反而显得凌乱，不推荐
         */
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("name", "测试1");
        List<MpUserPojo> result = userMapper.selectByMap(queryMap);
        System.out.println(result);
    }

    /**
     * 只更新设置的字段，类似于updateByPrimaryKeySelective()
     * INSERT INTO mp_user ( id, name, age, user_type ) VALUES ( 1, '测试1', 18, 1 );
     * UPDATE mp_user SET name='修改后的值' WHERE id=1;
     */
    @Test
    public void testUpdateById() {
        // 先插入
        MpUserPojo mpUserPojo = new MpUserPojo();
        mpUserPojo.setName("测试1");
        mpUserPojo.setAge(18);
        mpUserPojo.setUserType(1);
        userMapper.insert(mpUserPojo);

        // 修改
        MpUserPojo updatePojo = new MpUserPojo();
        updatePojo.setId(mpUserPojo.getId());
        updatePojo.setName("修改后的值");
        userMapper.updateById(updatePojo);
    }

    /**
     * 即使传的条件为null也不会导致条件消失，能更有效防止条件失效导致的全表修改
     * INSERT INTO mp_user ( id, name, age, user_type ) VALUES ( 1, '测试1', 18, 1 );
     * UPDATE mp_user SET name='修改后的值' WHERE (user_type = null);
     */
    @Test
    public void testUpdate() {
        // 先插入
        MpUserPojo mpUserPojo = new MpUserPojo();
        mpUserPojo.setName("测试2");
        mpUserPojo.setAge(18);
        mpUserPojo.setUserType(1);
        userMapper.insert(mpUserPojo);

        // 修改
        MpUserPojo updatePojo = new MpUserPojo();
        updatePojo.setId(mpUserPojo.getId());
        updatePojo.setName("修改后的值");
        UpdateWrapper<MpUserPojo> updateWrapper = new UpdateWrapper<>();
        // 即使条件为null也不会更新全表。但是！！！如果Wrapper没有设置任何条件，仍然会更新全表！
        //        updateWrapper.eq("user_type", null);
        userMapper.update(updatePojo, updateWrapper);
    }

    /**
     * 默认都是物理删除，如果需要逻辑删除，请在deleted上加@TableLogic
     */
    @Test
    public void testDelete() {
        /**
         * 根据主键删除（物理删除）
         * DELETE
         * FROM mp_user
         * WHERE id=1;
         */
        userMapper.deleteById(1L);

        /**
         * 批量删除（物理删除），如果ids.size()==0，会报错，而不是像通用Mapper一样删除全部
         * DELETE
         * FROM mp_user
         * WHERE id IN ( );
         */
        userMapper.deleteBatchIds(Arrays.asList(1L, 2L));

        /**
         * 条件删除（物理删除）
         * DELETE
         * FROM mp_user
         * WHERE (age = null);
         * 即使条件为null，也不会像通用Mapper一样忽略条件导致删除全部（age=null是无效的，age is NULL才会生效）
         * 注意，如果你不给queryWrapper设置任何条件，仍然会删除全表！！！！
         */
        QueryWrapper<MpUserPojo> queryWrapper = new QueryWrapper<>();
        //        queryWrapper.eq("age", null);
        userMapper.delete(queryWrapper);

        /**
         * map构造条件，不说了
         */
        //        userMapper.deleteByMap()
    }

    /**
     * 乐观锁
     * <p>
     * 支持乐观锁分两步：
     * 1.配置乐观锁插件 {@link com.bravo.demo.config.MybatisPlusConfig}
     * 2.DO字段上加@Version
     * <p>
     * UPDATE mp_user SET name='测试乐观锁', version=1
     * WHERE id=1 AND version=0 AND deleted=0;
     */
    @Test
    public void testOptimisticLock() {
        MpUserPojo mpUserPojo = new MpUserPojo();
        mpUserPojo.setId(1L);
        mpUserPojo.setName("测试乐观锁");
        // 假设这条记录是刚从数据库查出来的，version=0
        mpUserPojo.setVersion(0);
        // 如果更新成功，version=version+1
        userMapper.updateById(mpUserPojo);
    }

}
