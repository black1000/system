package jp.co.f1.spring.uos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	public Optional<User> findByUserid(String userid);
}
