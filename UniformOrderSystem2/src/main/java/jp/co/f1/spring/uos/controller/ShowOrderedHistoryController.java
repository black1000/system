package jp.co.f1.spring.uos.controller;

<<<<<<< HEAD
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
=======
import java.util.ArrayList;
import java.util.Optional;
>>>>>>> 240b570c31286049940fd9591b1eb9abccb17f86

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.dao.OrderDAO;
import jp.co.f1.spring.uos.dao.OrderDetailDAO;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.OrderDetailRepository;
import jp.co.f1.spring.uos.repository.OrderRepository;
<<<<<<< HEAD
=======
import jp.co.f1.spring.uos.repository.UserRepository;
>>>>>>> 240b570c31286049940fd9591b1eb9abccb17f86


@Controller
public class ShowOrderedHistoryController {

	// EntityManager自動インスタンス化
	@PersistenceContext
	private EntityManager entityManager;

	// DAO自動インスタンス化
	@Autowired
	private OrderDAO orderDao;

	@Autowired
	private OrderDetailDAO orderDetailDao;

	// Repositoryインターフェースを自動インスタンス化
	@Autowired
	private OrderRepository orderinfo;
	@Autowired
	private UserRepository userinfo;

	@Autowired
	private OrderDetailRepository orderdetail;

<<<<<<< HEAD
			// ユーザーごとの注文を検索
			Iterable<Order> orderedList = orderinfo.findByUserid(user.getUserid());
			
			// ordernoで紐づけられるorderdetailを取り出す
			Map<String, List<OrderDetail>> orderDetailMap = new HashMap<>();

			for (Order order : orderedList) {

			    List<OrderDetail> details =
			        (List<OrderDetail>) orderdetail.findById_Orderno(order.getOrderno());

			    orderDetailMap.put(
			        order.getOrderno(),
			        details
			    );
			}
			
		
			
			mav.addObject("orderedList", orderedList);
			mav.addObject("orderDetailMap", orderDetailMap);
			mav.setViewName("view/orderHistory");
=======
	@Autowired
	private HttpSession session;
	private User user = new User();

	/*
	 * 「/orderHistory」にアクセスがあった場合
	 *  
	 */
	@GetMapping("/orderHistory")
	public ModelAndView showOrderedItem(ModelAndView mav) {

		// User user = (User) session.getAttribute("user");

		// セッションを受け取る
		String userid = (String) session.getAttribute("loginUserId");
		Optional<User> optionalUser = userinfo.findByUserid(userid);

		// userがない(セッション切れ)の時
		if (optionalUser.isEmpty()) {

			mav.addObject("errorMessage", "セッション切れの為、商品一覧に戻ります。");

			mav.setViewName("view/error");
>>>>>>> 240b570c31286049940fd9591b1eb9abccb17f86
			return mav;

		}

		mav.addObject("user", optionalUser);
		user = optionalUser.get();

		// セッション切れのエラー処理
		if (user == null) {
			mav.addObject("errorMessage", "セッション切れのため、注文履歴を確認できません");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			// 画面に出力するViewを指定
			mav.setViewName("view/error");

			return mav;
		}

		// ユーザーごとの注文を検索
		Iterable<Order> orderedList = orderinfo.findByUserid(user.getUserid());

		// ordernoで紐づけられるorderdetailを取り出す
		ArrayList<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
		for (Order order : orderedList) {

			orderDetailList.add((OrderDetail) orderdetail.findByOrderno(order.getOrderno()));
		}

		mav.addObject("orderedList", orderedList);
		mav.addObject("orderDetailList", orderDetailList);
		mav.setViewName("view/orderHistory");
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
