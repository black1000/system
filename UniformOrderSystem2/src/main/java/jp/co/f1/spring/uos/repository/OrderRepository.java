package jp.co.f1.spring.uos.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	public Iterable<Order> findByOrderno(String orderno);

	public Optional<Order> findByIsbn(String orderno);

	
}
