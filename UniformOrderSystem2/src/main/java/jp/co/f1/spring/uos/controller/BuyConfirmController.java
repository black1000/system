package jp.co.f1.spring.uos.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.repository.UniformRepository;

public class BuyConfirmController {

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
	 * 注文履歴
	 */

	@GetMapping("buyconfirm")
	public ModelAndView buyconfirm(HttpServletRequest request, ModelAndView mav) {

		//ユーザー情報の取得
		//セッションからユーザー情報取得
		User user = (User) session.getAttribute("user");

		//セッション切れの場合
		if (user == null) {
			//エラーメッセージ
			mav.addObject("errorMessage", "セッション切れの為、注文履歴の確認は出来ません。");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			// 画面に出力するViewを指定
			mav.setViewName("view/error");
			// ModelとView情報を返す
			return mav;
		}

		//セッションに登録されているユーザーIDが購入したオーダー履歴を取得
		Iterable<Order> orderedList = orderinfo.findByUserid(user.getUsreid());

		//取得したデータを渡す
		mav.addObject("orderedList", orderedList);

		//画面に出力するViewを指定
		mav.setViewName("view/buyconfirm");
		//ModelとView情報を返す
		return mav;
	}
}
