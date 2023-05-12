package com.xkcoding.spel.controller;

import com.xkcoding.spel.annotation.OperationDescAnnotation;
import com.xkcoding.spel.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangxinyu
 * @date 2023/5/11
 **/
@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @PostMapping("add")
    @OperationDescAnnotation(operationTarget = "User", value = "#user.id", operationTargetId =
        "#baseRequest.data.id", operationType = "新建")
    public User insert(User user) {
        return user;
    }

    @PostMapping("delete")
    @OperationDescAnnotation(operationTarget = "User", value = "删除", operationTargetId =
        "#ids", operationType = "删除")
    public Boolean delete(List<Integer> ids) {
        return true;
    }
}
