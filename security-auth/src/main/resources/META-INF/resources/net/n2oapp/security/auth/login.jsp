<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="n2o" uri="http://n2oapp.net/framework/tld" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="favicon.ico">
    <title>Авторизация</title>
    <link href="dist/css/n2o/bootstrap.css" rel="stylesheet">
    <link href="dist/css/n2o/n2o.css" rel="stylesheet">
    <link href="dist/signin.css" rel="stylesheet">
</head>

<body>

<div class="container">
    <form class="form-signin" action="login" method='POST'>
        <h2 class="form-signin-heading">Авторизация</h2>
        <c:if test="${param.error == 'true'}">
            <output id="errorText" title="errorText" style="color: red">Введены неправильные данные!</output>
        </c:if>
        <label for="auth_username" class="sr-only">Логин</label>
        <input type="text" id="auth_username" name="username" class="form-control" placeholder="Логин" required autofocus>
        <label for="auth_password" class="sr-only">Пароль</label>
        <input type="password" id="auth_password" name="password" class="form-control" placeholder="Пароль" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
        <br/>
        <c:if test='${n2o:property("n2o.auth.registration.enabled")}'>
        <div>или <a href="/registration">Зарегистрироваться</a></div>
    </c:if>
    </form>

</div>
</body>
</html>