package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Transactional
public class UserDetailsServiceSql implements UserDetailsService {

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) {
        return new User();
    }
}
