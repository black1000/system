package jp.co.f1.spring.uos.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
			for(Cookie cookie : cookies) {
				if ("strUserid".equals(cookie.getName())) { 
					strUserid = cookie.getValue();
				} else if("strPassword".equals(cookie.getName())) {
					strPassword = 
				}
			}
		}
	}
}
