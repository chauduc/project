package com.duc.aws.project.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duc.aws.project.dto.AccountDTO;
import com.duc.aws.project.dto.AuthDTO;
import com.duc.aws.project.dto.RankingDTO;
import com.duc.aws.project.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final String ERROR = "FAIL";

	@GetMapping(value = "/api/secure/hello/{name}")
	public ResponseEntity<?> helloSecure(@PathVariable String name) {
		String result = String.format("Hello JWT, %s! (Secure)", name);
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/api/public/hello/{name}")
	public ResponseEntity<?> helloPublic(@PathVariable String name) {
		String result = String.format("Hello JWT, %s! (Public)", name);
		return ResponseEntity.ok(result);
	}
	
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
		jsonObject.put("error", ERROR);
		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
	}

	@PostMapping(value = "/api/auth")
	public ResponseEntity<?> auth(@RequestBody AuthDTO auth) {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		String passWord = auth.getPassword();
		String correctCredentials = userService.authenticate(userName, passWord);
		if (!StringUtils.isEmpty(correctCredentials)) {
			entity.put("key", correctCredentials);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping(value = "/api/score")
	public ResponseEntity<?> score(@RequestBody AuthDTO auth) {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		int score = auth.getScore();
		boolean rs = userService.updateScore(userName, score);
		if (rs) {
			entity.put("score", score);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		entity.put("error", ERROR);
		return new ResponseEntity<>(entity, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * @param auth
	 * @return
	 */
	@PostMapping(value = "/api/user")
	public ResponseEntity<?> user(@RequestBody AuthDTO auth) {
		ObjectNode entity = objectMapper.createObjectNode();
		String userName = auth.getUsername();
		AccountDTO dto = userService.findUserByUserName(userName);
		if (null != dto) {
			entity.put("UserName", dto.getUserName());
			entity.put("Score", dto.getScore());
			entity.put("Rank", dto.getRanking());
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	@PostMapping(value = "/api/ranking")
	public ResponseEntity<?> ranking(@RequestBody RankingDTO ranking) {
		List<ObjectNode> listAccount = new ArrayList<ObjectNode>();
		int fromRank = ranking.getFromRank();
		int toRank =ranking.getToRank();
		List<AccountDTO> listDTO = userService.findUserByRanking(fromRank, toRank);
		if (!CollectionUtils.isEmpty(listDTO)) {
			int rank = fromRank;
			for (AccountDTO accountDTO : listDTO) {
				ObjectNode entity = objectMapper.createObjectNode();
				entity.put("UserName", accountDTO.getUserName());
				entity.put("Score", accountDTO.getScore());
				entity.put("Rank",rank);
				listAccount.add(entity);
				rank++;
			}
			return new ResponseEntity<>(listAccount, HttpStatus.OK);
		}
		return new ResponseEntity<>(listAccount, HttpStatus.OK);
	}
	

}
