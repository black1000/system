package jp.co.f1.spring.uos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, String> {

	public Iterable<OrderDetail> findByOrderno(String orderno);
	

<<<<<<< HEAD
=======


>>>>>>> b087766432f08ede320c69fb78f44c9ed95d3eef
}



