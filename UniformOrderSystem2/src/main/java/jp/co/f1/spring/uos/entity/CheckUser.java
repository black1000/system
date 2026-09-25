package jp.co.f1.spring.uos.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class CheckUser {

	//ユーザーID
	@Id
	@Column(length = 20, nullable = false)
	@NotEmpty(message = "ユーザー入力値不正の為、登録できません。")
	//データベース内のカラム名にする
	private String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	// 氏名
	@Column(length = 100, nullable = false)
	@NotEmpty(message = "名前を入力してください。")
	//データベース内のカラム名にする
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//旧パスワード
	@Column(length = 100, nullable = false)
	@NotEmpty(message = "パスワードを入力してください")
	private String oldPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	//新パスワード
	//※newPasswordだけValidationアノテーションを付けない（未入力チェックはControllerで行う）
	@Column(length = 100)
	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	//新パスワード（確認用）
	@Column(length = 100, nullable = false)
	@NotEmpty(message = "新パスワード（確認用）を入力してください")
	private String confirmPassword;

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	//Email
	@Column(length = 100, nullable = false)
	@NotEmpty(message = "メールアドレスを入力してください")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	//住所
	@Column(length = 255, nullable = false)
	@NotEmpty(message = "住所を入力してください。")
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	// Authority
	@Column(length = 1,nullable = true)
	private String authority;
	
	public String getAuthority() {
		return authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}



	}