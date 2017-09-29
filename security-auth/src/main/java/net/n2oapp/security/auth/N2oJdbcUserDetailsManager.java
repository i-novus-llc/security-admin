package net.n2oapp.security.auth;

import net.n2oapp.security.auth.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserDetailsManager для схемы sec
 */
@Service
public class N2oJdbcUserDetailsManager implements UserDetailsManager {
    private static final Logger log = LoggerFactory.getLogger(N2oJdbcUserDetailsManager.class);

    private final static String INSERT_USER = "insert into sec.user(username, password, email, surname, name, patronymic, is_active) values(:username, :password, :email, :surname, :name, :patronymic, :isActive);";
    private final static String UPDATE_USER = "update sec.user set email = :email, password = :password, surname = :surname, name = :name, patronymic = :patronymic, is_active = :isActive where username = :username; ";
    private final static String UPDATE_PASSWORD = "update sec.user set password = :password where username = :username; ";
    private final static String DELETE_USER = "delete from sec.user where username = :username;";
    private final static String INSERT_USER_ROLE = "insert into sec.user_role(user_id, role_id) values(:userId, :roleId);";
    public static final String USER_EXISTS = "select username from sec.user where username = :username;";
    private final static String GET_USER_BY_USERNAME = "select password, email, surname, name, patronymic, is_active from sec.user  where username = :username;";
    private final static String GET_ROLES_BY_USERNAME = "select r.id, r.code from sec.user u join sec.user_role ur on ur.user_id=u.id join sec.role r on r.id=ur.role_id where u.username = :username;";
    private final static String GET_PERMISSIONS_BY_ROLE_ID = "select p.code from sec.permission p join sec.role_permission rp on p.id=rp.permission_id where rp.role_id = :roleId;";

    private TransactionTemplate transactionTemplate;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private PasswordEncoder passwordEncoder;

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void createUser(UserDetails userDetails) {
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                User user = (User) userDetails;
                SqlParameterSource namedParameters =
                        new MapSqlParameterSource("username", user.getUsername())
                                .addValue("password", passwordEncoder.encode(user.getPassword()))
                                .addValue("email", user.getEmail())
                                .addValue("surname", user.getSurname())
                                .addValue("name", user.getName())
                                .addValue("patronymic", user.getPatronymic())
                                .addValue("isActive", true);
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(INSERT_USER, namedParameters, keyHolder, new String[]{"id"});
                Integer userId = (Integer) keyHolder.getKey();
                if (user.getAuthorities() != null) {
                    user.getAuthorities().stream().filter(a -> a instanceof RoleGrantedAuthority).forEach(a -> {
                        SqlParameterSource params =
                                new MapSqlParameterSource("userId", userId)
                                        .addValue("roleId", Integer.parseInt(a.getAuthority()));
                        jdbcTemplate.update(INSERT_USER_ROLE, params);
                    });
                }
                return user;
            }
        });
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        User user = (User) userDetails;
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("username", user.getUsername())
                        .addValue("password", passwordEncoder.encode(user.getPassword()))
                        .addValue("email", user.getEmail())
                        .addValue("surname", user.getSurname())
                        .addValue("name", user.getName())
                        .addValue("patronymic", user.getPatronymic())
                        .addValue("isActive", true);
        jdbcTemplate.update(UPDATE_USER, namedParameters);
    }

    @Override
    public void deleteUser(String username) {
        SqlParameterSource namedParameters =
                new MapSqlParameterSource("username", username);
        jdbcTemplate.update(DELETE_USER, namedParameters);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();
        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
       /* if (authenticationManager != null) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, oldPassword));
        }*/
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username)
                .addValue("password", passwordEncoder.encode(newPassword));
        jdbcTemplate.update(UPDATE_PASSWORD, namedParameters);
        SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser, newPassword));
    }

    @Override
    public boolean userExists(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        List<String> users = jdbcTemplate.queryForList(USER_EXISTS, params, String.class);
        if (users.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(
                    "More than one user found with name '" + username + "'", 1);
        }
        return users.size() == 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        List<User> users = jdbcTemplate.query(GET_USER_BY_USERNAME, params, (rs, i) -> {
            String password = rs.getString(1);
            String email = rs.getString(2);
            String surname = rs.getString(3);
            String name = rs.getString(4);
            String patronymic = rs.getString(5);
            boolean enabled = rs.getBoolean(6);
            return new User(username, password, enabled, true, true, true,
                    AuthorityUtils.NO_AUTHORITIES, surname, name, patronymic, email);
        });
        if (users.size() == 0) {
            log.debug("Query returned no results for user '" + username + "'");
            throw new UsernameNotFoundException(String.format("User with username {0} not found", username));
        }
        User user = users.get(0); // contains no GrantedAuthority[]
        List<GrantedAuthority> authorities = loadAuthorities(username);
        if (authorities == null || authorities.isEmpty())
            return user;
        return new User(username, user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), authorities, user.getSurname(), user.getName(), user.getPatronymic(), user.getEmail());
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private List<GrantedAuthority> loadAuthorities(String username) {
        class TempRole {
            private Integer id;
            private String code;

            public TempRole(Integer id, String code) {
                this.id = id;
                this.code = code;
            }

            public Integer getId() {
                return id;
            }

            public String getCode() {
                return code;
            }
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        List<TempRole> tempRoleAuthorities = jdbcTemplate.query(GET_ROLES_BY_USERNAME, params, (rs, i) -> {
            Integer id = rs.getInt(1);
            String code = rs.getString(2);
            return new TempRole(id, code);
        });
        if (tempRoleAuthorities == null) return null;
        tempRoleAuthorities = tempRoleAuthorities.stream().filter(r -> r.getCode() != null).collect(Collectors.toList());
        tempRoleAuthorities.forEach(r -> {
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("roleId", r.getId());
            authorities.addAll(jdbcTemplate.query(GET_PERMISSIONS_BY_ROLE_ID, roleParams, (rs, i) -> {
                String permission = rs.getString(1);
                return new PermissionGrantedAuthority(permission);
            }));
        });
        List<RoleGrantedAuthority> roleAuthorities = tempRoleAuthorities.stream()
                .map(r -> new RoleGrantedAuthority(r.getCode())).collect(Collectors.toList());
        authorities.addAll(roleAuthorities);
        return authorities;
    }

  /*  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }*/

    protected Authentication createNewAuthentication(Authentication currentAuth,
                                                     String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }
}

