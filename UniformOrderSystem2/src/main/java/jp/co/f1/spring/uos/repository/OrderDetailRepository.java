package jp.co.f1.spring.uos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, String> {
<<<<<<< HEAD
	public Iterable<OrderDetail> findByOrderno(String orderno);
	
=======
	public List<jp.co.f1.spring.uos.controller.OrderDetail> findByOrderno(String orderno);
>>>>>>> bee29a73b7a8a7fcfade61dd03d643340ee0fb88


}



