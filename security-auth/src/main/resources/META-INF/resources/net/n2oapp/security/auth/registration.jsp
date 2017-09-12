<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
    <link rel="stylesheet" href="lib/bootstrap/css/bootstrap.min.css"/>
</head>
<body>
<div id="menu_auth_modal" class="modal" tabindex="-1" role="dialog" aria-hidden="false" style="display: block; max-width: 360px">
    <div class="modal-header">
        <h3 data-title-editable="1">Регистрация</h3>
    </div>
    <div class="modal-body">
        <c:if test="${param.error == 'password'}">
            <output id="errorText" title="errorText" style="color: red">Пароли не совпадают!</output>
        </c:if>
        <c:if test="${param.error == 'loginAlreadyExists'}">
            <output id="errorText" title="errorText" style="color: red">Пользователь с таким логином уже существует!</output>
        </c:if>
        <c:if test="${param.error == 'emptyLogin'}">
            <output id="errorText" title="errorText" style="color: red">Пожалуйста, введите логин!</output>
        </c:if>
        <form class="form-signin n2o-auth-form" name='registrationForm' action="registrationServlet" method='POST'>
            <div class="control-group">
                <div class="controls">
                    <input id="auth_username" name="username" type="text" class="input-block-level" placeholder="Логин" data-title-editable="1" required="required">
                </div>
                <div class="controls">
                    <input id="auth_email" name="email" type="text" class="input-block-level" placeholder="E-mail" data-title-editable="1" required="required">
                </div>
                <div class="controls">
                    <input id="auth_surname" name="surname" type="text" class="input-block-level" placeholder="Фамилия" data-title-editable="1" required="required">
                </div>
                <div class="controls">
                    <input id="auth_name" name="name" type="text" class="input-block-level" placeholder="Имя" data-title-editable="1" required="required">
                </div>
                <div class="controls">
                    <input id="auth_patromynic" name="patronymic" type="text" class="input-block-level" placeholder="Отчество" data-title-editable="1">
                </div>
                <div class="controls">
                    <input id="auth_password1" name="password1" type="password" class="input-block-level" placeholder="пароль" data-title-editable="1" required="required">
                </div>
                <div class="controls">
                    <input id="auth_password2" name="password2" type="password" class="input-block-level" placeholder="повторите пароль" data-title-editable="1" required="required">
                </div>
                <button id="btnAuthUser" class="btn btn-large btn-primary btn-block" data-loading-text="подождите..." data-title-editable="1" type="submit">
                    Зарегистрироваться
                </button>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
    </div>
</div>
</body>
</html>