package jp.co.f1.spring.uos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="orderdetail")
public class OrderDetail {
	
	//注文番号
	@Column(length = 20)
	@JoinColumn(name = "orderno", referencedColumnName = "orderno", insertable = false, updatable = false)
	private String orderno;
	
	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	
	
	//価格
	@Column(length = 20)
	private int price;
	
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	
	//注文個数
	@Column(length = 20)
	private int quantity;
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	//商品番号
	@Id
	@JoinColumn(name = "productno", referencedColumnName = "productno", insertable = false, updatable = false)
	private String productno;
	
	public String getProductno() {
		return productno;
	}

	public void setProductno(String productno) {
		this.productno = productno;
	}
	
	
	//商品名
	@Column(length = 200)
	private String productname;
	
	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

}
