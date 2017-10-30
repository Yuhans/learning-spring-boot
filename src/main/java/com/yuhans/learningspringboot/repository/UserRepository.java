package com.yuhans.learningspringboot.repository;

import com.yuhans.learningspringboot.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUserName(String userName);

}
