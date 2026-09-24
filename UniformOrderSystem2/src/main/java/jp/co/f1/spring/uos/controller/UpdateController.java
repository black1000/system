package jp.co.f1.spring.uos.controller;

import java.util.ArrayList;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import jp.co.f1.spring.uos.dao.UniformDAO;
import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.repository.UniformRepository;


@Controller
public class UpdateController {
	


		// EntityManager自動インスタンス化
		@PersistenceContext
		private EntityManager entityManager;

		// DAO自動インスタンス化
		@Autowired
		private UniformDAO uniformDao;

		@PostConstruct
		public void init() {
			uniformDao = new UniformDAO(entityManager);
		}

		// Repositoryインターフェースを自動インスタンス化
		@Autowired
		private UniformRepository uniforminfo;

		@Autowired
		private HttpSession session;
		
		/*
		 * 「/update」へGET送信でアクセスがあった場合
		 */
		@GetMapping("/update")
		public ModelAndView update(@RequestParam(required = true) String productno, ModelAndView mav) {
			// 商品情報の検索
			Optional<Uniform> optionalUniform = uniforminfo.findByProductno(productno);

			// エラーチェック
			if (optionalUniform.isEmpty()) {
				mav.addObject("errorMessage", "更新対象の商品が存在しない為、変更画面は表示出来ませんでした。 ");
				mav.addObject("cmd", "list");
				mav.addObject("next", "[一覧表示に戻る]");
				mav.setViewName("view/error");
				return mav;
			}

			// Viewに渡す変数をModelに格納
			mav.addObject("uniform", optionalUniform.get());

			// 画面に出力するViewを指定
			mav.setViewName("view/update");

			// ModelとView情報を返す
			return mav;

		}

		/*
		 * 「/update」へPOST送信された場合
		 */
		@PostMapping(value = "/update")
		public ModelAndView postUpdate(@ModelAttribute @Validated(Uniform.All.class) Uniform uniform, BindingResult result,
				@RequestParam(required = true) String productno, ModelAndView mav) {

			// 権限振り分け用

			User user = (User) session.getAttribute("user");

			// userがない(セッション切れ)の時
			if (user == null) {

				mav.addObject("errorMessage", "セッション切れの為、更新できませんでした。 ");
				mav.addObject("cmd", "logout");
				mav.addObject("next", "[ログイン画面へ]");
				mav.setViewName("view/error");
				return mav;

			}

			mav.addObject("user", user);
			// 商品情報検索
			Optional<Uniform> optionalUniform = uniforminfo.findByProductno(productno);

			// エラーチェック
			if (optionalUniform.isEmpty()) {
				mav.addObject("errorMessage", "更新対象の商品が存在しない為、更新処理は行えませんでした。 ");
				mav.addObject("cmd", "list");
				mav.addObject("next", "[一覧表示に戻る]");
				mav.setViewName("view/error");
				return mav;

			} else if (result.hasErrors()) {
				Uniform old_uniform = optionalUniform.get();

				mav.addObject("message", "入力内容に誤りがあります。");
				mav.addObject("old_uniform", old_uniform);
				mav.setViewName("view/update");

				return mav;

			}
			uniforminfo.saveAndFlush(uniform);

			mav = new ModelAndView("redirect:/list");

			return mav;
		}
}
