package jp.co.f1.spring.uos.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.Uniform;
import jp.co.f1.spring.uos.entity.Order;
import jp.co.f1.spring.uos.entity.OrderDetail;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	public Iterable<Order> findByUserid(String userid);
}
