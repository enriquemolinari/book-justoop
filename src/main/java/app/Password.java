package app;

import jakarta.persistence.Embeddable;
import lombok.*;
import app.api.BusinessException;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"password"})
class Password {
    private String password;
    static final String NOT_VALID_PASSWORD = "Password is not valid";
    static final String CAN_NOT_CHANGE_PASSWORD = "Some of the provided information is not valid to change the password";
    static final String PASSWORDS_MUST_BE_EQUALS = "Passwords must be equals";

    public Password(String password) {
        checkValidPassword(password);
        this.password = encript(password);
    }

    private String encript(String nonEncriptedPassword) {
        // encript password here !
        return nonEncriptedPassword;
    }

    private void checkValidPassword(String password) {
        String pwd = new NotBlankString(password, NOT_VALID_PASSWORD).value();
        if (pwd.length() < 12) {
            throw new BusinessException(NOT_VALID_PASSWORD);
        }
    }

    public void change(String currentPassword, String newPassword1,
                       String newPassword2) {
        checkValidPassword(newPassword1);
        if (!this.password.equals(currentPassword)) {
            throw new BusinessException(CAN_NOT_CHANGE_PASSWORD);
        }
        checkPasswordsMatch(newPassword2, newPassword1);
        this.password = newPassword1;
    }

    void checkPasswordsMatch(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            throw new BusinessException(PASSWORDS_MUST_BE_EQUALS);
        }
    }

    boolean hasPassword(String password) {
        return this.equals(new Password(password));
    }
}
