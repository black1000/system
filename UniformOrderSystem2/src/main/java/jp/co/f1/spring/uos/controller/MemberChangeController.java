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
import jp.co.f1.spring.uos.entity.CheckUser;
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
	
	/*
	 * 「memberChange」へGETアクセスがあった場合
	 */
	@GetMapping("/memberChange")
	public ModelAndView memberChange(ModelAndView mav) {
		User user = (User) session.getAttribute("user");

		// セッション切れの時のエラー処理
		if (user == null) {

			mav.addObject("errorMessage", "セッション切れの為、再度ログインしてください。 ");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			mav.setViewName("view/error");
			return mav;

		}
		
		// セッションから読み取ったuser情報を検索
		Optional<User> optionalUser = userinfo.findByUserid(user.getUserid());
		
		
	
		
		User oldUser = optionalUser.get();
		mav.addObject("oldUser", oldUser);
		mav.addObject("userid", user.getUserid());

		mav.setViewName("view/memberChange");

		return mav;
	}

	/*
	 * 「menberChange」へPost送信でアクセスがあった場合
	 */
	@PostMapping("/memberChange")
	public ModelAndView postUpdateUser(@ModelAttribute @Validated CheckUser checkUser, BindingResult result,
			HttpServletRequest request, ModelAndView mav) {

		User user = (User) session.getAttribute("user");
		
		// セッション切れのエラー処理
		if (user == null) {

			mav.addObject("errorMessage", "セッション切れの為、再度ログインしてください。 ");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			mav.setViewName("view/error");
			return mav;

		}

		checkUser = (CheckUser) session.getAttribute("checkUser");

		// ユーザーを検索
		Optional<User> optionalUser = userinfo.findByUserid(checkUser.getUserid());


		// ユーザーがいた場合はoldUserに格納
		User oldUser = optionalUser.get();

		// 入力値チェック
		if (result.hasErrors()) {
			
			

			if (checkUser.getNewPassword() == "") {
				mav.addObject("passwordError", "パスワードを入力してください");

			}

			mav.addObject("message", "入力内容に誤りがあります");
			mav.addObject("oldUser", oldUser); // 古い値も渡す必要あり
			mav.addObject("checkUser", checkUser);
			mav.setViewName("view/memberChange");
			return mav;

		}

		if (!checkUser.getNewPassword().equals(checkUser.getConfirmPassword())) {
			mav.addObject("message", "新パスワードと確認パスワードが合っていません");
			mav.addObject("oldUser", oldUser); // 古い値も渡す必要あり
			mav.addObject("checkUser", checkUser);
			mav.setViewName("view/memberChange");
			return mav;

		}

		// エラーがなければoldUserに登録
		oldUser.setUserid(checkUser.getUserid());
		oldUser.setEmail(checkUser.getEmail());
		oldUser.setPassword(checkUser.getNewPassword());
		oldUser.setName(checkUser.getName());
		oldUser.setAddress(checkUser.getAddress());

		userinfo.saveAndFlush(oldUser);

		mav = new ModelAndView("redirect:/memberChange");

		return mav;

	}
	

}
