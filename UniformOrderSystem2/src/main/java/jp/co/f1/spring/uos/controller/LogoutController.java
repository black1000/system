package jp.co.f1.spring.uos.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LogoutController {
	
	//セッション使用
		@Autowired
		private HttpSession session;

	@PostMapping("/logout")
    public ModelAndView logoutForm(ModelAndView mav,HttpServletRequest request) {

    	//セッション情報をクリアする
    	session.invalidate();

    	//リダイレクト先を指定
    	mav = new ModelAndView("redirect:/login");
    	// ModelとView情報を返す
    	return mav;
	}
}