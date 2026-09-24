package jp.co.f1.spring.uos.controller;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.CheckUser;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UserRepository;

public class LoginController {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserRepository userinfo;

	@Autowired
	private User userDao;

	//セッション使用
	@Autowired
	private HttpSession session;

	/* login
	 *  
	 */
	@GetMapping("/login")
	public ModelAndView loginForm(ModelAndView mav, HttpServletRequest request) {

		// クッキーを取得
		Cookie[] cookies = request.getCookies();
		String strUserid = null;
		String strPassword = null;

		//クッキーが存在するかチェック
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("strUserid".equals(cookie.getName())) {
					strUserid = cookie.getValue();
				} else if ("strPassword".equals(cookie.getName())) {
					strPassword = cookie.getValue();
				}
			}
		}

		// Modelにクッキーの値を追加
		mav.addObject("strUserid", strUserid);
		mav.addObject("strPassword", strPassword);
		// mav.addObject("strAuthority", strAuthority);

		// 画面に出力するViewを指定
		mav.setViewName("view/login");

		// ModelとView情報を返す
		return mav;
	}

	/*
	 * login   POST
	 */
	@PostMapping("/login")
	public ModelAndView loginPost(@ModelAttribute User user, BindingResult result,
			ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {

		//入力されたユーザーIDとパスワードでユーザー,名前、アドレス、email検索
		Optional<User> optionalUser = userinfo.findByUseridAndPassword(user.getUsreid(), user.getPassword());

		//該当ユーザーが存在しない場合
		if (!(optionalUser.isPresent())) {
			//エラーメッセージ
			mav.addObject("errorMessage", "入力内容に誤りがあります。");
			// 画面に出力するViewを指定
			mav.setViewName("view/login");
			//ModelとView情報を返す
			return mav;
		}

		//クッキーの登録
		//ユーザーID
		Cookie useridCookie = new Cookie("strUserid", user.getUsreid());
		useridCookie.setMaxAge(60 * 60 * 24 * 5); // 5日（秒）に設定
		response.addCookie(useridCookie);
		//パスワード
		Cookie passCookie = new Cookie("strPassword", user.getPassword());
		passCookie.setMaxAge(60 * 60 * 24 * 5); // 5日（秒）に設定
		response.addCookie(passCookie);

		//現在ログインしているユーザー情報をセッションに登録
		user = optionalUser.get();
		session.setAttribute("user", user);

		//パスワード変更の際に使うものをセッションに登録
		CheckUser checkUser = new CheckUser();
		checkUser.setUserid(user.getUsreid());
		checkUser.setOldPassword(user.getPassword());
		checkUser.setEmail(user.getEmail());
		checkUser.setAuthority(user.getAuthority());
		session.setAttribute("checkUser", checkUser);

		//リダイレクト先を指定
		mav = new ModelAndView("redirect:/menu");
		//ModelとView情報を返す
		return mav;
	}
}
