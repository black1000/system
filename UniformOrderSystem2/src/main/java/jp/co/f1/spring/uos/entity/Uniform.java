package jp.co.f1.spring.uos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="uniforminfo")
public class Uniform {
	public interface Group1{}
	public interface Group2{}
	@GroupSequence({
		Group1.class,
		Group2.class
	})
	public interface All{}

	// 商品番号
	@Id
	@Column(length = 20)
	@NotEmpty(message="商品番号を入力してください", groups = Group1.class)
	private String productno;

	public String getProductno() {
		return productno;
	}

	public void setProductno(String productno) {
		this.productno = productno;
	}

	// 商品名
	@Column(length = 100, nullable = true)
	@NotEmpty(message="商品名を入力してください", groups = Group1.class)
	private String productname;

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	// 価格
	@Column(length = 11, nullable = true)
	@NotEmpty(message="価格を入力してください", groups = Group1.class)
	@Pattern(regexp = "^[0-9]+$", message = "価格は数字のみで入力してください", groups = Group2.class)
	
	private int price;

	public int getPrice() {
		return price;
	} 

	public void setPrice(int  price) {
		this.price = price;
	}
	// 在庫数
	@Column(length = 11, nullable = true)
	@NotEmpty(message="在庫数を入力してください", groups = Group1.class)
	@Pattern(regexp = "^[0-9]+$", message = "在庫数は数字のみで入力してください", groups = Group2.class)
	
	private int stock;

	public int getStock() {
		return stock;
	} 

	public void setStock(int stock) {
		this.stock = stock;
	}
	
}