package com.duc.aws.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.util.Base64;
import com.duc.aws.project.dao.AccountDAO;
import com.duc.aws.project.dto.AccountDTO;
import com.duc.aws.project.model.JwtUser;

@Service
public class UserService {

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	private JwtService jwtService;

	/**
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public String authenticate(String userName, String passWord) {

		AccountDTO user = findUserByUserName(userName);
		if (null != user) {
			if (user.getPassword().equals(Base64.encodeAsString(passWord.getBytes()))) {
				JwtUser jwtUser = new JwtUser(userName);
				return jwtService.getToken(jwtUser);
			}
		}
		return "";
	}

	/**
	 * @param userName
	 * @return
	 */
	public AccountDTO findUserByUserName(String userName) {
		AccountDTO account = accountDAO.findAccountByUserName(userName);
		return account;
	}

	/**
	 * @param fromRank
	 * @param toRank
	 * @return
	 */
	public List<AccountDTO> findUserByRanking(int fromRank, int toRank) {
		
		// Call rankingDao to get UserName List
		// Pass Username list to findAccountByRank
		List<AccountDTO> accountList = accountDAO.findAccountByRank(null);
		return accountList;
	}

	/**
	 * @param userName
	 * @param password
	 * @return
	 */
	public String signUp(String userName, String password) {
		String token = "";
		AccountDTO newAccount = new AccountDTO();
		newAccount.setUserName(userName);
		newAccount.setPassword(Base64.encodeAsString(password.getBytes()));
		if (accountDAO.registerAccount(newAccount)) {
			JwtUser jwtUser = new JwtUser(userName);
			return jwtService.getToken(jwtUser);
		}
		return token;
	}
	
	public Boolean updateScore(String userName, int score) {
		AccountDTO account = new AccountDTO();
		account.setUserName(userName);
		account.setScore(score);
		if (accountDAO.updateAccount(account)) {
			return true;
		}
		return false;
	}

}
