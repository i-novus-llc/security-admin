package net.n2oapp.security.admin.sso.keycloak.synchronization;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.impl.service.specification.UserSpecifications;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider.EXT_SYS;

/**
 * Синхронизация пользователей с Keycloak
 */

@Service
@EnableConfigurationProperties(AdminSsoKeycloakProperties.class)
public class KeycloakUserSynchronizeProvider {

    //По сколько пользователей обрабатывать за один раз
    private static final int CHUNK_SIZE = 100;

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserSynchronizeProvider.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionTemplate transaction;
    @Autowired
    private KeycloakRestUserService userService;

    /**
     * Запустить синхронизацию
     */
    public synchronized void startSynchronization() {
        logger.info("sync started");

        Integer usersCount = userService.getUsersCount();
        logger.info(usersCount + " users in keycloak");
        int pos = 0;
        List<Integer> syncedUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        while (pos < usersCount) {
            try {
                List<UserRepresentation> users = userService.searchUsers("", pos, CHUNK_SIZE);
                syncedUsers.addAll(syncUsers(users, errors));
            } catch (Exception e) {
                String message = "Failed search users from:" + pos + " to " + (pos + CHUNK_SIZE) + " " + e.getLocalizedMessage();
                logger.error(message);
                errors.add(message);
                break;
            }
            pos += CHUNK_SIZE;
        }
        logger.info(syncedUsers.size() + " users synchronized, " + errors.size() + " errors.");
        if (errors.isEmpty())
            deactivateUsers(usersCount.longValue(), syncedUsers);
    }

    private void deactivateUsers(Long usersCount, List<Integer> syncedUsers) {
        int pos = 0;
        UserCriteria criteria = new UserCriteria();
        criteria.setPageSize(CHUNK_SIZE);
        criteria.setExtSys(EXT_SYS);
        final Specification<UserEntity> specification = new UserSpecifications(criteria);

        int missings = 0;
        while (pos * CHUNK_SIZE < usersCount) {
            criteria.setPageNumber(pos);

            Page<UserEntity> all = transaction.execute(status -> userRepository.findAll(specification, criteria));
            usersCount = all.getTotalElements();

            List<UserEntity> missingUsers = all.get().filter(e -> !syncedUsers.contains(e.getId())).collect(Collectors.toList());
            if (missingUsers.isEmpty()) {
                pos++;
            } else {
                missings += missingUsers.size();
                deactivate(missingUsers);
            }
        }
        logger.info(missings + " users were deactivated");
    }

    private void deactivate(List<UserEntity> missing) {
        if (!missing.isEmpty()) {
            try {
                missing.forEach(ent -> ent.setIsActive(false));
                transaction.execute(status -> userRepository.saveAll(missing));
            } catch (Exception e) {
                logger.error("Failed deactivate users " + missing.stream().map(UserEntity::getId) + " " + e.getLocalizedMessage());
            }
        }
    }

    private List<Integer> syncUsers(List<UserRepresentation> users, List<String> errors) {
        final List<Integer> result = new ArrayList<>();
        for (UserRepresentation user : users) {
            try {
                transaction.execute(status -> {
                    UserEntity admEntity = userRepository.findOneByExtUid(user.getId());

                    if (admEntity == null) {
                        admEntity = userRepository.findOneByUsername(user.getUsername());
                        if (admEntity == null) {
                            admEntity = new UserEntity();
                        }
                        admEntity = userRepository.save(mapUser(user, admEntity));
                    } else if (!isEqual(user, admEntity)) {
                        admEntity = userRepository.save(mapUser(user, admEntity));
                    }

                    result.add(admEntity.getId());
                    return user.getId();
                });
            } catch (Exception e) {
                String message = "Failed synchronize user id:" + user.getId() + " " + e.getLocalizedMessage();
                logger.error(message);
                errors.add(message);
            }
        }
        return result;
    }

    private UserEntity mapUser(UserRepresentation user, UserEntity entity) {
        if (entity == null) {
            entity = new UserEntity();
        }
        entity.setExtUid(user.getId());
        entity.setExtSys(EXT_SYS);
        entity.setIsActive(user.isEnabled());
        entity.setUsername(user.getUsername());
        entity.setName(user.getFirstName());
        entity.setSurname(user.getLastName());
        entity.setEmail(user.getEmail());

        return entity;
    }

    private boolean isEqual(UserRepresentation urep, UserEntity ent2) {
        return Objects.equals(urep.getId(), ent2.getExtUid()) &&
                Objects.equals(urep.isEnabled(), ent2.getIsActive()) &&
                Objects.equals(urep.getUsername(), ent2.getUsername()) &&
                Objects.equals(urep.getFirstName(), ent2.getName()) &&
                Objects.equals(urep.getLastName(), ent2.getSurname()) &&
                Objects.equals(urep.getEmail(), ent2.getEmail()) &&
                Objects.equals(EXT_SYS, ent2.getExtUid());
    }
}
