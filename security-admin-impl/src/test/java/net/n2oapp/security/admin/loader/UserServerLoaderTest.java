package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Account;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.AccountEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.loader.builder.UserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Тесты лоадера пользователей
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class UserServerLoaderTest {

    @Autowired
    private ServerLoader<User> serverLoader;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Тест {@link RepositoryServerLoader}
     */
    @Test
    public void repositoryLoader() {
        BiConsumer<List<User>, String> loader = serverLoader::load;
        userRepository.removeByUsernameIn(Arrays.asList("username1", "username2", "username3"));
        case1(loader);
        case2(loader);
        case3(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<User>, String> loader) {
        User user1 = UserBuilder.buildUser1();
        User user2 = UserBuilder.buildUser2();
        List<User> data = Arrays.asList(user1, user2);


        loader.accept(data, "ignored");

        UserEntity userEntity1 = userRepository.findOneByUsernameIgnoreCase(user1.getUsername());
        UserEntity userEntity2 = userRepository.findOneByUsernameIgnoreCase(user2.getUsername());

        userAssertEquals(user1, userEntity1);
        userAssertEquals(user2, userEntity2);
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<User>, String> loader) {
        User user1 = UserBuilder.buildUser1();
        User user2 = UserBuilder.buildUser2Updated();
        List<User> data = Arrays.asList(user1, user2);


        loader.accept(data, "ignored");

        userAssertEquals(user1, userRepository.findOneByUsernameIgnoreCase(user1.getUsername()));
        userAssertEquals(user2, userRepository.findOneByUsernameIgnoreCase(user2.getUsername()));
    }

    /**
     * Вставка двух записей, две есть в БД, одна будет обновлена, одна добавлена
     */
    private void case3(BiConsumer<List<User>, String> loader) {
        User user1 = UserBuilder.buildUser1();
        User user2 = UserBuilder.buildUser2();
        User user3 = UserBuilder.buildUser3();
        List<User> data = Arrays.asList(user2, user3);

        loader.accept(data, "ignored");

        UserEntity userEntity3 = userRepository.findOneByUsernameIgnoreCase(user3.getUsername());

        userAssertEquals(user1, userRepository.findOneByUsernameIgnoreCase(user1.getUsername()));
        userAssertEquals(user2, userRepository.findOneByUsernameIgnoreCase(user2.getUsername()));
        userAssertEquals(user3, userEntity3);
    }

    private void userAssertEquals(User expected, UserEntity actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        if (expected.getAccounts() != null) {
            assertEquals(expected.getAccounts().size(), actual.getAccounts().size());
            for (int i = 0; i < expected.getAccounts().size(); i++)
                accountAssertEquals(expected.getAccounts().get(i), actual.getAccounts().get(i));
        }
    }

    private void accountAssertEquals(Account expected, AccountEntity actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUserLevel(), actual.getUserLevel());
        assertEquals(expected.getRoles().stream().map(Role::getCode).collect(Collectors.toList()),
                actual.getRoleList().stream().map(RoleEntity::getCode).collect(Collectors.toList()));
    }
}
