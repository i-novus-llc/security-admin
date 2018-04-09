package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;

import java.util.List;

/**
 * Загрузка данных пользователя с его ролями и правами доступа
 * с одновременным обновлением данных в нашей системе
 */
public interface UserDetailsService {

    User loadUserDetails(UserDetailsToken userDetails);
}
