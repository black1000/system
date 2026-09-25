package jp.co.f1.spring.uos.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.repository.OrderDetailRepository;
import jp.co.f1.spring.uos.repository.OrderRepository;

@Controller
public class OrderDetail {

	private static final String ADMIN_AUTHORITY = "1";

	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;

	/*
	 *コンストラクタ.
	 */
	public OrderDetail(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {

		this.orderRepository = orderRepository;

		this.orderDetailRepository = orderDetailRepository;
	}

	/*
	 *管理者用の注文詳細画面.
	 */
	@GetMapping("/admin/orders/{orderno}")
	public ModelAndView showOrderDetail(
			@PathVariable("orderno") String orderno,

			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {

		/*
		 *管理者権限を確認.
		 */
		String authority = (String) session.getAttribute(
				"authority");

		if (!ADMIN_AUTHORITY.equals(authority)) {

			mav.setViewName(
					"redirect:/admin/login");

			return mav;
		}

		/*
		 *注文番号から注文情報を取得.
		 */
		Order order = orderRepository
				.findById(orderno)
				.orElse(null);

		/*
		 *注文が存在しない場合.
		 */
		if (order == null) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"指定された注文が"
							+ "見つかりませんでした。");

			mav.setViewName(
					"redirect:/admin/orders");

			return mav;
		}

		/*
		 *注文番号から注文明細を取得.
		 */
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderno(orderno);

		/*
		 *adminBuy.htmlへ渡す.
		 */
		mav.addObject(
				"order",
				order);

		mav.addObject(
				"orderDetails",
				orderDetails);

		/*
		 *templates/view/adminBuy.html.
		 */
		mav.setViewName(
				"view/adminBuy");

		return mav;
	}
}