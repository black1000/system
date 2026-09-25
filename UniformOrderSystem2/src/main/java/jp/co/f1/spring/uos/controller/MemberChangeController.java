package jp.co.f1.spring.uos.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.f1.spring.uos.entity.CheckUser;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UserRepository;

@Controller
public class MemberChangeController {

	private static final String MEMBER_AUTHORITY = "2";

	private final UserRepository userRepository;

	/*
	 *コンストラクタ.
	 */
	public MemberChangeController(
			UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	/*
	 *会員情報変更画面を表示.
	 *
	 *会員メニューのURLが.
	 * /member/profileの場合にも対応.
	 */
	@GetMapping({
			"/memberChange",
			"/member/profile"
	})
	public ModelAndView showMemberChange(
			HttpSession session,
			ModelAndView mav) {

		/*
		 *セッションからログイン情報を取得.
		 */
		String loginUserId = (String) session.getAttribute(
				"loginUserId");

		String authority = (String) session.getAttribute(
				"authority");

		/*
		 *未ログインまたは.
		 *一般会員ではない場合.
		 */
		if (loginUserId == null
				|| !MEMBER_AUTHORITY.equals(
						authority)) {

			mav.addObject(
					"errorMessage",
					"セッションが切れています。"
							+ "もう一度ログインしてください。");

			mav.setViewName(
					"view/error");

			return mav;
		}

		/*
		 *DBから会員情報を取得.
		 */
		User user = userRepository
				.findById(loginUserId)
				.orElse(null);

		/*
		 *会員情報が存在しない場合.
		 */
		if (user == null) {

			session.invalidate();

			mav.addObject(
					"errorMessage",
					"会員情報を確認できませんでした。"
							+ "もう一度ログインしてください。");

			mav.setViewName(
					"view/error");

			return mav;
		}

		/*
		 *HTMLのth:object="${checkUser}".
		 *へ渡すフォームを作成.
		 */
		CheckUser checkUser = new CheckUser();

		checkUser.setUserid(
				user.getUserid());

		checkUser.setName(
				user.getName());

		checkUser.setEmail(
				user.getEmail());

		checkUser.setAddress(
				user.getAddress());

		/*
		 *パスワードは画面へ渡さない.
		 */
		checkUser.setOldPassword("");
		checkUser.setNewPassword("");
		checkUser.setConfirmPassword("");

		mav.addObject(
				"checkUser",
				checkUser);

		/*
		 *templates/view/memberChange.html.
		 */
		mav.setViewName(
				"view/memberChange");

		return mav;
	}

	/*
	 *会員情報を変更.
	 */
	@PostMapping("/memberChange")
	public ModelAndView updateMember(
			@Validated @ModelAttribute("checkUser") CheckUser checkUser,

			BindingResult result,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {

		/*
		 *セッションからログイン情報を取得.
		 */
		String loginUserId = (String) session.getAttribute(
				"loginUserId");

		String authority = (String) session.getAttribute(
				"authority");

		/*
		 *未ログインまたは.
		 *一般会員ではない場合.
		 */
		if (loginUserId == null
				|| !MEMBER_AUTHORITY.equals(
						authority)) {

			mav.addObject(
					"errorMessage",
					"セッションが切れています。"
							+ "もう一度ログインしてください。");

			mav.setViewName(
					"view/error");

			return mav;
		}

		/*
		 *送信されたユーザーIDを信用せず、.
		 *セッションのIDを設定.
		 */
		checkUser.setUserid(
				loginUserId);

		/*
		 *DBから現在の会員情報を取得.
		 */
		User oldUser = userRepository
				.findById(loginUserId)
				.orElse(null);

		if (oldUser == null) {

			session.invalidate();

			mav.addObject(
					"errorMessage",
					"会員情報を確認できませんでした。");

			mav.setViewName(
					"view/error");

			return mav;
		}

		/*
		 *入力エラーがある場合.
		 */
		if (result.hasErrors()) {

			mav.addObject(
					"message",
					"入力内容に誤りがあります。");

			mav.setViewName(
					"view/memberChange");

			return mav;
		}

		/*
		 *現在のパスワードを確認.
		 */
		if (!oldUser.getPassword().equals(
				checkUser.getOldPassword())) {

			mav.addObject(
					"message",
					"現在のパスワードが違います。");

			mav.setViewName(
					"view/memberChange");

			return mav;
		}

		/*
		 *新しいパスワードの未入力確認.
		 */
		if (checkUser.getNewPassword() == null
				|| checkUser
						.getNewPassword()
						.isBlank()) {

			mav.addObject(
					"passwordError",
					"新しいパスワードを"
							+ "入力してください。");

			mav.addObject(
					"message",
					"入力内容に誤りがあります。");

			mav.setViewName(
					"view/memberChange");

			return mav;
		}

		/*
		 *新しいパスワードと.
		 *確認用パスワードを比較.
		 */
		if (!checkUser
				.getNewPassword()
				.equals(
						checkUser
								.getConfirmPassword())) {

			mav.addObject(
					"message",
					"新しいパスワードと"
							+ "確認用パスワードが"
							+ "一致しません。");

			mav.setViewName(
					"view/memberChange");

			return mav;
		}

		/*
		 *メールアドレスの簡単な確認.
		 */
		if (checkUser.getEmail() == null
				|| !checkUser
						.getEmail()
						.contains("@")) {

			mav.addObject(
					"message",
					"メールアドレスを"
							+ "正しく入力してください。");

			mav.setViewName(
					"view/memberChange");

			return mav;
		}

		/*
		 *DBへ更新内容を設定.
		 *useridは主キーなので変更しない.
		 */
		oldUser.setName(
				checkUser
						.getName()
						.trim());

		oldUser.setEmail(
				checkUser
						.getEmail()
						.trim());

		oldUser.setAddress(
				checkUser
						.getAddress()
						.trim());

		oldUser.setPassword(
				checkUser
						.getNewPassword());

		/*
		 *DBへ保存.
		 */
		userRepository.saveAndFlush(
				oldUser);

		/*
		 *セッション内の表示名も更新.
		 */
		session.setAttribute(
				"loginUserName",
				oldUser.getName());

		/*
		 *リダイレクト後に表示する.
		 *完了メッセージ.
		 */
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"会員情報を更新しました。");

		/*
		 *二重送信を防ぐためリダイレクト.
		 */
		mav.setViewName(
				"redirect:/memberChange");

		return mav;
	}
}