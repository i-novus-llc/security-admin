package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.loader.builder.UserBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;

/**
 * Тесты лоадера пользователей
 */
@RunWith(SpringRunner.class)
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

    private List<Integer> userIds = new ArrayList<>();

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
        User User1 = UserBuilder.buildUser1();
        User User2 = UserBuilder.buildUser2();
        List<User> data = Arrays.asList(User1, User2);


        loader.accept(data, "ignored");

        UserEntity userEntity1 = userRepository.findOneByUsernameIgnoreCase(User1.getUsername());
        UserEntity userEntity2 = userRepository.findOneByUsernameIgnoreCase(User2.getUsername());
        userIds.add(userEntity1.getId());
        userIds.add(userEntity2.getId());

        userAssertEquals(User1, userEntity1);
        userAssertEquals(User2, userEntity2);
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<User>, String> loader) {
        User User1 = UserBuilder.buildUser1();
        User User2 = UserBuilder.buildUser2Updated();
        List<User> data = Arrays.asList(User1, User2);


        loader.accept(data, "ignored");

        userAssertEquals(User1, userRepository.findOneByUsernameIgnoreCase(User1.getUsername()));
        userAssertEquals(User2, userRepository.findOneByUsernameIgnoreCase(User2.getUsername()));
    }

    /**
     * Вставка двух записей, две есть в БД, одна будет обновлена, одна добавлена
     */
    private void case3(BiConsumer<List<User>, String> loader) {
        User User1 = UserBuilder.buildUser1();
        User User2 = UserBuilder.buildUser2();
        User User3 = UserBuilder.buildUser3();
        List<User> data = Arrays.asList(User2, User3);

        loader.accept(data, "ignored");

        UserEntity userEntity3 = userRepository.findOneByUsernameIgnoreCase(User3.getUsername());
        userIds.add(userEntity3.getId());

        userAssertEquals(User1, userRepository.findOneByUsernameIgnoreCase(User1.getUsername()));
        userAssertEquals(User2, userRepository.findOneByUsernameIgnoreCase(User2.getUsername()));
        userAssertEquals(User3, userEntity3);
    }

    private void userAssertEquals(User expected, UserEntity actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        //        todo SECURITY-396
//        assertEquals(expected.getUserLevel().getName(), actual.getUserLevel().toString());
//        assertEquals(expected.getRoles().stream().map(Role::getCode).collect(Collectors.toList()),
//                actual.getRoleList().stream().map(RoleEntity::getCode).collect(Collectors.toList()));
    }
}
