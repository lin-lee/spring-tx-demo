package com.demo.dao;

import com.demo.UserEntity;

/**
 * 
 * @author lin
 *
 */
public interface IUserDAO {

    public int save(UserEntity userEntity) throws Exception;
    
    public UserEntity selectById(int id) throws Exception;
}