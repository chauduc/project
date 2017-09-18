package com.duc.aws.project.repositories;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.duc.aws.project.dynamodb.model.Users;

@EnableScan
public interface UsersRepository extends CrudRepository<Users, String> {

	List<Users> findByUserId(String userId);
}
