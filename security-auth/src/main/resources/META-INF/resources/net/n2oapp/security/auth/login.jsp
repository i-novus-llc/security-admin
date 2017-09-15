<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n2o" uri="http://n2oapp.net/framework/tld" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>Авторизация</title>
    <link rel="stylesheet" href="dist/css/n2o/bootstrap.css"/>
</head>
<body>
<div id="menu_auth_modal" class="modal" tabindex="-1" role="dialog" aria-hidden="false" style="display: block; max-width: 360px">
    <div class="modal-header">
        <h3 data-title-editable="1">Авторизация</h3>
    </div>
    <div class="modal-body">
        <c:if test="${param.error == 'true'}">
            <output id="errorText" title="errorText" style="color: red">Введены неправильные данные!</output>
        </c:if>
        <form class="form-signin n2o-auth-form" name='loginForm' action="login" method='POST'>
            <div class="control-group">
                <div class="controls">
                    <input id="auth_username" name="username" type="text" class="input-block-level" placeholder="логин" data-title-editable="1" autofocus>
                </div>
                <div class="controls">
                    <input id="auth_password" name="password" type="password" class="input-block-level" placeholder="пароль" data-title-editable="1">
                </div>
                <div>
                    <button id="btnAuthUser" class="btn btn-large btn-primary btn-block" data-loading-text="подождите..." data-title-editable="1" type="submit">
                        Войти
                    </button>
                </div>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        </form>
       <c:if test='${n2o:property("n2o.auth.registration.enabled")}'>
           <div class="">
                или <a href="/registration" >Зарегистрироваться</a>
           </div>
       </c:if>
        </div>
    </div>
</div>
</body>
</html>