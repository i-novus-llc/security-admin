<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title><#nested "title"></title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body>
    <div>
        <div class="container">
            <h2 class="form-signin-heading"><#nested "header"></h2>
            <div>
                <div>
                    <#if displayMessage && message?has_content>
                        <div class="container">
                                <#if message.type = 'success'><div class="alert alert-success"></#if>
                                <#if message.type = 'warning'><div class="alert alert-warning"></#if>
                                <#if message.type = 'error'><div class="alert alert-error"></#if>
                                <#if message.type = 'info'><div class="alert alert-info"></#if>
                                <span class="text">${message.summary?no_esc}</span>
                            </div>
                        </div>
                    </#if>

                    <div>
                        <#nested "form">
                    </div>

                    <#if displayInfo>
                        <div>
                            <#nested "info">
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
</#macro>
