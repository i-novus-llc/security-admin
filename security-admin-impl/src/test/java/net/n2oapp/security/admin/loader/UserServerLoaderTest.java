package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.loader.UserServerLoader;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.loader.builder.UserFormBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Тесты лоадера пользователей
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class UserServerLoaderTest {

    @Autowired
    private UserServerLoader userServerLoader;

    @Autowired
    private RepositoryServerLoader<UserForm, UserEntity, Integer> repositoryServerLoader;

    @Autowired
    private UserRepository repository;

    @LocalServerPort
    private int port;


    private List<Integer> userIds = new ArrayList<>();

    /**
     * Тест {@link UserServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<UserForm>, String> loader = userServerLoader::load;
        repository.deleteByUsername("username1");
        repository.deleteByUsername("username2");
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
    }

    /**
     * Тест {@link RepositoryServerLoader}
     */
    @Test
    public void repositoryLoader() {
        BiConsumer<List<UserForm>, String> loader = repositoryServerLoader::load;
        repository.deleteByUsername("username1");
        repository.deleteByUsername("username2");
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<UserForm>, String> loader) {
        UserForm userForm1 = UserFormBuilder.buildUserForm1(null);
        UserForm userForm2 = UserFormBuilder.buildUserForm2(null);
        List<UserForm> data = Arrays.asList(userForm1, userForm2);

        loader.accept(data, "ignored");

        UserEntity userEntity1 = repository.findOneByUsernameIgnoreCase(userForm1.getUsername());
        UserEntity userEntity2 = repository.findOneByUsernameIgnoreCase(userForm2.getUsername());
        userIds.add(userEntity1.getId());
        userIds.add(userEntity2.getId());

        userAssertEquals(userForm1, userEntity1);
        userAssertEquals(userForm2, userEntity2);
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<UserForm>, String> loader) {
        UserForm userForm1 = UserFormBuilder.buildUserForm1(userIds.get(0));
        UserForm userForm2 = UserFormBuilder.buildUserForm2Updated(userIds.get(1));
        List<UserForm> data = Arrays.asList(userForm1, userForm2);

        loader.accept(data, "ignored");

        userAssertEquals(userForm1, repository.findOneByUsernameIgnoreCase(userForm1.getUsername()));
        userAssertEquals(userForm2, repository.findOneByUsernameIgnoreCase(userForm2.getUsername()));
    }

    /**
     * Вставка двух записей, две есть в БД, одна будет обновлена, одна добавлена
     */
    private void case3(BiConsumer<List<UserForm>, String> loader) {
        UserForm userForm1 = UserFormBuilder.buildUserForm1(userIds.get(0));
        UserForm userForm2 = UserFormBuilder.buildUserForm2(userIds.get(1));
        UserForm userForm3 = UserFormBuilder.buildUserForm3(userIds.get(2));
        List<UserForm> data = Arrays.asList(userForm2, userForm3);

        loader.accept(data, "ignored");

        UserEntity userEntity3 = repository.findOneByUsernameIgnoreCase(userForm3.getUsername());
        userIds.add(userEntity3.getId());

        userAssertEquals(userForm1, repository.findOneByUsernameIgnoreCase(userForm1.getUsername()));
        userAssertEquals(userForm2, repository.findOneByUsernameIgnoreCase(userForm2.getUsername()));
        userAssertEquals(userForm3, userEntity3);
    }

    /**
     * Вставка новой записи с неуникальным username
     * Проверка, что добавления не будет и другие записи не будут повреждены
     */
    private void case4(BiConsumer<List<UserForm>, String> loader) {
        UserForm userForm1 = UserFormBuilder.buildUserForm1(userIds.get(0));
        UserForm userForm2 = UserFormBuilder.buildUserForm2(userIds.get(1));
        UserForm userForm3 = UserFormBuilder.buildUserForm3(userIds.get(2));
        UserForm invalidUserForm = UserFormBuilder.buildUserForm2Updated(999);
        invalidUserForm.setUsername(userForm2.getUsername());

        List<UserForm> data = Arrays.asList(invalidUserForm);

        try {
            loader.accept(data, "ignored");
            fail("Method should throw exception, but he didn't!");
        } catch (Exception ignored) {}

        userAssertEquals(userForm1, repository.findOneByUsernameIgnoreCase(userForm1.getUsername()));
        userAssertEquals(userForm2, repository.findOneByUsernameIgnoreCase(userForm2.getUsername()));
        userAssertEquals(userForm3, repository.findOneByUsernameIgnoreCase(userForm3.getUsername()));
    }


    private void userAssertEquals(UserForm expected, UserEntity actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getUserLevel(), actual.getUserLevel().toString());
        assertEquals(expected.getRoles(), actual.getRoleList().stream().mapToInt(RoleEntity::getId).boxed().collect(Collectors.toList()));
    }
}
