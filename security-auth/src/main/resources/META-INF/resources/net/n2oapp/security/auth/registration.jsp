<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="favicon.ico">
    <title>Регистрация</title>
    <link href="dist/css/n2o/bootstrap.css" rel="stylesheet">
    <link href="dist/css/n2o/n2o.css" rel="stylesheet">
    <link href="net/n2oapp/security/auth/css/signin.css" rel="stylesheet">
</head>

<body>
<div class="container">
    <form class="form-signin" name='registrationForm' action="registrationServlet" method='POST'>
        <h2 class="form-signin-heading">Регистрация</h2>

        <c:if test="${param.error == 'password'}">
            <output id="errorText" title="errorText" style="color: red">Пароли не совпадают!</output>
        </c:if>
        <c:if test="${param.error == 'loginAlreadyExists'}">
            <output id="errorText" title="errorText" style="color: red">Пользователь с таким логином уже существует!
            </output>
        </c:if>
        <c:if test="${param.error == 'notCorrectLogin'}">
            <output id="errorText" title="errorText" style="color: red">Неверный логин! Он должен содержать только
                латинские
                буквы и цифры.
            </output>
        </c:if>
        <c:if test="${param.error == 'notCorrectEmail'}">
            <output id="errorText" title="errorText" style="color: red">Неверный e-mail!</output>
        </c:if>

        <input id="auth_username" name="username" type="text" value="${username}" class="form-control"
               placeholder="Логин" required="required">
        <input id="auth_email" name="email" type="text" class="form-control" placeholder="E-mail"
               required="required">
        <input id="auth_surname" name="surname" type="text" class="form-control" placeholder="Фамилия"
               required="required">
        <input id="auth_name" name="name" type="text" class="form-control" placeholder="Имя"
               required="required">
        <input id="auth_patromynic" name="patronymic" type="text" class="form-control" placeholder="Отчество">
        <input id="auth_password1" name="password1" type="password" class="form-control" placeholder="пароль"
               required="required">
        <input id="auth_password2" name="password2" type="password" class="form-control"
               placeholder="повторите пароль" required="required">
        <button id="btnAuthUser" class="btn btn-lg btn-primary btn-block" data-loading-text="подождите..."
                type="submit">
            Зарегистрироваться
        </button>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
</body>
</html>