package jp.co.f1.spring.uos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, String> {
	public List<jp.co.f1.spring.uos.controller.OrderDetail> findByOrderno(String orderno);


}



