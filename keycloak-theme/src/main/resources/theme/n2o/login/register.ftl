<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("registerWithTitle",(realm.displayName!''))}
    <#elseif section = "header">
        ${msg("registerWithTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
    <div class="form">
        <form style="width: 500px" action="${url.registrationAction}" method="post">
                    <label for="firstName" class="control-label">${msg("firstName")}</label>
                    <input type="text" id="firstName" class="form-control" name="firstName" value="${(register.formData.firstName!'')}" />

                    <label for="lastName" class="control-label">${msg("lastName")}</label>
                    <input type="text" id="lastName" class="form-control" name="lastName" value="${(register.formData.lastName!'')}" />

                    <label for="email" class="control-label">${msg("email")}</label>
                    <input type="text" id="email" class="form-control" name="email" value="${(register.formData.email!'')}" autocomplete="email" />

          <#if !realm.registrationEmailAsUsername>
                    <label for="username" class="control-label">${msg("username")}</label>
                    <input type="text" id="username" class="form-control" name="username" value="${(register.formData.username!'')}" autocomplete="username" />
          </#if>

            <#if passwordRequired>
                    <label for="password" class="control-label">${msg("password")}</label>
                    <input type="password" id="password" class="form-control" name="password" autocomplete="new-password"/>

                    <label for="password-confirm" class="control-label">${msg("passwordConfirm")}</label>
                    <input type="password" id="password-confirm" class="form-control" name="password-confirm" style="margin-bottom: 15px"/>
             </#if>

            <#if recaptchaRequired??>
            <div>
                <div>
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                </div>
            </div>
            </#if>

            <input class="btn btn-default btn-primary btn-block" type="submit" value="${msg("doRegister")}"/>
            <span><a href="${url.loginUrl}">${msg("backToLogin")?no_esc}</a></span>
        </form>
    </div>
    </#if>
</@layout.registrationLayout>