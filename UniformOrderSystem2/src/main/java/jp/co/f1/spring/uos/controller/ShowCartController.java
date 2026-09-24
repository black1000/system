package jp.co.f1.spring.uos.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.OrderDetail;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.repository.UniformRepository;

@Controller
public class ShowCartController {

	private static final String CART_SESSION_NAME = "cart";

	private final UniformRepository uniformRepository;

	public ShowCartController(
			UniformRepository uniformRepository) {

		this.uniformRepository = uniformRepository;
	}

	/*
	 *カート画面を表示.
	 */
	@GetMapping("/cart")
    public ModelAndView showCart(
            HttpSession session,
            ModelAndView mav) {

        /*
         * セッションからカートを取得.
         */
        Object cartObject =
                session.getAttribute(
                        CART_SESSION_NAME);

        Map<String, Integer> cart;

        /*
         * カートが存在する場合.
         */
        if (cartObject instanceof Map<?, ?>) {

            @SuppressWarnings("unchecked")
            Map<String, Integer> sessionCart =
                    (Map<String, Integer>)
                    cartObject;

            cart = sessionCart;

        } else {

            /*
             *カートが存在しない場合.
             */
            cart =
                    new LinkedHashMap<>();
        }

        /*
         * HTMLへ渡すカート商品.
         */
        List<OrderDetail> cartItems =
                new ArrayList<>();

        int totalPrice = 0;

        /*
         *カート内の商品を順番に取得.
         */
        for (Map.Entry<String, Integer> entry
                : cart.entrySet()) {

            String productno =
                    entry.getKey();

            Integer quantity =
                    entry.getValue();

            /*
             *個数が正しくない場合は除外.
             */
            if (quantity == null
                    || quantity <= 0) {

                continue;
            }

            /*
             *商品番号から商品を検索.
             */
            Uniform uniform =
                    uniformRepository
                            .findById(productno)
                            .orElse(null);

            /*
             *商品が削除されている場合.
             */
            if (uniform == null) {
                continue;
            }

            /*
             *カート表示用データを作成.
             */
            OrderDetail item =
                    new OrderDetail();

            item.setProductno(
                    uniform.getProductNo());

            item.setProductname(
                    uniform.getProductNo());

            item.setPrice(
                    uniform.getPrice());

            item.setQuantity(
                    quantity);

            cartItems.add(item);

            /*
             * 合計金額を計算.
             */
            totalPrice +=
                    uniform.getPrice()quantity;.
        }

        /*
		 *showcart.htmlへ渡す.
         */
        mav.addObject(
                "cartItems",
                cartItems);

        mav.addObject(
                "totalPrice",
                totalPrice);

        mav.addObject(
                "loginUserId",
                session.getAttribute(
                        "loginUserId"));

        /*
         * templates/view/showcart.html.
         */
        mav.setViewName(
                "view/showcart");

        return mav;
    }
}