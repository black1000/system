package jp.co.f1.spring.uos.controller;

import java.util.ArrayList;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.User;

@Controller
public class OrderedListController {

	@Autowired
	private HttpSession session;

	// DBの権限値：1＝管理者
	private static final String ADMIN_AUTHORITY = "1";

	@GetMapping("orderedList")
	public ModelAndView orderedList(HttpServletRequest request, ModelAndView mav) {

		//セッションからユーザー情報取得
		User user = (User) session.getAttribute("user");

		//セッション切れの場合
		if (user == null) {
			//エラーメッセージ
			mav.addObject("errorMessage", "セッション切れの為、売り上げ状況の確認は出来ません。");
			mav.addObject("cmd", "logout");
			mav.addObject("next", "[ログイン画面へ]");
			// 画面に出力するViewを指定
			mav.setViewName("view/error");
			// ModelとView情報を返す
			return mav;
		}
		
		//検索した年、月をパラメータ取得
				String year = request.getParameter("year");
				String month = request.getParameter("month");

				//データを検索
				Iterable<Order> order_list = OrderDAO.findByMonth(year, month);

				//キャスト
				ArrayList<Order> orderList = (ArrayList<Order>) order_list;
				//小計用のArrayListを作成
				ArrayList<Integer> subtotal_list = new ArrayList<Integer>();

				//合計、小計の計算
				//合計金額用変数の初期化
				int total = 0;
				for (int i = 0; i < orderList.size(); i++) {

					//該当商品を取り出す
					Optional<Order> bookList = orderinfo.findByIsbn(orderList.get(i).getOrderno());
					Order Book = bookList.get();

					
					//合計値を合算
					total += Integer.parseInt(Order.getPrice()) * orderList.get(i).getQuantity();
				}

				// Modelに検索した年、月、合計を追加
				mav.addObject("total", total);
				mav.addObject("subtotal_list", subtotal_list);
				mav.addObject("year", year);
				mav.addObject("month", month);
				mav.addObject("order_list", order_list);

				//画面に出力するViewを指定
				mav.setViewName("view/orderList");
				//ModelとView情報を返す
				return mav;
			}
	}


