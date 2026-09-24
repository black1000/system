package jp.co.f1.spring.uos.controller;

import java.util.ArrayList;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.repository.UniformRepository;

public class ShowCartController {
	
	// Repositoryインターフェースを自動インスタンス化
		@Autowired
		private UniformRepository productinfo;

		@Autowired
		private OrderRepository orderinfo;

		@PersistenceContext
		private EntityManager entityManager;


		@Autowired
		private HttpSession session;
	
	/*
	 * カート一覧
	 */
	
	@GetMapping("showcart")
	public ModelAndView showcart(HttpServletRequest request ,ModelAndView mav) {

		//セッションからカート情報を取得
		ArrayList<Uniform> uniformlist = (ArrayList<Uniform>) session.getAttribute("uniformlist");

		//削除リンクを押下した場合
		if (request.getParameter("uniformlist") != null && uniformlist != null) {

		//該当書籍検索
		int i = 0;							//カウント用変数
		Uniform uniform = new Uniform();		//uniform初期化

		//繰り返して取り出す
		while (i < uniformlist.size()) {
			uniform = uniformlist.get(i);
				if (uniform.getProductNo().equals(request.getParameter("delno"))) {
					break;
					}
				i++;
			}
			//該当する書籍がカートにある場合のみ、カートから削除
			if (i < uniformlist.size()) {
				uniformlist.remove(uniformlist.indexOf(uniform));
			}
			//リダイレクト先を指定
			mav = new ModelAndView("redirect:/showCart");
			//ModelとView情報を返す
			return mav;
			}
		

			//合計値計算用の変数
			int total = 0;

			//計算用ループ
			if (uniformlist != null) {

			//uniformリストからユニフォーム情報を取り出す
			for (Uniform uniform : uniformlist) {

			//書籍検索
			Optional<Uniform> uniformList = productinfo.findById(uniform.getProductNo());

			//合計金額を計算
			total += uniformList.get().getPrice() * orderinfo.getQuantity();
			}
		}

		//Viewに渡す変数をModelに格納
		mav.addObject("total", total);
		mav.addObject("uniformlist", uniformlist);

		//画面に出力するViewを指定
		mav.setViewName("view/showCart");
		//ModelとView情報を返す
		return mav;
	}
	}

