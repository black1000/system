package jp.co.f1.spring.uos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.OrderDetail;
import jp.co.f1.spring.uos.entity.OrderDetailId;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, OrderDetailId> {

	
	public Iterable<OrderDetail> findById_Orderno(String orderno);


}



