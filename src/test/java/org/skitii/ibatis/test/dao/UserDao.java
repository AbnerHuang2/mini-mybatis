package org.skitii.ibatis.test.dao;

import org.skitii.ibatis.annotations.Select;
import org.skitii.ibatis.test.domain.User;

import java.util.List;

/**
 * @author skitii
 * @since 2023/11/07
 **/
public interface UserDao {
    User queryUserInfoById(Long id);

    List<User> queryUserList();

    String queryName(Long id);

    @Select("SELECT id, name, age FROM t_user WHERE name = #{name}")
    List<User> queryByName(String name);

    int insertUser(User user);

    List<User> dynamicQueryByUserCondition(User user);

}
