package jp.co.f1.spring.uos.dao;

import java.util.ArrayList;
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
import jp.co.f1.spring.uos.entity.User;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public class OrderDetailDAO {
	// エンティティマネージャー
	private EntityManager entityManager;

	// クエリ生成用インスタンス
	private CriteriaBuilder builder;

	// クエリ実行用インスタンス
	private CriteriaQuery<OrderDetail> query;

	// 検索されるエンティティのルート
	private Root<OrderDetail> root;

	/**
	 * コンストラクタ（DB接続準備）
	 */
	public OrderDetailDAO(EntityManager entityManager) {
		// EntityManager取得
		this.entityManager = entityManager;
		// クエリ生成用インスタンス
		builder = entityManager.getCriteriaBuilder();
		// クエリ実行用インスタンス
		query = builder.createQuery(OrderDetail.class);
		// 検索されるエンティティのルート
		root = query.from(OrderDetail.class);
	}

	/**
	 * 注文情報検索
	 * @param String 　orderno
	 * @return ArrayList<OrderDetail> orderdetail_list
	 */
	public ArrayList<OrderDetail> find(String orderno) {
		// SELECT句設定
		query.select(root);

		// WHERE句設定
		query.where(
				builder.like(root.get("orderno"),  orderno));

		// クエリ実行
		return (ArrayList<OrderDetail>) entityManager.createQuery(query).getResultList();
	}

}
