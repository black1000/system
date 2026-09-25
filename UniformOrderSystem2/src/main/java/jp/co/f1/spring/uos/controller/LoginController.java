package jp.co.f1.spring.uos.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UserRepository;

@Controller
public class LoginController {

	// DBの権限値
	private static final String ADMIN_AUTHORITY = "1";
	private static final String MEMBER_AUTHORITY = "2";

	// セッションに保存するカートの名前
	private static final String CART_SESSION_NAME = "cart";

	private final UserRepository userRepository;

	
	public LoginController(
			UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	/*
	 *一般会員用ログイン画面を表示.
	 */
	@GetMapping("/login")
	public ModelAndView showLogin(
			ModelAndView mav) {

		mav.addObject("adminLogin", false);
		mav.setViewName("view/login");

		return mav;
	}

	/*
	 *管理者用ログイン画面を表示.
	 */
	@GetMapping("/admin/login")
	public ModelAndView showAdminLogin(
			ModelAndView mav) {

		mav.addObject("adminLogin", true);
		mav.setViewName("view/login");

		return mav;
	}

	/*
	 *一般会員のログイン処理.
	 */
	@PostMapping("/login")
	public ModelAndView login(
			@RequestParam(name = "userid", defaultValue = "") String userid,

			@RequestParam(name = "password", defaultValue = "") String password,

			HttpServletRequest request,
			ModelAndView mav) {

		return checkLogin(
				userid,
				password,
				false,
				request,
				mav);
	}

	/*
	 *管理者のログイン処理.
	 */
	@PostMapping("/admin/login")
	public ModelAndView adminLogin(
			@RequestParam(name = "userid", defaultValue = "") String userid,

			@RequestParam(name = "password", defaultValue = "") String password,

			HttpServletRequest request,
			ModelAndView mav) {

		return checkLogin(
				userid,
				password,
				true,
				request,
				mav);
	}

	/*
	 *一般会員・管理者の共通ログイン処理.
	 */
	private ModelAndView checkLogin(
			String userid,
			String password,
			boolean adminLogin,
			HttpServletRequest request,
			ModelAndView mav) {

		/*
	     *エラー時にログイン画面を.
		 *再表示するための情報.
		 */
		mav.addObject("adminLogin", adminLogin);
		mav.addObject("userid", userid);
		mav.setViewName("view/login");

		/*
		 *未入力確認.
		 */
		if (userid.isBlank()
				|| password.isBlank()) {

			mav.addObject(
					"errorMessage",
					"ユーザーIDとパスワードを"
							+ "入力してください。");

			return mav;
		}

		/*
		 *ユーザーIDでDBを検索.
		 */
		User user = userRepository
				.findById(userid)
				.orElse(null);

		/*
		 *ユーザーID・パスワードを確認.
		 */
		if (user == null
				|| !password.equals(
						user.getPassword())) {

			mav.addObject(
					"errorMessage",
					"ユーザーIDまたは"
							+ "パスワードが違います。");

			return mav;
		}

		/*
		 *DBに登録された権限を確認.
		 */
		boolean isAdmin = ADMIN_AUTHORITY.equals(
				user.getAuthority());

		boolean isMember = MEMBER_AUTHORITY.equals(
				user.getAuthority());

		/*
		 *権限が1または2ではない場合.
		 */
		if (!isAdmin && !isMember) {

			mav.addObject(
					"errorMessage",
					"このアカウントでは"
							+ "ログインできません。");

			return mav;
		}

		/*
		 * 管理者用ログイン画面に.
		 *一般会員が入力された場合.
		 */
		if (adminLogin && !isAdmin) {

			mav.addObject(
					"errorMessage",
					"管理者アカウントで"
							+ "ログインしてください。");

			return mav;
		}

		/*
		 *一般会員用ログイン画面に.
		 *管理者が入力された場合.
		 */
		if (!adminLogin && isAdmin) {

			mav.addObject(
					"errorMessage",
					"管理者用ログイン画面から"
							+ "ログインしてください。");

			return mav;
		}

		/*
		 *ログイン成功.
		 */
		HttpSession session = request.getSession();

		/*
		 *カートの内容などを残したまま.
		 *セッションIDだけを変更.
		 */
		request.changeSessionId();

		/*
		 *ログイン情報をセッションに保存.
		 *パスワードは保存しない.
		 */
		session.setAttribute(
				"loginUserId",
				user.getUserid());

		session.setAttribute(
				"loginUserName",
				user.getName());

		session.setAttribute(
				"authority",
				user.getAuthority());

		/*
		 *ログイン画面用のModelを消す.
		 */
		mav.clear();

		/*
		 *権限とカートの有無によって.
		 *ログイン後の移動先を決める.
		 */
		if (isAdmin) {

			// 管理者メニューへ移動
			mav.setViewName(
					"redirect:/admin/menu");

		} else {

			/*
			 *ログイン前に商品をカートへ.
			 *入れていたか確認.
			 */
			Object cart = session.getAttribute(
					CART_SESSION_NAME);

			if (cart instanceof Map<?, ?> cartMap
					&& !cartMap.isEmpty()) {

				/*
				 *カートがある場合は.
				 *選択した商品を引き継いで.
				 *カート画面へ移動.
				 */
				mav.setViewName(
						"redirect:/cart");

			} else {

				/*
				 *カートがない場合は.
				 *会員メニューへ移動.
				 */
				mav.setViewName(
						"redirect:/member/menu");
			}
		}

		return mav;
	}
}