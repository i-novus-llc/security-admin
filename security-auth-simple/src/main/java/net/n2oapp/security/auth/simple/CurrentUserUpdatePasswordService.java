package net.n2oapp.security.auth.simple;

import net.n2oapp.context.StaticSpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Objects;

/**
 * Изменение пароля текущего пользователя
 *
 * @author igafurov
 * @since 13.12.2017
 */
public class CurrentUserUpdatePasswordService {

    private UserDetailsManager userDetailsManager = StaticSpringContext.getBean(UserDetailsManager.class);
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void updatePassword(String oldPassword, String newPassword, String newPasswordConfirm, String username) {
        String errorCode = checkErrorCode(oldPassword, newPassword, newPasswordConfirm, username);
        if (errorCode != null) {
            throw new RuntimeException(errorCode);
        }
        updatePassword(oldPassword, newPassword, userDetailsManager);
    }

    private String checkErrorCode(String oldPassword, String newPassword, String newPasswordConfirm, String username) {
        if (!isPasswordValid(oldPassword, username))
            throw new RuntimeException("Old password is incorrect");
        if (!Objects.equals(newPassword, newPasswordConfirm))
            throw new RuntimeException("Passwords doesn't match");
        return null;
    }

    public boolean isPasswordValid(String oldPassword, String username) {
        UserDetails existedUser = userDetailsManager.loadUserByUsername(username);
        if (!passwordEncoder.matches(oldPassword, existedUser.getPassword())) {
            return false;
        }
        return true;
    }

    private void updatePassword(String oldPassword, String newPassword, UserDetailsManager userDetailsManager) {
        userDetailsManager.changePassword(oldPassword, newPassword);
    }
}
