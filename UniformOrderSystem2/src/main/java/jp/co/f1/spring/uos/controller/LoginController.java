package jp.co.f1.spring.uos.controller;

import java.util.Optional;

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

	private static final UserRepository userinfo = null;
	private final UserRepository userRepository;

	public LoginController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// 一般のログイン画面を表示
	@GetMapping("/login")
	public ModelAndView showLogin(ModelAndView mav) {

		mav.addObject("adminLogin", false);
		mav.setViewName("login");

		return mav;
	}

	// 管理者のログイン画面を表示
	// 今回は同じlogin.htmlを使用
	@GetMapping("/admin/login")
	public ModelAndView showAdminLogin(ModelAndView mav) {

		mav.addObject("adminLogin", true);
		mav.setViewName("login");

		return mav;
	}

	// 一般のログインボタンが押されたとき
	@PostMapping("/login")
	public ModelAndView login(
			@RequestParam(name = "userid", defaultValue = "") String userid,
			@RequestParam(name = "password", defaultValue = "") String password,
			HttpServletRequest request,
			ModelAndView mav) {

		return checkLogin(
				userid, password, false, request, mav);
	}

	// 管理者のログインボタンが押されたとき
	@PostMapping("/admin/login")
	public ModelAndView adminLogin(
			@RequestParam(name = "userid", defaultValue = "") String userid,
			@RequestParam(name = "password", defaultValue = "") String password,
			HttpServletRequest request,
			ModelAndView mav) {

		return checkLogin(
				userid, password, true, request, mav);
	}

	// 一般・管理者の共通ログイン処理
	private ModelAndView checkLogin(
			String userid,
			String password,
			boolean adminLogin,
			HttpServletRequest request,
			ModelAndView mav) {

		// 失敗した場合に表示する画面と入力済みID
		mav.setViewName("login");
		mav.addObject("adminLogin", adminLogin);
		mav.addObject("userid", userid);

		//  未入力を確認
		if (userid.isBlank() || password.isBlank()) {

			mav.addObject(
					"errorMessage",
					"ユーザーIDとパスワードを入力してください。");

			mav.setViewName("view/error");
			return mav;
		}

		//  ユーザーIDでDBを検索
		User user = userRepository.findById(userid)
				.orElse(null);

		//  ID・パスワードを確認
		// 演習用：DBに平文で保存されている場合の照合
		if (user == null
				|| !password.equals(user.getPassword())) {

			mav.addObject(
					"errorMessage",
					"ユーザーIDまたはパスワードが違います。");

			mav.setViewName("view/error");

			return mav;
		}

		//  DBの権限を確認
		boolean isAdmin = "1".equals(user.getAuthority());

		boolean isMember = "2".equals(user.getAuthority());

		if (!isAdmin && !isMember) {

			mav.addObject(
					"errorMessage",
					"このアカウントではログインできません。");

			mav.setViewName("view/error");

			return mav;
		}

		// 管理者用入口では一般会員を受け付けない
		if (adminLogin && !isAdmin) {

			mav.addObject(
					"errorMessage",
					"管理者アカウントでログインしてください。");

			mav.setViewName("view/error");

			return mav;
		}

		//入力されたユーザーIDとパスワードでユーザー検索
		Optional<User> optionalUser = userinfo.findByUseridAndPassword(user.getUsreid(), user.getPassword());

		//該当ユーザーが存在しない場合
		if (!(optionalUser.isPresent())) {
			//エラーメッセージ
			mav.addObject("errorMessage", "入力内容に誤りがあります。");
			// 画面に出力するViewを指定
			mav.setViewName("view/login");
			//ModelとView情報を返す
			return mav;
		}

		//  ログイン成功：セッションを取得
		HttpSession session = request.getSession();

		// カートの内容を残してセッションIDを変更
		request.changeSessionId();

		// パスワードは保存しない
		session.setAttribute(
				"loginUserId", user.getUsreid());

		session.setAttribute(
				"loginUserName", user.getName());

		session.setAttribute(
				"authority", user.getAuthority());

		//  画面へ渡すログイン用データを消す
		// リダイレクト先のURLへ付けないため
		mav.clear();

		//  権限によって移動先を分ける
		//if (isAdmin) {
		//	mav.setViewName("redirect:/admin/menu");
		//} else {
		//	mav.setViewName("redirect:/member/menu");
		//}

		return mav;
	}
}
