package jp.co.f1.spring.uos.controller;

import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UserRepository;

@Controller
public class MenuController {
	

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
	
	private  User user = new User();
 
	
	/*
	 * 「menu」にアクセスがあった場合
	 */
	@GetMapping("/menu")
	public ModelAndView menu(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {

		// セッションを受け取る
		String userid =  (String)session.getAttribute("loginUserId");
	   Optional <User>	optionalUser = userinfo. findByUserid(userid);

		// userがない(セッション切れ)の時
		if (optionalUser.isEmpty()) {

			mav.addObject("errorMessage", "セッション切れの為、商品一覧に戻ります。");

			mav.setViewName("view/error");
			return mav;

		}

		mav.addObject("user", optionalUser);
		user = optionalUser.get();

		String auth = null;
		if (user.getAuthority().equals("1")) {

			auth = "一般ユーザー";
			mav.addObject("userid", user.getUserid());
			mav.addObject("auth", auth);
			session.setAttribute("user", user);
			// 画面に出力するViewを指定
			mav.setViewName("view/adminMenu");
			
			
		} else if (user.getAuthority().equals("2")) {
			auth = "管理者";
			mav.addObject("userid", user.getUserid());
			mav.addObject("auth", auth);
			session.setAttribute("user", user);
			// 画面に出力するViewを指定
			mav.setViewName("view/membermenu");
		}



		// ModelとView情報を返す
		return mav;
	}
	/**
	 * Exception発生時の処理メソッド.
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView ExceptionHandler(Exception e) {
		ModelAndView mav = new ModelAndView();

	
		mav.addObject("errorMessage", "エラー内容：" + e.getMessage());
		// 画面に出力するViewを指定
		mav.setViewName("view/error");
		// ModelとView情報を返す
		return mav;
	}
}
