package jp.co.f1.spring.uos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import jp.co.f1.spring.uos.dao.UniformDAO;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.repository.UniformRepository;

import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.dao.UserDAO;
import jp.co.f1.spring.uos.repository.UserRepository;
import jp.co.f1.spring.uos.repository.OrderDetailRepository;
import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.entity.CheckUser;
import jp.co.f1.spring.uos.dao.OrderDAO;
import jp.co.f1.spring.uos.dao.OrderDetailDAO;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;


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
		private OrderDetailRepository orderdetail;
		
		@Autowired
		private HttpSession session;
	
		
		
		/*
		 * 「/orderHistory」にアクセスがあった場合
		 *  
		 */
		@GetMapping("/orderHistory")
		public ModelAndView showOrderedItem(ModelAndView mav) {

			User user = (User) session.getAttribute("user");

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
			ArrayList<OrderDetail> orderDetailList  =  new ArrayList<OrderDetail>();
			for(Order order : orderedList) {
								
						
				orderDetailList.add((OrderDetail) orderdetail.findByOrderno(order.getOrderno()));
			}
			
			
		
			
			mav.addObject("orderedList", orderedList);
			mav.addObject("orderDetailList", orderDetailList);
			mav.setViewName("view/orderHistory");
			return mav;
		}
	
}
