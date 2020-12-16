package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.auth.common.authority.PermissionGrantedAuthority;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import net.n2oapp.security.auth.common.authority.SystemGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class RefreshTokenUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public RefreshTokenUserDetailsService(UserRepository userRepository, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsToken userDetailsToken = obtainUserDetailsToken(username);
        User user = userDetailsService.loadUserDetails(userDetailsToken);
        return map(user, username);
    }

    private UserDetailsToken obtainUserDetailsToken(String username) {
        UserEntity userEntity = userRepository.findOneByUsernameIgnoreCase(username);
        UserDetailsToken userDetailsToken = new UserDetailsToken();
        if (userEntity != null) {
            userDetailsToken.setUsername(userEntity.getUsername() == null ? username : userEntity.getUsername());
            userDetailsToken.setEmail(userEntity.getEmail());
            userDetailsToken.setExtUid(userEntity.getExtUid());
            userDetailsToken.setName(userEntity.getName());
            userDetailsToken.setSurname(userEntity.getSurname());
            userDetailsToken.setPatronymic(userEntity.getPatronymic());
            userDetailsToken.setRoleNames(userEntity.getRoleList().stream().map(RoleEntity::getCode).collect(Collectors.toList()));
        }
        return userDetailsToken;
    }

    private net.n2oapp.security.auth.common.User map(User user, String username) {
        List<GrantedAuthority> authorities = extractAuthorities(user);
        net.n2oapp.security.auth.common.User userDetails = new net.n2oapp.security.auth.common.User(username, "N/A", authorities);
        if (user != null) {
            userDetails.setEmail(user.getEmail());
            userDetails.setSurname(user.getSurname());
            userDetails.setName(user.getName());
            userDetails.setPatronymic(user.getPatronymic());
            userDetails.setUserLevel(user.getUserLevel() != null ? user.getUserLevel().toString() : null);
            userDetails.setOrganization(user.getOrganization() != null ? user.getOrganization().getCode() : null);
            userDetails.setDepartment(user.getDepartment() != null ? user.getDepartment().getCode() : null);
            userDetails.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : null);
            userDetails.setRegion(user.getRegion() != null ? user.getRegion().getCode() : null);
        }
        return userDetails;
    }

    private List<GrantedAuthority> extractAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (nonNull(user) && nonNull(user.getRoles())) {
            authorities.addAll(user.getRoles().stream().map(r -> new RoleGrantedAuthority(r.getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(r -> nonNull(r.getPermissions())).flatMap(r -> r.getPermissions().stream())
                    .map(p -> new PermissionGrantedAuthority(p.getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(role -> nonNull(role.getSystem())).
                    map(role -> new SystemGrantedAuthority(role.getSystem().getCode())).collect(Collectors.toList()));
            authorities.addAll(user.getRoles().stream().filter(r -> nonNull(r.getPermissions())).flatMap(r -> r.getPermissions().stream())
                    .filter(permission -> nonNull(permission.getSystem())).map(p -> new SystemGrantedAuthority(p.getSystem().getCode())).collect(Collectors.toList()));
            authorities = authorities.stream().distinct().collect(Collectors.toList());
        }
        return authorities;
    }
}
