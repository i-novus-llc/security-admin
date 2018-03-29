<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("emailForgotTitle")}
    <#elseif section = "header">
        ${msg("emailForgotTitle")}
    <#elseif section = "form">
    <div class="form">
        <form id="kc-reset-password-form" style="width: 500px;" action="${url.loginAction}" method="post">
            <label for="username" class="control-label"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>
            <input type="text" id="username" name="username" class="form-control" style="margin-bottom: 15px" autofocus/>
            <input class="btn btn-default btn-primary btn-block" style="margin-bottom: 15px" type="submit" value="${msg("doSubmit")}"/>
            <span><a href="${url.loginUrl}">${msg("backToLogin")?no_esc}</a></span>
        </form>
    </div>
    <#elseif section = "info">
        <div style="width: 500px;">
            ${msg("emailInstruction")}
        </div>
    </#if>
</@layout.registrationLayout>
