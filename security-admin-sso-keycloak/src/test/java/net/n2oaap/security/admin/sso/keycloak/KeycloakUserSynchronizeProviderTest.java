package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.synchronization.KeycloakUserSynchronizeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {TestApplication.class, KeycloakUserSynchronizeProvider.class},
        properties = {"spring.liquibase.change-log=classpath:changelog.xml",
                "audit.service.url=Mocked", "audit.client.enabled=false"})
@EnableJpaRepositories(basePackages = "net.n2oapp.security.admin.impl")
@EntityScan("net.n2oapp.security.admin.impl")
public class KeycloakUserSynchronizeProviderTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KeycloakRestUserService restUserService;

    @Autowired
    private KeycloakUserSynchronizeProvider synchronizeProvider;


    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testKeycloakEmpty() {
        userRepository.deleteAll();
        List<UserEntity> before = userRepository.saveAll(getRefeUsers());

        reset(restUserService);
        when(restUserService.getUsersCount()).thenReturn(0);

        synchronizeProvider.startSynchronization();

        List<UserEntity> after = userRepository.findAll();

        assertIsEqual(before, after);
    }

    @Test
    public void testCreateOne() {
        userRepository.deleteAll();
        List<UserEntity> before = userRepository.saveAll(getRefeUsers());

        List<UserRepresentation> newUsers = new ArrayList<>();
        newUsers.add(getUserRep("User_5"));
        newUsers.add(getUserRep("User_4"));
        reset(restUserService);
        when(restUserService.getUsersCount()).thenReturn(2);
        when(restUserService.searchUsers(anyString(), anyInt(), anyInt())).thenReturn(newUsers);

        synchronizeProvider.startSynchronization();

        List<UserEntity> after = userRepository.findAll();

        assertEquals(before.size(), after.size() - 1);

        UserEntity user5 = getByUserName(after, "user_5_name");
        //        todo SECURITY-396
//        assertEquals(user5.getExtUid(), "User_5_id");
//        assertEquals(user5.getExtSys(), "KEYCLOAK");

        before.add(user5);

        assertIsEqual(before, after);
    }

    @Test
    public void testCreateTwoDeactivateOne() {
        userRepository.deleteAll();
        List<UserEntity> before = userRepository.saveAll(getRefeUsers());

        List<UserRepresentation> newUsers = new ArrayList<>();
        newUsers.add(getUserRep("User_5"));
        newUsers.add(getUserRep("User_6"));
        reset(restUserService);
        when(restUserService.getUsersCount()).thenReturn(2);
        when(restUserService.searchUsers(anyString(), anyInt(), anyInt())).thenReturn(newUsers);

        synchronizeProvider.startSynchronization();

        List<UserEntity> after = userRepository.findAll();

        assertEquals(before.size(), after.size() - 2);

        UserEntity user4 = getByUserName(after, "user_4_name");
        //        todo SECURITY-396
//        assertEquals(user4.getExtUid(), "User_4_id");
//        assertEquals(user4.getExtSys(), "KEYCLOAK");
        assertEquals(user4.getIsActive(), Boolean.FALSE);
        user4.setIsActive(null);

        UserEntity user5 = getByUserName(after, "user_5_name");
        //        todo SECURITY-396
//        assertEquals(user5.getExtUid(), "User_5_id");
//        assertEquals(user5.getExtSys(), "KEYCLOAK");

        UserEntity user6 = getByUserName(after, "user_6_name");
        //        todo SECURITY-396
//        assertEquals(user6.getExtUid(), "User_6_id");
//        assertEquals(user6.getExtSys(), "KEYCLOAK");

        before.add(user5);
        before.add(user6);

        assertIsEqual(before, after);
    }

    @Test
    public void testCreateOneUpdateOne() {
        userRepository.deleteAll();
        List<UserEntity> before = userRepository.saveAll(getRefeUsers());

        UserRepresentation user4rep = getUserRep("User_4");
        user4rep.setUsername("User_4_new_name");

        List<UserRepresentation> newUsers = new ArrayList<>();
        newUsers.add(getUserRep("User_5"));
        newUsers.add(user4rep);
        reset(restUserService);
        when(restUserService.getUsersCount()).thenReturn(2);
        when(restUserService.searchUsers(anyString(), anyInt(), anyInt())).thenReturn(newUsers);

        synchronizeProvider.startSynchronization();

        List<UserEntity> after = userRepository.findAll();

        assertEquals(before.size(), after.size() - 1);

        UserEntity user4 = getByUserName(after, "User_4_new_name");
        //        todo SECURITY-396
//        assertEquals(user4.getExtUid(), "User_4_id");
//        assertEquals(user4.getExtSys(), "KEYCLOAK");

        UserEntity user5 = getByUserName(after, "user_5_name");
        //        todo SECURITY-396
//        assertEquals(user5.getExtUid(), "User_5_id");
//        assertEquals(user5.getExtSys(), "KEYCLOAK");

        getByUserName(before, "user_4_name").setUsername("User_4_new_name");
        before.add(user5);

        assertIsEqual(before, after);
    }

    private UserRepresentation getUserRep(String name) {
        UserRepresentation user = new UserRepresentation();
        user.setId(name + "_id");
        user.setUsername(name + "_name");
        return user;
    }

    /**
     * Изначальные пользователи в БД
     */
    private List<UserEntity> getRefeUsers() {
        List<UserEntity> result = new ArrayList<>();
        UserEntity entity = new UserEntity();
        entity.setUsername("user_1");
        result.add(entity);

        entity = new UserEntity();
        entity.setUsername("user_2");
        //        todo SECURITY-396
//        entity.setExtUid("user_2");
        result.add(entity);

        entity = new UserEntity();
        entity.setUsername("user_3");
        //        todo SECURITY-396
//        entity.setExtUid("user_3");
//        entity.setExtSys("ESIA");
        result.add(entity);

        entity = new UserEntity();
        entity.setUsername("User_4_name");
        //        todo SECURITY-396
//        entity.setExtUid("User_4_id");
//        entity.setExtSys("KEYCLOAK");
        result.add(entity);

        return result;
    }

    private void assertIsEqual(List<UserEntity> entities1, List<UserEntity> entities2) {
        assertEquals(entities1.size(), entities2.size());
        for(UserEntity entity1 : entities1) {
            UserEntity entity2 = getById(entities2, entity1.getId());
            assertIsEquals(entity1, entity2);
        }
    }

    private void assertIsEquals(UserEntity entity1, UserEntity entity2) {
        assertEquals(entity1.getUsername().toLowerCase(), entity2.getUsername().toLowerCase());
        assertEquals(entity1.getIsActive(), entity2.getIsActive());
        //        todo SECURITY-396
//        assertEquals(entity1.getExtSys(), entity2.getExtSys());
//        assertEquals(entity1.getExtUid(), entity2.getExtUid());
    }

    private UserEntity getById(List<UserEntity> entities2, Integer id) {
        return entities2.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    private UserEntity getByUserName(List<UserEntity> entities2, String name) {
        return entities2.stream().filter(e -> e.getUsername().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
