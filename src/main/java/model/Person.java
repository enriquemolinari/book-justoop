package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class Person {

	static final String NAME_MUST_NOT_BE_BLANK = "Name must not be blank";
	static final String SURNAME_MUST_NOT_BE_BLANK = "Surname must not be blank";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private String surname;

	public Person(String name, String surname) {
		this.name = new NotBlankString(name, NAME_MUST_NOT_BE_BLANK).value();
		this.surname = new NotBlankString(surname, SURNAME_MUST_NOT_BE_BLANK)
				.value();
	}

	public boolean isNamed(String aName) {
		return this.fullName().equals(aName);
	}

	String fullName() {
		return this.name + " " + this.surname;
	}
}
