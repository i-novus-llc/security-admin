<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
    ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    ${msg("loginTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
        <#if realm.password>
        <div class="form">
            <form style="width: 500px;" onsubmit="login.disabled = true; return true;" action="${url.loginAction}"
                  method="post">
                <label for="username" class="control-label">
                    <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>
                </label>
                <#if usernameEditDisabled??>
                    <input tabindex="1" id="username" class="form-control" name="username"
                           value="${(login.username!'')}" type="text" disabled/>
                <#else>
                    <input tabindex="1" id="username" class="form-control" name="username"
                           value="${(login.username!'')}" type="text" autofocus autocomplete="off"/>
                </#if>
                <label for="password" class="control-label">${msg("password")}</label>
                <input tabindex="2" id="password" class="form-control" style="margin-bottom: 15px" name="password" type="password"
                       autocomplete="off"/>
                <#if realm.rememberMe && !usernameEditDisabled??>
                    <div class="checkbox">
                        <label>
                            <#if login.rememberMe??>
                                <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" tabindex="3"
                                       checked> ${msg("rememberMe")}
                            <#else>
                                <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"
                                       tabindex="3"> ${msg("rememberMe")}
                            </#if>
                        </label>
                    </div>
                </#if>
                <input tabindex="4" class="btn btn-default btn-primary btn-block" name="login" id="kc-login"
                       type="submit" value="${msg("doLogIn")}" style="margin-bottom: 15px"/>
            </form>
            <div>
                <#if realm.resetPasswordAllowed>
                    <div><span><a tabindex="5"
                                  href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></span></div>
                </#if>
            </div>
        </div>
        </#if>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
        <div>
            <span>${msg("noAccount")} <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
        </div>
        </#if>
    </#if>
</@layout.registrationLayout>
