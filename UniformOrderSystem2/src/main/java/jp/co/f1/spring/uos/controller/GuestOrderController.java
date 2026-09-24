package jp.co.f1.spring.uos.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GuestOrderController {

	@GetMapping("/guest/order")
	public ModelAndView showGuestOrder(ModelAndView mav, HttpSession session,Model model) {

		Object cart = session.getAttribute("cart");

		if (cart == null) {
			model.addAttribute(
					"errorMessage",
					"カートに商品が入っていません。");

			mav.setViewName("view/error");
			
			return mav;
		}
		return mav;
	}
}