package jp.co.f1.spring.uos.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestOrderController {

	@GetMapping("/guest/order")
	public String showGuestOrder(
			HttpSession session,
			Model model) {

		Object cart = session.getAttribute("cart");

		if (cart == null) {
			model.addAttribute(
					"errorMessage",
					"カートに商品が入っていません。");

			return "showcart";
		}

		return "GestOrderlist";
	}
}