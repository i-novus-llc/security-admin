package net.n2oapp.security.auth;

import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static net.n2oapp.properties.StaticProperties.getProperty;

/**
 * Сервлет для создания пользователя при регистрации
 */
public class RegistrationServlet extends HttpServlet {

    private UserDetailsManager userDetailsManager = StaticSpringContext.getBean(UserDetailsManager.class);


    /**
     * Проверка существования в бд пользователя с введенным username,
     * редирект c заданым параметром error в случае ошибки
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Objects.equals(checkErrorCode(request.getParameter("username"), "", "", userDetailsManager), "loginAlreadyExists"))
            response.sendRedirect("registration?error=loginAlreadyExists");
    }

    /**
     * Создание пользователя и редирект при ошибке (с заполненным параметром error и без нее
     */
    /**
     * Получение кода ошибки, и, если он имеется,
     * редирект с заполненным параметром error,
     * создание пользователя и редирект на страницу входа
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password1");
        String repeatedPassword = request.getParameter("password2");
        String surname = request.getParameter("surname");
        String name = request.getParameter("name");
        String patronymic= request.getParameter("patronymic");
        String errorCode = checkErrorCode(username, password, repeatedPassword, userDetailsManager);
        if(errorCode != null) {
            response.sendRedirect("registration?error=" + errorCode);
            return;
        }
        createUser(username, password, surname, name, patronymic, email, userDetailsManager);
        response.sendRedirect("login");
    }

    /**
     * Проверка существования пользователя с введенным username в бд,
     * эквивалентности значений полей password1 и password2,
     * и тогоб что поле username заполнено
     * @param username данные из поля username
     * @param password данные из поля password1
     * @param repeatedPassword данные из поля password2
     * @param userDetailsManager менеджер для работы с бд
     * @return код ошибки или null
     * @throws ServletException
     * @throws IOException
     */
    private String checkErrorCode(String username, String password, String repeatedPassword, UserDetailsManager userDetailsManager) throws ServletException, IOException {
        if (userDetailsManager.userExists(username))
            return "loginAlreadyExists";
        if (Objects.equals(username, ""))
            return "emptyLogin";
        if (!Objects.equals(password, repeatedPassword))
            return "password";

        return null;
    }

    /**
     * Логика создания пользователя
     * @param username данные из поля username
     * @param password данные из поля password1
     * @param userDetailsManager менеджер для работы с бд
     */
    private void createUser(String username, String password, String surname, String name, String patronymic,
                            String email, UserDetailsManager userDetailsManager) {
        User user = new User(username, password,
                new ArrayList<GrantedAuthority>(Collections.singletonList(new RoleGrantedAuthority(getProperty("n2o.auth.authority")))),
                surname, name, patronymic, email);
        userDetailsManager.createUser(user);
    }
}

