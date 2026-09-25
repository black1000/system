package jp.co.f1.spring.uos.dao;

import java.util.ArrayList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.User;

@Repository
public class UserDAO {
	// エンティティマネージャー
	private EntityManager entityManager;

	// クエリ生成用インスタンス
	private CriteriaBuilder builder;

	// クエリ実行用インスタンス
	private CriteriaQuery<User> query;

	// 検索されるエンティティのルート
	private Root<User> root;

	/**
		 * コンストラクタ（DB接続準備）
		 */
		public UserDAO(EntityManager entityManager) {
			// EntityManager取得
			this.entityManager = entityManager;
			// クエリ生成用インスタンス
			builder = entityManager.getCriteriaBuilder();
			// クエリ実行用インスタンス
			query = builder.createQuery(User.class);
			// 検索されるエンティティのルート
			root = query.from(User.class);
		}

	/**
	 * ユーザー情報検索
	 * @param String userid
	 * @return ArrayList<User> user_list
	 */
	public ArrayList<User> find(String userid) {
		// SELECT句設定
		query.select(root);

		// WHERE句設定
		query.where(
				builder.like(root.get("userid"), "%" + userid + "%"));

		// クエリ実行
		return (ArrayList<User>) entityManager.createQuery(query).getResultList();
	}
}
