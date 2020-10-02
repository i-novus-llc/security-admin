package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import java.util.Collections;
import java.util.List;

import static net.n2oapp.security.auth.common.TestConstants.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class UserTest {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Test
    public void testConstructor_withUsernameArg() {
        User user = new User(SOME_USERNAME);
        assertNotNull(user);
        assertEquals(user.getUsername(), SOME_USERNAME);

        assertEquals(user.getPassword(), StringUtils.EMPTY);

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(listRole.get(0), DEFAULT_ROLE);

        assertEquals(user.getUserFullName(), SOME_USERNAME);
        assertEquals(user.getUserShortName(), SOME_USERNAME + StringUtils.SPACE);
        assertEquals(user.getUserNameSurname(), SOME_USERNAME);
    }

    @Test
    public void testConstructor_withThreeArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD, Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)));
        assertNotNull(user);
        assertEquals(user.getUsername(), SOME_USERNAME);
        assertEquals(user.getPassword(), SOME_PASSWORD);

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(listRole.get(0), DEFAULT_ROLE);

        assertEquals(user.getUserFullName(), SOME_USERNAME);
        assertEquals(user.getUserShortName(), SOME_USERNAME + StringUtils.SPACE);
        assertEquals(user.getUserNameSurname(), SOME_USERNAME);
    }

    @Test
    public void testConstructor_withSurnameNamePatronymicEmailArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                             Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)),
                             SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC,
                             SOME_EMAIL);
        assertNotNull(user);
        assertEquals(user.getUsername(), SOME_USERNAME);
        assertEquals(user.getPassword(), SOME_PASSWORD);

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(listRole.get(0), DEFAULT_ROLE);
        assertEquals(user.getSurname(), SOME_SURNAME);
        assertEquals(user.getName(), SOME_NAME);
        assertEquals(user.getPatronymic(), SOME_PATRONYMIC);
        assertEquals(user.getEmail(), SOME_EMAIL);

        assertEquals(user.getUserFullName(), String.format(USER_FULLNAME_FORMAT, SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC));

        assertEquals(user.getUserShortName(),String.format(USER_SHORTNAME_FORMAT, SOME_SURNAME,
                SOME_NAME.substring(0, 1).toUpperCase(),
                SOME_PATRONYMIC.substring(0, 1).toUpperCase()));

        assertEquals(user.getUserNameSurname(),
                String.format(USER_NAME_FULLNAME_FORMAT, SOME_NAME, SOME_SURNAME));
    }

    @Test
    public void testConstructor_withFlagArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                             Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                             Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)));
        assertNotNull(user);
        assertEquals(user.getUsername(), SOME_USERNAME);
        assertEquals(user.getPassword(), SOME_PASSWORD);

        assertFalse(user.isEnabled());
        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isCredentialsNonExpired());
        assertFalse(user.isAccountNonLocked());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(listRole.get(0), DEFAULT_ROLE);

        assertEquals(user.getUserFullName(), SOME_USERNAME);
        assertEquals(user.getUserShortName(), SOME_USERNAME + StringUtils.SPACE);
        assertEquals(user.getUserNameSurname(), SOME_USERNAME);
    }

    @Test
    public void testConstructor_withAllArgs() {
        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)),
                SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC,
                SOME_EMAIL);
        assertNotNull(user);
        assertEquals(user.getUsername(), SOME_USERNAME);
        assertEquals(user.getPassword(), SOME_PASSWORD);

        assertFalse(user.isEnabled());
        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isCredentialsNonExpired());
        assertFalse(user.isAccountNonLocked());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(listRole.get(0), DEFAULT_ROLE);

        assertEquals(user.getSurname(), SOME_SURNAME);
        assertEquals(user.getName(), SOME_NAME);
        assertEquals(user.getPatronymic(), SOME_PATRONYMIC);
        assertEquals(user.getEmail(), SOME_EMAIL);

        assertEquals(user.getUserFullName(), String.format(USER_FULLNAME_FORMAT, SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC));

        assertEquals(user.getUserShortName(),String.format(USER_SHORTNAME_FORMAT, SOME_SURNAME,
                SOME_NAME.substring(0, 1).toUpperCase(),
                SOME_PATRONYMIC.substring(0, 1).toUpperCase()));

        assertEquals(user.getUserNameSurname(),
                String.format(USER_NAME_FULLNAME_FORMAT, SOME_NAME, SOME_SURNAME));
    }

    @Test
    public void testGetUserShortName_withOnlyName() {

        User user = new User(SOME_USERNAME);
        assertNotNull(user);

        user.setName(SOME_NAME);
        assertEquals(user.getUserShortName(), SOME_NAME);
    }
}
