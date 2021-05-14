package com.zpyl.mapper;

import com.zpyl.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

public interface UserMapper {

  @Select("select * from user")
  List<User> findAll();

  @Select("select * from user where id=#{id}")
  User findById(@Param("id") Integer id);

  @Update("update user set username=#{username},birthday=#{birthday},sex=#{sex},address=#{address} where id=#{id}")
  void update(@Param("user")User user);

  @Update("update user set username=#{arg1} where id=#{arg0}")
  void setName(Integer id,String username);

}
