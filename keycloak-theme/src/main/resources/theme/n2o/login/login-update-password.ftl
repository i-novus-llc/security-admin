<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("updatePasswordTitle")}
    <#elseif section = "header">
        ${msg("updatePasswordTitle")}
    <#elseif section = "form">
    <div class="form">
        <form style="width: 500px;" action="${url.loginAction}" method="post">
            <input type="text" id="username" name="username" value="${username}" autocomplete="username" readonly="readonly" style="display:none;"/>
            <input type="password" id="password" name="password" autocomplete="current-password" style="display:none;"/>

            <label for="password-new" class="control-label">${msg("passwordNew")}</label>
            <input type="password" id="password-new" name="password-new" class="form-control" autofocus autocomplete="new-password" />
            <label for="password-confirm" class="control-label">${msg("passwordConfirm")}</label>
            <input type="password" id="password-confirm" name="password-confirm" class="form-control" autocomplete="new-password" />
            <input class="btn btn-default btn-primary btn-block" type="submit" value="${msg("doSubmit")}" style="margin-top:15px"/>
        </form>
    </#if>
</@layout.registrationLayout>
