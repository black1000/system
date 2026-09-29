package jp.co.f1.spring.uos.controller;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.repository.UserRepository;


@Controller
public class MemberInfoController {
	
	
	// EntityManager自動インスタンス化
	@PersistenceContext
	private EntityManager entityManager;

	// DAO自動インスタンス化
	@Autowired
	private UserDAO userDao;

	@PostConstruct
	public void init() {
		userDao = new UserDAO(entityManager);
	}

	// Repositoryインターフェースを自動インスタンス化
	@Autowired
	private UserRepository userinfo;

	@Autowired
	private HttpSession session;
	
	
	
	
	
	

}
