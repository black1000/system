package jp.co.f1.spring.uos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;


import java.util.ArrayList;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import jp.co.f1.spring.uos.dao.UniformDAO;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.repository.UniformRepository;


import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.repository.UserRepository;


import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.dao.OrderDAO;
import jp.co.f1.spring.uos.entity.Order;


@Controller
public class MemberChangeController {
	
	
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
