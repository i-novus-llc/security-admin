package net.n2oapp.security.auth.common;

import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static net.n2oapp.security.auth.common.TestConstants.*;
import static org.junit.Assert.*;

public class UserTest {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Test
    public void testConstructor_withUsernameArg() {
        User user = new User(SOME_USERNAME);
        assertNotNull(user);
        assertEquals(SOME_USERNAME, user.getUsername());

        assertEquals(StringUtils.EMPTY, user.getPassword());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(DEFAULT_ROLE, listRole.get(0));

        assertEquals(SOME_USERNAME, user.getUserFullName());
        assertEquals(SOME_USERNAME + StringUtils.SPACE, user.getUserShortName());
        assertEquals(SOME_USERNAME, user.getUserNameSurname());
    }

    @Test
    public void testConstructor_withThreeArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD, Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)));
        assertNotNull(user);
        assertEquals(SOME_USERNAME, user.getUsername());
        assertEquals(SOME_PASSWORD, user.getPassword());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(DEFAULT_ROLE, listRole.get(0));

        assertEquals(SOME_USERNAME, user.getUserFullName());
        assertEquals(SOME_USERNAME + StringUtils.SPACE, user.getUserShortName());
        assertEquals(SOME_USERNAME, user.getUserNameSurname());
    }

    @Test
    public void testConstructor_withSurnameNamePatronymicEmailArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                             Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)),
                             SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC,
                             SOME_EMAIL);
        assertNotNull(user);
        assertEquals(SOME_USERNAME, user.getUsername());
        assertEquals(SOME_PASSWORD, user.getPassword());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(DEFAULT_ROLE, listRole.get(0));
        assertEquals(SOME_SURNAME, user.getSurname());
        assertEquals(SOME_NAME, user.getName());
        assertEquals(SOME_PATRONYMIC, user.getPatronymic());
        assertEquals(SOME_EMAIL, user.getEmail());

        assertEquals(getUserFullName(), user.getUserFullName());
        assertEquals(getUserShortName(), user.getUserShortName());
        assertEquals(getUserNameSurname(), user.getUserNameSurname());
    }

    @Test
    public void testConstructor_withFlagArgs() {

        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                             Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                             Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)));
        assertNotNull(user);
        assertEquals(SOME_USERNAME, user.getUsername());
        assertEquals(SOME_PASSWORD, user.getPassword());

        assertFalse(user.isEnabled());
        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isCredentialsNonExpired());
        assertFalse(user.isAccountNonLocked());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(DEFAULT_ROLE, listRole.get(0));

        assertEquals(SOME_USERNAME, user.getUserFullName());
        assertEquals(SOME_USERNAME + StringUtils.SPACE, user.getUserShortName());
        assertEquals(SOME_USERNAME, user.getUserNameSurname());
    }

    @Test
    public void testConstructor_withAllArgs() {
        User user = new User(SOME_USERNAME, SOME_PASSWORD,
                Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                Collections.singleton(new RoleGrantedAuthority(DEFAULT_ROLE)),
                SOME_SURNAME, SOME_NAME, SOME_PATRONYMIC,
                SOME_EMAIL);
        assertNotNull(user);
        assertEquals(SOME_USERNAME, user.getUsername());
        assertEquals(SOME_PASSWORD, user.getPassword());

        assertFalse(user.isEnabled());
        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isCredentialsNonExpired());
        assertFalse(user.isAccountNonLocked());

        List<String> listRole = user.getRoles();
        assertFalse(listRole.isEmpty());
        assertEquals(DEFAULT_ROLE, listRole.get(0));

        assertEquals(SOME_SURNAME, user.getSurname());
        assertEquals(SOME_NAME, user.getName());
        assertEquals(SOME_PATRONYMIC, user.getPatronymic());
        assertEquals(SOME_EMAIL, user.getEmail());

        assertEquals(getUserFullName(), user.getUserFullName());

        assertEquals(getUserShortName(), user.getUserShortName());

        assertEquals(getUserNameSurname(), user.getUserNameSurname());
    }

    @Test
    public void testGetUserShortName_withOnlyName() {

        User user = new User(SOME_USERNAME);
        assertNotNull(user);

        user.setName(SOME_NAME);
        assertEquals(SOME_NAME, user.getUserShortName());
    }
}
