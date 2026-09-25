package jp.co.f1.spring.uos.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.repository.UniformRepository;

@Controller
public class ListController {

	// DBの権限値：1＝管理者
	private static final String ADMIN_AUTHORITY = "1";

	// セッションで使用する名前
	private static final String SESSION_AUTHORITY = "authority";
	private static final String SESSION_LOGIN_USER_ID = "loginUserId";
	private static final String SESSION_CART = "cart";

	private final UniformRepository uniformRepository;

	public ListController(
			UniformRepository uniformRepository) {

		this.uniformRepository = uniformRepository;
	}

	/*
	 * 一般購入者用の商品一覧.
	 *
	 * / または /list へアクセスした場合.
	 */
	@GetMapping({ "/", "/list" })
	public ModelAndView showProductList(
			HttpSession session,
			ModelAndView mav) {

		String authority = (String) session.getAttribute(
				SESSION_AUTHORITY);

		// 管理者は管理者用の商品一覧へ移動
		if (ADMIN_AUTHORITY.equals(authority)) {

			mav.setViewName("redirect:/admin/list");

			return mav;
		}

		List<Uniform> products = uniformRepository
				.findAllByOrderByProductnoAsc();

		mav.addObject("products", products);

		mav.addObject(
				"loginUserId",
				session.getAttribute(
						SESSION_LOGIN_USER_ID));

		mav.setViewName("view/list");

		return mav;
	}

	/*
	 * 管理者用の商品一覧.
	 */
	@GetMapping("view/admin/list")
	public ModelAndView showAdminProductList(
			HttpSession session,
			ModelAndView mav) {

		String authority = (String) session.getAttribute(
				SESSION_AUTHORITY);

		// 管理者以外は管理者ログイン画面へ移動
		if (!ADMIN_AUTHORITY.equals(authority)) {

			mav.setViewName("redirect:/admin/login");

			return mav;
		}

		List<Uniform> products = uniformRepository
				.findAllByOrderByProductnoAsc();
		
	

		// 在庫数が0の商品があるか確認
		boolean hasSoldOutProduct = products.stream()
				.anyMatch(
						product -> product.getStock() == 0);

		mav.addObject("products", products);

		mav.addObject(
				"hasSoldOutProduct",
				hasSoldOutProduct);

		mav.setViewName("view/admin/list");

		return mav;
	}

	/*
	 * 商品一覧で選択した商品をカートへ追加.
	 */
	@PostMapping("/cart/add")
	public ModelAndView addCart(
			@RequestParam(name = "productIds", required = false) List<String> productIds,

			@RequestParam(name = "quantities", required = false) List<Integer> quantities,

			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {

		String authority = (String) session.getAttribute(
				SESSION_AUTHORITY);

		// 管理者は購入できない
		if (ADMIN_AUTHORITY.equals(authority)) {

			mav.setViewName("redirect:/admin/list");

			return mav;
		}

		// 商品番号・購入個数を受け取れなかった場合
		if (productIds == null
				|| quantities == null
				|| productIds.isEmpty()
				|| productIds.size() != quantities.size()) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"商品を正しく選択してください。");

			mav.setViewName("redirect:/list");

			return mav;
		}

		/*
		 * セッションから現在のカートを取得.
		 *
		 * キー：商品番号.
		 *値  ：購入個数.
		 */
		@SuppressWarnings("unchecked")
		Map<String, Integer> currentCart = (Map<String, Integer>) session.getAttribute(SESSION_CART);

		/*
		 * エラー発生時に現在のカートを.
		 *変更しないようコピーして処理する
		 */
		Map<String, Integer> updatedCart = currentCart == null
				? new LinkedHashMap<>()
				: new LinkedHashMap<>(currentCart);

		boolean productSelected = false;

		// 商品番号と個数を同じ順番で取得
		for (int i = 0; i < productIds.size(); i++) {

			String productno = productIds.get(i);

			Integer selectedQuantity = quantities.get(i);

			// 0枚の商品は追加しない
			if (selectedQuantity == null
					|| selectedQuantity <= 0) {

				continue;
			}

			productSelected = true;

			Uniform uniform = uniformRepository
					.findById(productno)
					.orElse(null);

			// DBに存在しない商品だった場合
			if (uniform == null) {

				redirectAttributes.addFlashAttribute(
						"errorMessage",
						"選択された商品が見つかりません。");

				mav.setViewName("redirect:/list");

				return mav;
			}

			// 既にカートに入っている個数
			int currentQuantity = updatedCart.getOrDefault(
					productno, 0);

			// 今回選択した個数を加算
			int totalQuantity = currentQuantity
					+ selectedQuantity;

			// 在庫数を超えた場合
			if (totalQuantity > uniform.getStock()) {

				redirectAttributes.addFlashAttribute(
						"errorMessage",
						uniform.getProductNo()
								+ "は在庫数を超えて"
								+ "購入できません。");

				mav.setViewName("redirect:/list");

				return mav;
			}

			// カートへ商品番号・個数を保存
			updatedCart.put(
					productno,
					totalQuantity);
		}

		// 全商品の購入個数が0枚だった場合
		if (!productSelected) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"購入する商品を選択してください。");

			mav.setViewName("redirect:/list");

			return mav;
		}

		// 更新後のカートをセッションに保存
		session.setAttribute(
				SESSION_CART,
				updatedCart);

		// カート画面へ移動
		mav.setViewName("redirect:/cart");

		return mav;
	}
}