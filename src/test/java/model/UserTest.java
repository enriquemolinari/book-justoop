package model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UserTest {

	@Test
	public void userCanBeCreated() {
		var u = createUserEnrique();

		assertTrue(u.isPassword("Ab138RtoUjkL"));
		assertTrue(u.hasName("Enrique"));
		assertTrue(u.hasSurname("Molinari"));
		assertTrue(u.hasUsername("enriquemolinari"));
	}

	private User createUserEnrique() {
		var u = new User(new Person("Enrique", "Molinari"), "enriquemolinari",
				"enrique.molinari@gmail.com", "Ab138RtoUjkL");
		return u;
	}

	@Test
	public void userEmailIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new User(new Person("Enrique", "Molinari"), "emolinari",
					"enrique.molinarigmail.com", "Ab138RtoUjkL");
		});

		assertTrue(e.getMessage().equals(Email.NOT_VALID_EMAIL));
	}

	@Test
	public void userPasswordIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new User(new Person("Enrique", "Molinari"), "emolinari",
					"enrique.molinari@gmail.com", "abcAdif");
		});

		assertTrue(e.getMessage().equals(Password.NOT_VALID_PASSWORD));
	}

	@Test
	public void changePasswordCurrentPasswordNotTheSame() {
		var u = createUserEnrique();

		Exception e = assertThrows(BusinessException.class, () -> {
			u.changePassword("abchd1239876", "Abcdefghijkl", "Abcdefghijkl");
		});

		assertTrue(e.getMessage().equals(User.CAN_NOT_CHANGE_PASSWORD));
	}

	@Test
	public void changePasswordNewPassword1And2DoesNotMatch() {
		var u = createUserEnrique();

		Exception e = assertThrows(BusinessException.class, () -> {
			u.changePassword("Ab138RtoUjkL", "Abcdefghrjkl", "Abcdefghijkl");
		});

		assertTrue(e.getMessage().equals(User.CAN_NOT_CHANGE_PASSWORD));
	}

	@Test
	public void changePasswordNewPasswordNotValid() {
		var u = createUserEnrique();

		Exception e = assertThrows(BusinessException.class, () -> {
			u.changePassword("Ab138RtoUjkL", "Abcdefgh", "Abcdefgh");
		});

		assertTrue(e.getMessage().equals(Password.NOT_VALID_PASSWORD));
	}

	@Test
	public void changePasswordOk() {
		var u = createUserEnrique();

		u.changePassword("Ab138RtoUjkL", "Abcdefghijkl", "Abcdefghijkl");

		assertTrue(u.isPassword("Abcdefghijkl"));
	}

	@Test
	public void newCreatedUserHasZeroPoints() {
		var u = createUserEnrique();
		assertTrue(u.hasPoints(0));
	}

	@Test
	public void userEarnsSomePoints() {
		var u = createUserEnrique();
		u.newEarnedPoints(10);
		assertTrue(u.hasPoints(10));
	}

	@Test
	public void userEarnsAnInvalidNumberOfPoints() {
		var u = createUserEnrique();

		Exception e = assertThrows(BusinessException.class, () -> {
			u.newEarnedPoints(0);
		});

		assertTrue(
				e.getMessage().equals(User.POINTS_MUST_BE_GREATER_THAN_ZERO));
	}

}