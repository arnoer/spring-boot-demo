package com.bravo;

import com.bravo.dao.MpUserMapper;
import com.bravo.pojo.MpUserPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试手写SQL与接口方法是否会自动映射驼峰
 * <p>
 * 在通用Mapper中，接口的驼峰和手写SQL的驼峰要分别开启，MyBatis-Plus是一起的
 *
 * @author qiyu
 * @date 2020-09-13 18:16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperXMLTest {

    @Autowired
    private MpUserMapper userMapper;

    @Test
    public void create() {
        MpUserPojo mpUserPojo = new MpUserPojo();
        mpUserPojo.setName("bravo");
        mpUserPojo.setUserType(1);
        mpUserPojo.setAge(18);
        userMapper.insert(mpUserPojo);
    }

    @Test
    public void test() {
        // 手写SQL
        MpUserPojo userPojo = userMapper.myGetById(1305079897975795715L);
        System.out.println(userPojo);
        // 接口方法
        MpUserPojo mpUserPojo = userMapper.selectById(1305079897975795715L);
        System.out.println(mpUserPojo);
    }

}
