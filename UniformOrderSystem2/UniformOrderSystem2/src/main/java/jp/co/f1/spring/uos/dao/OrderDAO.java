package jp.co.f1.spring.uos.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.Order;

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

	    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Order> query = builder.createQuery(Order.class);
	    Root<Order> root = query.from(Order.class);

	    Predicate yearCondition = null;

	    if (year != null && !year.isEmpty()) {
	        yearCondition = builder.equal(
	            builder.function(
	                "YEAR",
	                Integer.class,
	                root.get("datetime")
	            ),
	            Integer.parseInt(year)
	        );
	    }

	    Predicate monthCondition = null;

	    if (month != null && !month.isEmpty()) {
	        monthCondition = builder.equal(
	            builder.function(
	                "MONTH",
	                Integer.class,
	                root.get("datetime")
	            ),
	            Integer.parseInt(month)
	        );
	    }

	    if (yearCondition != null && monthCondition != null) {

	        query.where(
	            builder.and(yearCondition, monthCondition)
	        );

	    } else if (yearCondition != null) {

	        query.where(yearCondition);

	    } else if (monthCondition != null) {

	        query.where(monthCondition);
	    }

	    query.select(root);

	    // 新しい注文から表示
	    query.orderBy(builder.desc(root.get("datetime")));

	    return entityManager
	            .createQuery(query)
	            .getResultList();
	
	}
}
