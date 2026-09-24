package jp.co.f1.spring.uos.dao;


import java.util.ArrayList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.Uniform;


@Repository
public class UniformDAO {
	
	// エンティティマネージャー
	private EntityManager entityManager;
	
	// クエリ生成用インスタンス
	private CriteriaBuilder builder;
	
	// クエリ実行用インスタンス
	private CriteriaQuery<Uniform> query;
	
	// 検索されるエンティティのルート
	private Root<Uniform> root;
	
	/**
	 * コンストラクタ（DB接続準備）
	 */
	public UniformDAO(EntityManager entityManager) {
		
		// EntityManager取得
		this.entityManager = entityManager;
		
		// クエリ生成用インスタンス
		builder = entityManager.getCriteriaBuilder();
		
		// クエリ実行用インスタンス
		query = builder.createQuery(Uniform.class);
		
		// 検索されるエンティティのルート
		root = query.from(Uniform.class);
		
		
	}
	
	/**
	 * 商品情報検索
	 * @param String productno
	 * @param String productname
	 * @param int price
	 *  @param int stock
	 * @return ArrayList<Uniform> uniform_list
	 * 
	 */
	public ArrayList<Uniform> find(String productno, String productname, int price,int stock){
		// SELECT句設定
		query.select(root);
		
		// WHERE句設定
		query.where(
			builder.like(root.get("productno"),"%" + productno + "%"),
			builder.like(root.get("productname"),"%" + productname + "%"),
			builder.like(root.get("price"),"%" + price + "%"),
			builder.like(root.get("stock"),"%" + stock + "%")
				
		);
		
		// クエリ実行
		return (ArrayList<Uniform>)entityManager.createQuery(query).getResultList();
			
			
	}
	

}

