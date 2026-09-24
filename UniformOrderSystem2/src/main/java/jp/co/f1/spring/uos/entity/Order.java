package jp.co.f1.spring.uos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Table(name = "orderinfo")

public class Order {

	// 注文No(外部キー制約)

	@Id
	@Column(length = 20)
	private String orderno;

	public String getOrderno() {
		return orderno;

	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	// ユーザID(外部キー制約)
	@Column(length = 8)
	private String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}


// 注文の合計金額
	@Column(length = 20)
	private int totalprice;

	public int getTotalPrice() {
		return totalprice;
	}

	public void setTotalPrice(int totalprice) {
		this.totalprice = totalprice;
	}

	// 注文の合計個数
	@Column(length = 20)
	private int totalquantity;

	public int getTotalQuantity() {
		return totalquantity;

	}

	public void setTotalQuantity(int totalquantity) {
		this.totalquantity = totalquantity;
	}
 // 注文日時
	@Column(length = 20)
	private Date datetime;

	public Date getDatetime() {
		return datetime;

	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	// 注文者名
	@Column(length = 100)
	private String name;
	
	public String getName() {
		return name;
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// 注文者の住所
	@Column(length = 255)
	private String address;
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	// 注文者のメールアドレス
	@Column(length = 100)
	private String email;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	// 備考
	@Column(length = 200)
	private String remarks;
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	// 入金状況
	@Column(length = 1)
	private String payment;
	
	public String getPayment() {
		return payment;
	}
	
	public void setPayment(String payment) {
		this.payment = payment;
		
	}
	
	// 発送状況
	@Column(length = 1)
	private String shipment;
	
	public String getShipment() {
		return shipment;
	}
	
	public void setShipment(String shipment) {
		this.shipment = shipment;
		
	}
	

	// OrderDetailオブジェクト
	@ManyToOne
	@JoinColumn(name = "orderno", referencedColumnName = "orderno", insertable = false, updatable = false)
	private  OrderDetail orderdetail;

	public OrderDetail getOrderDetail() {
		return orderdetail;

	}

	public void setOrderDetail(OrderDetail orderdetail) {
		this.orderdetail = orderdetail;
	}
}
