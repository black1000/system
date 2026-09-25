package jp.co.f1.spring.uos.controller;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.repository.UserRepository;
import jp.co.f1.spring.uos.entity.User;

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

	
	/*
	 * 「menu」にアクセスがあった場合
	 */
	@GetMapping("/menu")
	public ModelAndView menu(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {

		// セッションを受け取る
		User user = (User) session.getAttribute("user");

		// userがない(セッション切れ)の時
		if (user == null) {

			mav.addObject("errorMessage", "セッション切れの為、商品一覧に戻ります。");
			mav.addObject("cmd", "list");
			mav.addObject("next", "[商品一覧へ]");
			mav.setViewName("view/error");
			return mav;

		}

		mav.addObject("user", user);

		String auth = null;
		if (user.getAuthority().equals("1")) {

			auth = "一般ユーザー";
			mav.addObject("userid", user.getUserid());
			mav.addObject("auth", auth);

			// 画面に出力するViewを指定
			mav.setViewName("view/membermenu");
			
		} else if (user.getAuthority().equals("2")) {
			auth = "管理者";
			mav.addObject("userid", user.getUserid());
			mav.addObject("auth", auth);

			// 画面に出力するViewを指定
			mav.setViewName("view/adminMenu");
		}



		// ModelとView情報を返す
		return mav;
	}

}
