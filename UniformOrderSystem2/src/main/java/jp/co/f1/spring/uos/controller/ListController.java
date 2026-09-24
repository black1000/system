package jp.co.f1.spring.uos.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UniformRepository;



public class ListController {

	// Repositoryインターフェースを自動インスタンス化
	@Autowired
	private UniformRepository productinfo;

	@PersistenceContext
	private EntityManager entityManager;


	@Autowired
	private HttpSession session;
	
	/*
	 * 商品一覧へアクセス
	 */
	
	@GetMapping("/list")
	public ModelAndView list(ModelAndView mav) {
		
	//セッションからユーザー情報取得
	User user = (User) session.getAttribute("user");
	
	//ユーザー情報をモデルに格納
	mav.addObject("user", user);	
	
	
	//商品情報の取得
	// Uniformテーブルから商品情報全件取得
	Iterable<Uniform> uniformList = productinfo.findAll();
	
	//取得した商品データをModelに格納
	mav.addObject("uniformList", uniformList);

	// 画面に出力するViewを指定
	mav.setViewName("view/menu");
	
	// ModelとView情報を返す
	return mav;
		}

	}
