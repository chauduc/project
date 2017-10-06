package com.duc.aws.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.util.Base64;
import com.duc.aws.project.dao.AccountDAO;
import com.duc.aws.project.dao.RankingDAO;
import com.duc.aws.project.dto.AccountDTO;
import com.duc.aws.project.dto.RankingDTO;
import com.duc.aws.project.model.JwtUser;

@Service
public class UserService {

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	RankingDAO rankingDAO;

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

		List<RankingDTO> listAccount = rankingDAO.getRankingByRange(fromRank, toRank);

		List<AccountDTO> accountList = accountDAO.findAccountByRank(listAccount);
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

	/**
	 * @param userName
	 * @param score
	 * @return
	 */
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
