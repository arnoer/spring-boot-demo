package com.xkcoding.spel.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangxinyu
 * @date 2023/5/11
 **/
@Data
@Accessors(chain = true)
public class User {
    private String name;
    private Long id;
}
