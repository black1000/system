package jp.co.f1.spring.uos.controller;

import java.util.Optional;

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
public class InsertNewMemberController {

	// EntityManager自動インスタンス化
	@PersistenceContext
	private EntityManager entityManager;

	// DAO自動インスタンス化
	@Autowired
	private UserDAO userDao;
	
	// Repositoryインターフェースを自動インスタンス化
	@Autowired
	private UserRepository userinfo;
	
	@Autowired
	private HttpSession session;

	/*
	 * 「insertUser」にアクセスがあった場合
	 */
	@GetMapping("/insertNewUser")
	public ModelAndView insertUser(@ModelAttribute CheckUser checkUser, ModelAndView mav) {

		mav.addObject("checkUser", checkUser);
		mav.setViewName("view/insertNewUser");
		return mav;

	}

	/*
	 * 「insertUser」にPost送信でアクセスがあった場合
	 */
	@PostMapping("/insertNewUser")
	public ModelAndView postInsertUser(@ModelAttribute @Validated CheckUser checkUser, BindingResult result,
			ModelAndView mav) {

		
	
		


		Optional<User> optionalUser = userinfo.findByUserid(checkUser.getUserid());

		// エラーチェック
		if (optionalUser.isPresent()) {

			mav.addObject("message", "入力ユーザー名は既に使用済みの為、登録できません。");

			mav.setViewName("view/insertNewUser");
			return mav;
		}
		if (!checkUser.getNewPassword().equals(checkUser.getConfirmPassword())) {
			mav.addObject("message", "新パスワードと確認パスワードが合っていません");

			mav.setViewName("view/insertNewUser");
			return mav;
		}

		boolean isNewPasswordEmpty = checkUser.getNewPassword() == null || checkUser.getNewPassword().isEmpty();
		if (isNewPasswordEmpty) {
			mav.addObject("passwordError", "パスワードを入力してください");
		}
		// 入力値チェック
		if (result.hasErrors() || isNewPasswordEmpty) {
			// エラーメッセージ
			mav.addObject("message", "入力内容に誤りがあります");
			// 画面に出力するViewを指定
			mav.setViewName("view/insertNewUser");
			// ModelとView情報を返す
			return mav;
		}
		// 新しいパスワードと確認用のパスワードの確認
		if (!checkUser.getNewPassword().equals(checkUser.getConfirmPassword())) {
			// エラーメッセージ
			mav.addObject("message", "新パスワードと確認パスワードが合っていません");
			// 画面に出力するViewを指定
			mav.setViewName("view/insertNewUser");
			// ModelとView情報を返す
			return mav;
		}

		// 確認用のクラスからUserのオブジェクトに格納
		User newUser = new User();
		newUser.setUserid(checkUser.getUserid());
		newUser.setPassword(checkUser.getNewPassword());
		newUser.setEmail(checkUser.getEmail());
		newUser.setName(checkUser.getName());
		newUser.setAddress(checkUser.getAddress());
		newUser.setAuthority(checkUser.getAuthority());
	

		// 入力されたデータをDBに保存
		userinfo.saveAndFlush(newUser); // セッションの値を入れないように注意

		// Viewに渡す変数をModelに格納
		mav.addObject("message", "ユーザー登録完了しました！");

		// 画面に出力するViewを指定
		mav.setViewName("view/insertNewUser");
		// ModelとView情報を返す
		return mav;
	}

}
