package jp.co.f1.spring.uos.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.dao.OrderDAO;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.OrderDetailRepository;
import jp.co.f1.spring.uos.repository.OrderRepository;
import jp.co.f1.spring.uos.repository.UniformRepository;
import jp.co.f1.spring.uos.repository.UserRepository;

@Controller
public class OrderedListController {

	@Autowired
	private HttpSession session;
	private User user = new User();

	@Autowired
	private OrderRepository orderinfo;;

	@Autowired
	private OrderDAO orderDao;

	@Autowired
	private UserRepository userinfo;

	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UniformRepository uniformRepository;
	private final UserRepository userRepository;
	private final JavaMailSender mailSender;

	/*
	 * コンストラクタ
	 */
	public OrderedListController(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
			UniformRepository uniformRepository, UserRepository userRepository, JavaMailSender mailSender) {
		this.orderRepository = orderRepository;
		this.orderDetailRepository = orderDetailRepository;
		this.uniformRepository = uniformRepository;
		this.userRepository = userRepository;
		this.mailSender = mailSender;
	}

	/*
	 * application.propertiesの spring.mail.usernameを取得
	 */
	@Value("${spring.mail.username}")
	private String fromAddress;

	/*
	 * 入金済みにした時メール送信
	 */
	private void sendPaymentMail(Order order, List<OrderDetail> orderDetails) {

		SimpleMailMessage message = new SimpleMailMessage();

		/*
		 * 送信元
		 */
		message.setFrom(fromAddress);

		/*
		 * 送信先
		 */
		message.setTo("kisaragi33pencilwonder@gmail.com");

		/*
		 * order.getEmail()
		 * 
		 * 
		 * /
		 * 
		 * 
		 * /* 件名
		 *
		 */
		message.setSubject("【神田ユニフォーム】" + "ご入金の確認");

		/*
		 * 本文
		 */
		StringBuilder text = new StringBuilder();
		text.append(order.getName()).append(" 様\n\n");
		text.append("ご注文ありがとうございます。\n");
		text.append("以下の注文について入金を確認しました。\n\n");
		text.append("注文番号：").append(order.getOrderno()).append("\n\n");
		text.append("【購入内容】\n");

		/*
		 * 複数商品をメール本文へ追加
		 */
		for (OrderDetail detail : orderDetails) {
			int subtotal = detail.getPrice() * detail.getQuantity();
			text.append("商品名：").append(detail.getProductname()).append("\n");
			text.append("税込価格：").append(detail.getPrice()).append("円\n");
			text.append("購入個数：").append(detail.getQuantity()).append("枚\n");
			text.append("小計：").append(subtotal).append("円\n\n");
		}
		text.append("合計金額：").append(order.getTotalprice()).append("円\n\n");

		/*
		 * 実際の振込先へ変更する
		 */
		text.append("発送完了時、再度メールいたします。\n");
		message.setText(text.toString());
		/*
		 * メールを送信
		 */
		mailSender.send(message);
	}

	/*
	 * nullを空文字へ変換し、 前後の空白を削除
	 */
	private String normalize(String value) {
		if (value == null) {
			return "";
		}
		return value.trim();
	}

	/*
	 * 発送済みにした時メール送信
	 */
	private void sendShipmentMail(Order order, List<OrderDetail> orderDetails) {

		SimpleMailMessage message = new SimpleMailMessage();

		/*
		 * 送信元
		 */
		message.setFrom(fromAddress);

		/*
		 * 送信先
		 */
		message.setTo("kisaragi33pencilwonder@gmail.com");

		/*
		 * order.getEmail()
		 * 
		 * 
		 * /
		 * 
		 * 
		 * /* 件名
		 *
		 */
		message.setSubject("【神田ユニフォーム】" + "発送完了のお知らせ");

		/*
		 * 本文
		 */
		StringBuilder text = new StringBuilder();
		text.append(order.getName()).append(" 様\n\n");
		text.append("ご注文ありがとうございます。\n");
		text.append("以下の注文について発送処理をいたしました。\n\n");
		text.append("注文番号：").append(order.getOrderno()).append("\n\n");
		text.append("【購入内容】\n");

		/*
		 * 複数商品をメール本文へ追加
		 */
		for (OrderDetail detail : orderDetails) {
			int subtotal = detail.getPrice() * detail.getQuantity();
			text.append("商品名：").append(detail.getProductname()).append("\n");
			text.append("税込価格：").append(detail.getPrice()).append("円\n");
			text.append("購入個数：").append(detail.getQuantity()).append("枚\n");
			text.append("小計：").append(subtotal).append("円\n\n");
		}
		text.append("合計金額：").append(order.getTotalprice()).append("円\n\n");

		message.setText(text.toString());

		/*
		 * メールを送信
		 */
		mailSender.send(message);
	}

	/*
	 * nullを空文字へ変換し、 前後の空白を削除
	 */
	private String normalizeShip(String value) {
		if (value == null) {
			return "";
		}
		return value.trim();
	}

	@GetMapping("/orderedList")
	public ModelAndView orderedList(HttpServletRequest request, ModelAndView mav) {

		// セッションからユーザー情報取得
		// User user = (User) session.getAttribute("user");

		// セッションを受け取る
		String authAdminCheck = (String) session.getAttribute("authority");
		Optional<User> optionalUser = userinfo.findByAuthority(authAdminCheck);

		// userがない(セッション切れ)の時
		if (optionalUser.isEmpty()) {

			mav.addObject("errorMessage", "セッション切れの為、商品一覧に戻ります。");

			mav.setViewName("view/error");
			return mav;

		}

		mav.addObject("user", optionalUser);
		user = optionalUser.get();

		// セッション切れの場合
		if (user == null) {
			// エラーメッセージ
			mav.addObject("errorMessage", "セッション切れの為、売り上げ状況の確認は出来ません。");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			// 画面に出力するViewを指定
			mav.setViewName("view/error");
			// ModelとView情報を返す
			return mav;
		}

		// 検索した年、月をパラメータ取得
		String year = request.getParameter("year");
		String month = request.getParameter("month");

		LocalDate today = LocalDate.now();

		if (year == null || year.isEmpty()) {
			year = String.valueOf(today.getYear());
		}

		if (month == null || month.isEmpty()) {
			month = String.valueOf(today.getMonthValue());
		}

		// データを検索
		List<Order> orderList = orderDao.findByMonth(year, month);
		// 小計用のArrayListを作成
		ArrayList<Integer> subtotal_list = new ArrayList<Integer>();

		// 合計、小計の計算
		// 合計金額用変数の初期化
		int total = 0;
		for (int i = 0; i < orderList.size(); i++) {

			// 該当商品を取り出す
			Optional<Order> orderList1 = orderinfo.findByOrderno(orderList.get(i).getOrderno());
			Order order = orderList.get(i);

			// 合計値を合算

			total += order.getTotalprice();
		}

		// 先月と今月の売り上げ
		today = LocalDate.now();
		LocalDate previousMonth = today.minusMonths(1);

		int currentMonthSales = 0;
		int previousMonthSales = 0;

		Iterable<Order> allOrders = orderinfo.findAll();

		for (Order order : allOrders) {

			if (order.getDatetime() == null) {
				continue;
			}

			LocalDate orderDate = order.getDatetime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// 今月
			if (orderDate.getYear() == today.getYear() && orderDate.getMonthValue() == today.getMonthValue()
					&& "2".equals(order.getPayment())) {

				currentMonthSales += order.getTotalprice();
			}

			// 先月
			if (orderDate.getYear() == previousMonth.getYear()
					&& orderDate.getMonthValue() == previousMonth.getMonthValue() && "2".equals(order.getPayment())) {

				previousMonthSales += order.getTotalprice();
			}
		}

		mav.addObject("currentMonthSales", currentMonthSales);
		mav.addObject("previousMonthSales", previousMonthSales);

		// Modelに検索した年、月、合計を追加
		mav.addObject("total", total);
		mav.addObject("subtotal_list", subtotal_list);
		mav.addObject("year", year);
		mav.addObject("month", month);
		mav.addObject("order_list", orderList);

		// 画面に出力するViewを指定
		mav.setViewName("view/orderedList");
		// ModelとView情報を返す
		return mav;
	}

	@PostMapping("/orderedList/payment")
	public String updatePayment(@RequestParam String orderno, @RequestParam String payment) {

		List<OrderDetail> orderDetails = new ArrayList<>();

		orderDetails = (List<OrderDetail>) orderDetailRepository.findById_Orderno(orderno);
		Order order = orderinfo.findById(orderno).orElse(null);

		if (order != null) {
			order.setPayment(payment);
			orderinfo.save(order);

			sendPaymentMail(order, orderDetails);

		}

		return "redirect:/orderedList";
	}

	@PostMapping("/orderedList/shipment")
	public String updateShipment(@RequestParam String orderno, @RequestParam String shipment) {

		List<OrderDetail> orderDetails = new ArrayList<>();

		orderDetails = (List<OrderDetail>) orderDetailRepository.findById_Orderno(orderno);
		Order order = orderinfo.findById(orderno).orElse(null);

		if (order != null) {
			order.setShipment("2");
			orderinfo.save(order);

			sendShipmentMail(order, orderDetails);

		}

		return "redirect:/orderedList";
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