package jp.co.f1.spring.uos.controller;

import java.io.BufferedReader;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

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
public class InsertController {
	


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
		 * 「/insert」へGET送信アクセスがあった場合
		 */
		@GetMapping("/insert")
		public ModelAndView insert(@ModelAttribute Uniform uniform, ModelAndView mav) {

			// Viewに渡す変数をModelに格納
			mav.addObject("uniform", uniform);

			// 画面に出力するViewを指定
			mav.setViewName("view/insert");

			// ModelとView情報を返す
			return mav;
		}

		/*
		 * 「/insert」へPOST送信された場合
		 */
		@PostMapping(value = "/insert")
		// POSTデータをUniformインスタンスとして受け取る
		public ModelAndView insertPost(@ModelAttribute @Validated(Uniform.All.class) Uniform uniform, BindingResult result,
				ModelAndView mav) {

			// 権限振り分け用

			User user = (User) session.getAttribute("user");

			// userがない(セッション切れ)の時
			if (user == null) {

				mav.addObject("errorMessage", "セッション切れの為、登録できません。 ");
				mav.addObject("cmd", "logout");
				mav.addObject("next", "[ログイン画面へ]");
				mav.setViewName("view/error");
				return mav;

			}

			mav.addObject("user", user);

			// 書籍情報の検索
			Optional<Uniform> optionalUniform = uniforminfo.findByProductno(uniform.getProductNo());

			// 入力エラーがある場合
			if (result.hasErrors()) {
				// エラーメッセージ
				mav.addObject("message", "入力内容に誤りがあります");

				// 画面に出力するViewを指定
				mav.setViewName("view/insert");

				// ModelとView情報を返す
				return mav;

				// ISBNの重複チェック
			} else if (optionalUniform.isPresent()) {
				// エラーメッセージ

				mav.addObject("errorMessage", "入力した商品番号は既に登録済みの為、商品登録処理は行えませんでした。 ");
				mav.addObject("cmd", "insert");
				mav.addObject("next", "[登録画面に戻る]");
				mav.setViewName("view/error");
				return mav;

			}

			// 入力されたデータをDBに保存
			uniforminfo.saveAndFlush(uniform);

			// リダイレクト先を指定
			mav = new ModelAndView("redirect:/list");

			// ModelとView情報を返す
			return mav;
		}

	

}
