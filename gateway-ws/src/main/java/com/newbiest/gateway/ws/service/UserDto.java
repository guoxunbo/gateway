package com.newbiest.gateway.ws.service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoxunbo
 * @date 2020-08-29 12:28
 */
@Data
public class UserDto implements Serializable {

    private String name;

    private String password;
}
