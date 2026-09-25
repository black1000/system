package jp.co.f1.spring.uos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.f1.spring.uos.entity.Uniform;


@Repository
public interface UniformRepository extends JpaRepository<Uniform , String>{
	public Optional<Uniform> findByProductno(String productno);




	public List<Uniform> findAllByOrderByProductnoAsc();

}
