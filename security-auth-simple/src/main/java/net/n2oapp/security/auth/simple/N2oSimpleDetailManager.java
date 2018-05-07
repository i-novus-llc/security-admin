package net.n2oapp.security.auth.simple;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.auth.User;
import net.n2oapp.security.auth.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetailsManager для схемы sec
 */
@Service
public class N2oSimpleDetailManager implements UserDetailsManager {
    private static final Logger log = LoggerFactory.getLogger(N2oSimpleDetailManager.class);


    private AuthenticationProvider authenticationProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }


    @Override
    public void createUser(UserDetails userDetails) {
        User user = (User) userDetails;
        userService.create(map(user, null));
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        User user = (User) userDetails;
        Integer id = null;
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(user.getUsername());
        criteria.setSize(1);
        Page<net.n2oapp.security.admin.api.model.User> users = userService.findAll(criteria);
        if (users.getTotalElements() == 1) {
           id =  users.getContent().get(0).getId();
        }
        else {
            if (users.getTotalElements() > 1)
                throw new IncorrectResultSizeDataAccessException(
                        "More than one user found with name '" + user.getUsername() + "'", 1);
        }
        userService.update(map(user, id));
    }

    @Override
    public void deleteUser(String username) {
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        criteria.setSize(1);
        Page<net.n2oapp.security.admin.api.model.User> all = userService.findAll(criteria);
        if (all.getTotalElements() == 1)
            all.getContent().get(0).getId();
        else
            throw new IllegalArgumentException("Can't delete element. There are  some elements with such username.");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();



        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (authenticationProvider != null) {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                    username, newPassword));
        }
        SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser, newPassword));
    }

    @Override
    public boolean userExists(String username) {
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        criteria.setSize(1);
        Page<net.n2oapp.security.admin.api.model.User> users = userService.findAll(criteria);
        if (users.getTotalElements() > 1) {
            throw new IncorrectResultSizeDataAccessException(
                    "More than one user found with name '" + username + "'", 1);
        }
        return users.getTotalElements() == 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        criteria.setSize(1);
        Page<net.n2oapp.security.admin.api.model.User> users = userService.findAll(criteria);
        if (users.getTotalElements() == 0) {
            log.debug("Query returned no results for user '" + username + "'");
            throw new UsernameNotFoundException(String.format("User with username {0} not found", username));
        }
        User user = map(users.getContent().get(0)); // contains no GrantedAuthority[]
        List<GrantedAuthority> authorities = loadAuthorities(username);
        if (authorities == null || authorities.isEmpty())
            return user;
        return new User(username, user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), authorities, user.getSurname(), user.getName(), user.getPatronymic(), user.getEmail());
    }


    private UserForm map(User user, Integer id) {
        if (user == null) return null;
        UserForm userForm = new UserForm();
        userForm.setId(id);
        userForm.setUsername(user.getUsername());
        userForm.setPassword(user.getPassword());
        userForm.setSurname(user.getSurname());
        userForm.setName(user.getName());
        userForm.setPatronymic(user.getPatronymic());
        userForm.setIsActive(true);
        return userForm;
    }

    private User map(net.n2oapp.security.admin.api.model.User user) {
        String password = user.getPasswordHash();
        String email = user.getEmail();
        String surname = user.getSurname();
        String name = user.getName();
        String patronymic = user.getPatronymic();
        boolean enabled = user.getIsActive();
        return new User(user.getUsername(), password, enabled, true, true, true,
                AuthorityUtils.NO_AUTHORITIES, surname, name, patronymic, email);


    }

    private List<GrantedAuthority> loadAuthorities(String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Integer> tempRoleAuthorities = new ArrayList<>();
        UserCriteria criteria = new UserCriteria();
        criteria.setUsername(username);
        criteria.setSize(1);
        Page<net.n2oapp.security.admin.api.model.User> users = userService.findAll(criteria);
        if (users.getTotalElements() > 1) {
            throw new IncorrectResultSizeDataAccessException(
                    "More than one user found with name '" + username + "'", 1);
        }
        if (users.getTotalElements() == 1) {
            tempRoleAuthorities = users.getContent().get(0).getRoles().stream().map(Role::getId).collect(Collectors.toList());
        }
        if (tempRoleAuthorities == null) return null;
        tempRoleAuthorities.forEach(r -> {
            Role role = roleService.getById(r);
            if (role.getPermissions() != null) {
                authorities.addAll(role.getPermissions().stream().map(p -> new PermissionGrantedAuthority(p.getCode())).collect(Collectors.toList()));
            }
        });

        List<RoleGrantedAuthority> roleAuthorities = tempRoleAuthorities.stream()
                .map(r -> new RoleGrantedAuthority(r.toString())).collect(Collectors.toList());
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

