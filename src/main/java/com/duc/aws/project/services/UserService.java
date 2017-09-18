package com.duc.aws.project.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.duc.aws.project.model.User;

@Service
public class UserService {

	private static Map<String, User> users = new HashMap<String, User>();
    static {
        users.put(
                "user1", User
                            .builder()
                            .userName("user1")
                            .passWord("123") // Never do this!
                            .email("user1@romania.com")
                            .role(User.ROLE_ADMIN)
                            .isActivated(true)
                            .build()
        );
    }

    public User findUserByUserName(String userName)
    {
    	// After get user check in db
        return users.get(userName);
    }


    public Boolean authenticate(String userName, String passWord)
    {
    	//After using db check
        User user = findUserByUserName(userName);
        if (null!=user)
        {
            return user.getPassWord().equals(passWord);
        }
        return false;
    }
}
