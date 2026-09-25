package jp.co.f1.spring.uos.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.OrderDetailRepository;
import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.repository.UniformRepository;
import jp.co.f1.spring.uos.repository.UserRepository;

@Controller
public class BuyConfirmController {
	// 権限
	private static final String ADMIN_AUTHORITY = "1";
	private static final String MEMBER_AUTHORITY = "2";
	// セッションに保存しているカート名
	private static final String CART_SESSION_NAME = "cart";
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UniformRepository uniformRepository;
	private final UserRepository userRepository;
	private final JavaMailSender mailSender;
	/*
	 * application.propertiesの
	 * spring.mail.usernameを取得
	 */
	@Value("${spring.mail.username}")
	private String fromAddress;

	/*
	 * コンストラクタ
	 */
	public BuyConfirmController(
			OrderRepository orderRepository,
			OrderDetailRepository orderDetailRepository,
			UniformRepository uniformRepository,
			UserRepository userRepository,
			JavaMailSender mailSender) {
		this.orderRepository = orderRepository;
		this.orderDetailRepository = orderDetailRepository;
		this.uniformRepository = uniformRepository;
		this.userRepository = userRepository;
		this.mailSender = mailSender;
	}

	/*
	 * 注文確定処理
	 */
	@Transactional
	@PostMapping("/orders/complete")
	public ModelAndView completeOrder(
			@ModelAttribute("guestOrder") Order guestOrder,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {
		/*
		 * 管理者は購入できない
		 */
		String authority = (String) session.getAttribute(
				"authority");
		if (ADMIN_AUTHORITY.equals(authority)) {
			mav.setViewName(
					"redirect:/admin/list");
			return mav;
		}
		/*
		 * カートを取得
		 */
		Object cartObject = session.getAttribute(
				CART_SESSION_NAME);
		/*
		 * カートがない、
		 * または空の場合
		 */
		if (!(cartObject instanceof Map<?, ?> cartMap)
				|| cartMap.isEmpty()) {
			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"カートに商品が入っていません。");
			mav.setViewName("redirect:/list");
			return mav;
		}
		@SuppressWarnings("unchecked")
		Map<String, Integer> cart = (Map<String, Integer>) cartObject;
		/*
		 * 保存する注文情報
		 */
		Order order = new Order();
		String loginUserId = (String) session.getAttribute(
				"loginUserId");
		/*
		 * 会員購入の場合
		 */
		if (loginUserId != null
				&& MEMBER_AUTHORITY.equals(
						authority)) {
			User user = userRepository
					.findById(loginUserId)
					.orElse(null);
			/*
			 * 会員情報が存在しない場合
			 */
			if (user == null) {
				session.removeAttribute(
						"loginUserId");
				session.removeAttribute(
						"loginUserName");
				session.removeAttribute(
						"authority");
				redirectAttributes.addFlashAttribute(
						"errorMessage",
						"会員情報を確認できませんでした。"
								+ "もう一度ログインしてください。");
				mav.setViewName(
						"redirect:/login");
				return mav;
			}
			order.setUserid(
					user.getUserid());
			order.setName(
					user.getName());
			order.setEmail(
					user.getEmail());
			order.setAddress(
					user.getAddress());
			order.setRemarks("");
		} else {
			/*
			 * ゲスト購入の場合
			 */
			String name = normalize(
					guestOrder.getName());
			String email = normalize(
					guestOrder.getEmail());
			String address = normalize(
					guestOrder.getAddress());
			String remarks = normalize(
					guestOrder.getRemarks());
			/*
			 * 入力内容をフォームへ戻す
			 */
			guestOrder.setName(name);
			guestOrder.setEmail(email);
			guestOrder.setAddress(address);
			guestOrder.setRemarks(remarks);
			/*
			 * 必須項目の確認
			 */
			if (name.isBlank()
					|| email.isBlank()
					|| address.isBlank()) {
				mav.addObject(
						"errorMessage",
						"名前、メールアドレス、住所を"
								+ "すべて入力してください。");
				mav.setViewName(
						"view/GestOrderlist");
				return mav;
			}
			/*
			 * メールアドレスの簡単な確認
			 */
			if (!email.contains("@")) {
				mav.addObject(
						"errorMessage",
						"メールアドレスを"
								+ "正しく入力してください。");
				mav.setViewName(
						"view/GestOrderlist");
				return mav;
			}
			/*
			 * 文字数の確認
			 */
			if (name.length() > 100
					|| email.length() > 100
					|| address.length() > 255
					|| remarks.length() > 200) {
				mav.addObject(
						"errorMessage",
						"入力できる文字数を"
								+ "超えています。");
				mav.setViewName(
						"view/GestOrderlist");
				return mav;
			}
			/*
			 * ゲストには会員IDがない
			 */
			order.setUserid(null);
			order.setName(name);
			order.setEmail(email);
			order.setAddress(address);
			order.setRemarks(remarks);
		}
		/*
		 * 保存する注文明細
		 */
		List<OrderDetail> orderDetails = new ArrayList<>();
		/*
		 * 在庫を更新する商品
		 */
		List<Uniform> orderedUniforms = new ArrayList<>();
		int totalPrice = 0;
		int totalQuantity = 0;
		/*
		 * カート内の商品を確認
		 */
		for (Map.Entry<String, Integer> entry : cart.entrySet()) {
			String productno = entry.getKey();
			Integer quantity = entry.getValue();
			/*
			 * 個数が正しくない場合
			 */
			if (quantity == null
					|| quantity <= 0) {
				continue;
			}
			/*
			 * 商品をDBから取得
			 */
			Uniform uniform = uniformRepository
					.findById(productno)
					.orElse(null);
			/*
			 * 商品が削除されていた場合
			 */
			if (uniform == null) {
				redirectAttributes.addFlashAttribute(
						"errorMessage",
						"カート内の商品が"
								+ "見つかりませんでした。");
				mav.setViewName(
						"redirect:/cart");
				return mav;
			}
			/*
			 * 在庫を再確認
			 */
			if (uniform.getStock() < quantity) {
				redirectAttributes.addFlashAttribute(
						"errorMessage",
						uniform.getProductno()
								+ "は在庫数を超えて"
								+ "購入できません。");
				mav.setViewName(
						"redirect:/cart");
				return mav;
			}
			/*
			 * 注文明細を作成
			 */
			OrderDetail detail = new OrderDetail();
			detail.setProductno(
					uniform.getProductno());
			detail.setProductname(
					uniform.getProductno());
			/*
			 * 購入時点の価格を保存
			 */
			detail.setPrice(
					uniform.getPrice());
			detail.setQuantity(
					quantity);
			orderDetails.add(detail);
			orderedUniforms.add(uniform);
			/*
			 * 合計金額を計算
			 */
			totalPrice += uniform.getPrice()
					* quantity;
			/*
			 * 合計個数を計算
			 */
			totalQuantity += quantity;
		}
		/*
		 * 有効な購入商品がない場合
		 */
		if (orderDetails.isEmpty()) {
			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"購入する商品を"
							+ "選択してください。");
			mav.setViewName(
					"redirect:/list");
			return mav;
		}
		/*
		 * 注文情報を設定
		 */
		order.setTotalprice(
				totalPrice);
		order.setTotalquantity(
				totalQuantity);
		order.setDatetime(
				new Date());
		/*
		 * 1：入金待ち
		 */
		order.setPayment("1");
		/*
		 * 1：発送準備前
		 */
		order.setShipment("1");
		/*
		 * orderinfoへ保存
		 *
		 * 注文番号はDBで自動採番
		 */
		Order savedOrder = orderRepository
				.saveAndFlush(order);
		/*
		 * 注文明細へ注文番号を設定し、
		 * 商品在庫を減らす
		 */
		for (int i = 0; i < orderDetails.size(); i++) {
			OrderDetail detail = orderDetails.get(i);
			Uniform uniform = orderedUniforms.get(i);
			/*
			 * 保存された注文番号
			 */
			detail.setOrderno(
					savedOrder.getOrderno());
			/*
			 * 在庫数から購入個数を引く
			 */
			uniform.setStock(
					uniform.getStock()
							- detail.getQuantity());
		}
		/*
		 * orderdetailへ保存
		 */
		List<OrderDetail> savedOrderDetails = orderDetailRepository
				.saveAll(orderDetails);
		/*
		 * 更新した在庫数を保存
		 */
		uniformRepository
				.saveAll(orderedUniforms);
		/*
		 * 購入内容と振込先を
		 * 注文者へメール送信
		 */
		sendOrderMail(
				savedOrder,
				savedOrderDetails);
		/*
		 * 完了画面へ渡す
		 */
		mav.addObject(
				"order",
				savedOrder);
		mav.addObject(
				"orderDetails",
				savedOrderDetails);
		/*
		 * 注文完了後に
		 * カート情報を削除
		 */
		session.removeAttribute("cart");
		session.removeAttribute("guestOrder");
		session.removeAttribute("pendingOrder");
		session.removeAttribute(
				"pendingOrderDetails");
		/*
		 * templates/view/Purchasecomplete.html
		 */
		mav.setViewName(
				"view/Purchasecomplete");
		return mav;
	}

	/*
	 * 購入内容と振込先をメール送信
	 */
	private void sendOrderMail(
			Order order,
			List<OrderDetail> orderDetails) {
		SimpleMailMessage message = new SimpleMailMessage();
		/*
		 * 送信元
		 */
		message.setFrom(
				fromAddress);
		/*
		 * 送信先
		 */
		message.setTo(
				order.getEmail());
		/*
		 * 件名
		 */
		message.setSubject(
				"【神田ユニフォーム】"
						+ "ご注文ありがとうございます");
		/*
		 * 本文
		 */
		StringBuilder text = new StringBuilder();
		text.append(
				order.getName())
				.append(" 様\n\n");
		text.append(
				"ご注文ありがとうございます。\n");
		text.append(
				"以下の内容で注文を受け付けました。\n\n");
		text.append("注文番号：")
				.append(
						order.getOrderno())
				.append("\n\n");
		text.append("【購入内容】\n");
		/*
		 * 複数商品をメール本文へ追加
		 */
		for (OrderDetail detail : orderDetails) {
			int subtotal = detail.getPrice()
					* detail.getQuantity();
			text.append("商品名：")
					.append(
							detail.getProductname())
					.append("\n");
			text.append("税込価格：")
					.append(
							detail.getPrice())
					.append("円\n");
			text.append("購入個数：")
					.append(
							detail.getQuantity())
					.append("枚\n");
			text.append("小計：")
					.append(subtotal)
					.append("円\n\n");
		}
		text.append("合計金額：")
				.append(
						order.getTotalprice())
				.append("円\n\n");
		/*
		 * 実際の振込先へ変更する
		 */
		text.append("【振込先】\n");
		text.append("銀行名：○○銀行\n");
		text.append("支店名：○○支店\n");
		text.append("口座種別：普通\n");
		text.append("口座番号：1234567\n");
		text.append(
				"口座名義：株式会社神田ユニフォーム\n\n");
		text.append(
				"ご入金を確認後、"
						+ "発送準備を行います。\n");
		message.setText(
				text.toString());
		/*
		 * メールを送信
		 */
		mailSender.send(message);
	}

	/*
	 * nullを空文字へ変換し、
	 * 前後の空白を削除
	 */
	private String normalize(
			String value) {
		if (value == null) {
			return "";
		}
		return value.trim();
	}
}
