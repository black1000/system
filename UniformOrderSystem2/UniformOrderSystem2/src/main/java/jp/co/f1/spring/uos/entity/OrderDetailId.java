package jp.co.f1.spring.uos.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {

	
	/*
	 * 注文番号
	 */
	private String orderno;

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	/*
	 * 商品番号
	 */
	private String productno;

	public String getProductno() {
		return productno;
	}

	public void setProductno(String productno) {
		this.productno = productno;
	}
}
