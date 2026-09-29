package jp.co.f1.spring.uos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="orderdetail")
public class OrderDetail {
	
	@EmbeddedId
	private OrderDetailId id = new OrderDetailId();
	
	
//	//注文番号
//	@Column(length = 20)
//	@JoinColumn(name = "orderno", referencedColumnName = "orderno", insertable = false, updatable = false)
//	private String orderno;
//	
	public String getOrderno() {
		return this.id.getOrderno();
	}

	public void setOrderno(String orderno) {
		this.id.setOrderno(orderno); 
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
	
//	
//	//商品番号
//	@Id
//	@JoinColumn(name = "productno", referencedColumnName = "productno", insertable = false, updatable = false)
//	private String productno;
	
	public String getProductno() {
		return this.id.getProductno();
	}

	public void setProductno(String productno) {
		this.id.setProductno(productno); 
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
