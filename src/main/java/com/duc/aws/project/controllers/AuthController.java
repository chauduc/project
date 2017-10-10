package com.duc.aws.project.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duc.aws.project.dto.AccountDTO;
import com.duc.aws.project.dto.AuthDTO;
import com.duc.aws.project.dto.RankingDTO;
import com.duc.aws.project.model.JwtUser;
import com.duc.aws.project.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private HttpServletRequest request;

	private static final String ERROR = "FAIL";

	private static final String USED_USERNAME = "UserName is used";
	
	/**
	 * @param auth
	 * @return
	 */
	@GetMapping(value = "/api")
	public ResponseEntity<?> healthyCheck() {
		ObjectNode jsonObject = objectMapper.createObjectNode();
		jsonObject.put("healthCheck", "Deploy By GitHub");
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	/**
	 * @param auth
	 * @return
	 */
	@PostMapping(value = "/api/signup")
	public ResponseEntity<?> signup(@RequestBody AuthDTO auth) {
		ObjectNode jsonObject = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		String passWord = auth.getPassword();
		String correctCredentials = userService.signUp(userName, passWord);
		if (!StringUtils.isEmpty(correctCredentials)) {
			jsonObject.put("key", correctCredentials);
			return new ResponseEntity<>(jsonObject, HttpStatus.OK);
		}
		jsonObject.put("error", USED_USERNAME);
		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param auth
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/api/signin")
	public ResponseEntity<?> auth(@RequestBody AuthDTO auth) throws Exception {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		String passWord = auth.getPassword();
		String correctCredentials = userService.authenticate(userName, passWord);
		if (!StringUtils.isEmpty(correctCredentials)) {
			entity.put("key", correctCredentials);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * @param auth
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/api/secure/score")
	public ResponseEntity<?> score(@RequestBody AuthDTO auth) throws Exception {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		JwtUser jtwUser = (JwtUser) request.getAttribute("jwtUser");
		// 他のユーザーのスコアを更新できない。
		if (!jtwUser.getUserName().equals(userName)) {
			entity.put("error", ERROR);
			return new ResponseEntity<>(entity, HttpStatus.UNAUTHORIZED);
		}
		// 登録していないユーザーはスコアを登録できない。
		AccountDTO dto = userService.findUserByUserName(userName);
		if (null == dto) {
			entity.put("error", ERROR);
			return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
		}
		int score = auth.getScore();
		boolean rs = userService.updateScore(userName, score);
		if (rs) {
			entity.put("username", userName);
			entity.put("score", score);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param auth
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/api/secure/user")
	public ResponseEntity<?> user(@RequestBody AuthDTO auth) throws Exception {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		AccountDTO dto = userService.findUserByUserName(userName);
		if (null != dto) {
			entity.put("username", dto.getUserName());
			entity.put("score", dto.getScore());
			entity.put("rank", dto.getRanking());
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param ranking
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/api/secure/ranking")
	public ResponseEntity<?> ranking(@RequestBody RankingDTO ranking) throws Exception {
		List<ObjectNode> listAccount = new ArrayList<ObjectNode>();
		int fromRank = ranking.getFromRank();
		int toRank = ranking.getToRank();
		List<AccountDTO> listDTO = userService.findUserByRanking(fromRank, toRank);
		if (!CollectionUtils.isEmpty(listDTO)) {
			for (AccountDTO accountDTO : listDTO) {
				ObjectNode entity = objectMapper.createObjectNode();
				entity.put("username", accountDTO.getUserName());
				entity.put("score", accountDTO.getScore());
				entity.put("rank", accountDTO.getRanking());
				listAccount.add(entity);
			}
			return new ResponseEntity<>(listAccount, HttpStatus.OK);
		}
		ObjectNode entity = objectMapper.createObjectNode();
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<?>  handleException() {
		ObjectNode entity = objectMapper.createObjectNode();
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}

}
