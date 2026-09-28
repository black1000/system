package jp.co.f1.spring.uos.controller;

import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UniformRepository;
import jp.co.f1.spring.uos.repository.UserRepository;


@Controller
public class DeleteController {


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
	private UniformRepository uniforminfo;

	@Autowired
	private HttpSession session;
	
	private  User user = new User();
 
	
	
	@GetMapping("/delete")
	public ModelAndView delete(@RequestParam(required = true) String productno, ModelAndView mav) {

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

		mav.addObject("user", user);


		Optional<Uniform> optionalUniform = uniforminfo.findByProductno(productno);

		// エラーチェック
		if (optionalUniform.isEmpty()) {

			mav.addObject("errorMessage", "削除対象の商品が存在しないため、削除処理は行えませんでした。");

			mav.setViewName("view/error");
			return mav;

		}

		uniforminfo.deleteById(productno);

		mav = new ModelAndView("redirect:/list");

		return mav;
	}
}
