package jp.co.f1.spring.uos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jp.co.f1.spring.uos.entity.Uniform;

import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public class OrderDAO {
	// エンティティマネージャー
	private EntityManager entityManager;

	// クエリ生成用インスタンス
	private CriteriaBuilder builder;

	// クエリ実行用インスタンス
	private CriteriaQuery<Order> query;

	// 検索されるエンティティのルート
	private Root<Order> root;

	/**
	 * コンストラクタ（DB接続準備）
	 */
	public OrderDAO(EntityManager entityManager) {
		// EntityManager取得
		this.entityManager = entityManager;
		// クエリ生成用インスタンス
		builder = entityManager.getCriteriaBuilder();
		// クエリ実行用インスタンス
		query = builder.createQuery(Order.class);
		// 検索されるエンティティのルート
		root = query.from(Order.class);
	}
	
	public List<Order> findByMonth(String year, String month) {
		// クエリビルダーを使用してCriteriaQueryを作成
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
		Root<Order> root = query.from(Order.class); // Orderエンティティを使う
		Join<Order, OrderDetail> detailJoin = root.join("orderdetail", JoinType.INNER); // Order と Ordetdail を結合

		// 年と月を基にした条件を作成(今月の売り上げと先月の売り上げ)
		Predicate yearCondition = null;
		if (year != null && !year.isEmpty()) {
			yearCondition = builder.equal(builder.function("YEAR", Integer.class, root.get("datetime")),
					Integer.parseInt(year)); // `date`フィールドから年を取得
		}

		Predicate monthCondition = null;
		if (month != null && !month.isEmpty()) {
			monthCondition = builder.equal(builder.function("MONTH", Integer.class, root.get("datetime")),
					Integer.parseInt(month)); // `date`フィールドから月を取得
		}
		
		// WHERE句を設定
		if (yearCondition != null && monthCondition != null) {
			query.where(builder.and(yearCondition, monthCondition)); // 年と月の両方の条件
		} else if (yearCondition != null) {
			query.where(yearCondition); // 年のみの条件
		} else if (monthCondition != null) {
			query.where(monthCondition); // 月のみの条件
		} else {
			query.where(builder.conjunction()); // 年も月も指定されていない場合（全件取得）
		}

		// 注文日時順に並べる
		query.orderBy(builder.asc(root.get("datetime")));
		
		// クエリ実行
		List<Object[]> result = entityManager.createQuery(query).getResultList();

		// 結果をOrderオブジェクトに変換して返す
		List<Order> orderList = new ArrayList<>();
		for (Object[] row : result) {
			Order order = new Order();
			order.setOrderno((String) row[0]); // 注文no
			order.setUserid((String) row[1]); // 注文者のID
			order.setTotalPrice((int) row[2]); // 合計金額
			order.setTotalQuantity((int) row[3]); // 合計個数 
			order.setDatetime((Date) row[4]); // 注文日時
			order.setName((String) row[5]);	// 注文者の名前
			order.setAddress((String) row[6]);	// 注文者の住所
			order.setEmail((String) row[7]);		// 注文者のメールアドレス
			order.setRemarks((String) row[8]);	  // 備考
			order.setPayment((String) row[9]);	  // 入金状況
			order.setShipment((String) row[10]);	// 発送状況
			
			
			orderList.add(order);
		}

		return orderList;
	}
}
