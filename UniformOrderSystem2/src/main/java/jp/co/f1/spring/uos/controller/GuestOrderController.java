package jp.co.f1.spring.uos.controller;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.f1.spring.uos.entity.Order;

@Controller
public class GuestOrderController {

	private static final String CART_SESSION_NAME = "cart";

	private static final String GUEST_ORDER_SESSION_NAME = "guestOrder";

	/*
	
	 ゲスト購入者情報入力画面を表示.
	 */
	@GetMapping("/guest/order")
	public ModelAndView showGuestOrder(
			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {

		/*
		
		 セッションからカートを取得.
		 */
		Object cart = session.getAttribute(
				CART_SESSION_NAME);

		/*
		
		 カートがない、または空の場合.
		 */
		if (!(cart instanceof Map<?, ?> cartMap)
				|| cartMap.isEmpty()) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"カートに商品が入っていません。");

			mav.setViewName("redirect:/list");

			return mav;
		}

		/*
		
		 入力途中のゲスト情報が.
		 セッションに残っているか確認.
		 */
		Order guestOrder = (Order) session.getAttribute(
				GUEST_ORDER_SESSION_NAME);

		/*
		
		 まだゲスト情報がない場合.
		 */
		if (guestOrder == null) {

			guestOrder = new Order();
		}

		/*
		
		 HTMLの.
		 th:object="${guestOrder}".
		 へ渡す.
		 */
		mav.addObject(
				"guestOrder",
				guestOrder);

		mav.setViewName("GestOrderlist");

		return mav;
	}

	/*
	
	 ゲスト購入者情報を受け取る.
	 */
	@PostMapping("/guest/order/confirm")
	public ModelAndView confirmGuestOrder(
			@ModelAttribute("guestOrder") Order guestOrder,

			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {

		/*
		
		カートが残っているか確認.
		 */
		Object cart = session.getAttribute(
				CART_SESSION_NAME);

		if (!(cart instanceof Map<?, ?> cartMap)
				|| cartMap.isEmpty()) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"カートに商品が入っていません。");

			mav.setViewName("redirect:/list");

			return mav;
		}

		/*
		
		 入力値を整える.
		 */
		String email = normalize(
				guestOrder.getEmail());

		String name = normalize(
				guestOrder.getName());

		String address = normalize(
				guestOrder.getAddress());

		String remarks = normalize(
				guestOrder.getRemarks());

		guestOrder.setEmail(email);
		guestOrder.setName(name);
		guestOrder.setAddress(address);
		guestOrder.setRemarks(remarks);

		/*
		
		◦ 必須項目の確認.
		 */
		if (email.isBlank()
				|| name.isBlank()
				|| address.isBlank()) {

			mav.addObject(
					"errorMessage",
					"メールアドレス、名前、住所を"
							+ "すべて入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		/*
		
		◦ メールアドレスの簡単な確認.
		 */
		if (!email.contains("@")) {

			mav.addObject(
					"errorMessage",
					"メールアドレスを"
							+ "正しく入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		/*
		
		◦ 文字数の確認.
		 */
		if (email.length() > 100) {

			mav.addObject(
					"errorMessage",
					"メールアドレスは100文字以内で"
							+ "入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		if (name.length() > 100) {

			mav.addObject(
					"errorMessage",
					"名前は100文字以内で"
							+ "入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		if (address.length() > 255) {

			mav.addObject(
					"errorMessage",
					"住所は255文字以内で"
							+ "入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		if (remarks.length() > 200) {

			mav.addObject(
					"errorMessage",
					"備考は200文字以内で"
							+ "入力してください。");

			mav.setViewName("GestOrderlist");

			return mav;
		}

		/*
		
		◦ ゲストなのでユーザーIDはnull.
		 */
		guestOrder.setUserid(null);

		/*
		
		◦ 注文確定処理でも使うため.
		◦ セッションへ一時保存.
		 */
		session.setAttribute(
				GUEST_ORDER_SESSION_NAME,
				guestOrder);

		/*
		
		◦ 購入内容確認画面へ移動.
		 */
		mav.setViewName(
				"redirect:/buy/confirm");

		return mav;
	}

	/*
	
	◦ nullを空文字へ変換して.
	◦ 前後の空白を削除.
	 */
	private String normalize(
			String value) {

		if (value == null) {
			return "";
		}

		return value.trim();
	}
}