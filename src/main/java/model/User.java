package model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ClientUser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"userName"})
class User {

	static final String CAN_NOT_CHANGE_PASSWORD = "Some of the provided information is not valid to change the password";
	static final String POINTS_MUST_BE_GREATER_THAN_ZERO = "Points must be greater than zero";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true)
	private String userName;
	@OneToOne
	private Person person;
	@Embedded
	private Email email;
	// this must not escape by any means out of this object
	@Embedded
	private Password password;

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
	private List<Sale> purchases;

	private int points;

	public User(Person person, String userName, String email, String password) {
		this.person = person;
		this.userName = new NotBlankString(userName, "").value();
		this.password = new Password(password);
		this.email = new Email(email);
		this.points = 0;
		this.purchases = new ArrayList<>();
	}

	boolean isPassword(String password) {
		return this.password.equals(new Password(password));
	}

	public void changePassword(String currentPassword, String newPassword1,
			String newPassword2) {
		if (!isPassword(currentPassword)) {
			throw new BusinessException(CAN_NOT_CHANGE_PASSWORD);
		}
		if (!newPassword1.equals(newPassword2)) {
			throw new BusinessException(CAN_NOT_CHANGE_PASSWORD);
		}
		this.password = new Password(newPassword1);
	}

	void newEarnedPoints(int points) {
		if (points <= 0) {
			throw new BusinessException(POINTS_MUST_BE_GREATER_THAN_ZERO);
		}
		this.points += points;
	}

	public boolean hasPoints(int points) {
		return this.points == points;
	}

	public String userName() {
		return userName;
	}

	public boolean hasName(String aName) {
		return this.person.hasName(aName);
	}

	public boolean hasSurname(String aSurname) {
		return this.person.aSurname(aSurname);
	}

	public boolean hasUsername(String aUserName) {
		return this.userName.equals(aUserName);
	}

	public String email() {
		return this.email.asString();
	}

	void newPurchase(Sale sale, int pointsWon) {
		this.newEarnedPoints(pointsWon);
		this.purchases.add(sale);
	}
}
