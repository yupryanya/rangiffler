<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script src="https://yastatic.net/jquery/2.2.3/jquery.min.js" crossorigin="anonymous"></script>

    <link href="https://yastatic.net/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <script src="https://yastatic.net/bootstrap/3.3.6/js/bootstrap.min.js" crossorigin="anonymous"></script>

    <link href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet" type="text/css"/>
    <script src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/xml.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/bash.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>

    <style>
        body {
            padding: 15px;
        }
        pre {
            white-space: pre-wrap;
            word-break: break-word;
        }
        .panel-heading {
            font-weight: bold;
            font-size: 16px;
        }
        .header-key {
            color: #337ab7;
        }
        .cookie-key {
            color: #5cb85c;
        }
        code {
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="panel panel-info">
    <div class="panel-heading">Request</div>
    <div class="panel-body">
        <code><strong><#if data.method??>${data.method}<#else>GET</#if></strong>:
        <#if data.url??>${data.url}<#else>Unknown URL</#if></code>
    </div>
</div>

<#if (data.headers)?has_content>
    <div class="panel panel-primary">
        <div class="panel-heading">Headers</div>
        <div class="panel-body">
            <#list data.headers as name, value>
                <div><code><span class="header-key">${name}</span>: ${value}</code></div>
            </#list>
        </div>
    </div>
</#if>

<#if data.body??>
    <div class="panel panel-success">
        <div class="panel-heading">Body</div>
        <div class="panel-body">
            <pre><code class="json">${data.body}</code></pre>
        </div>
    </div>
</#if>

<#if (data.cookies)?has_content>
    <div class="panel panel-warning">
        <div class="panel-heading">Cookies</div>
        <div class="panel-body">
            <#list data.cookies as name, value>
                <div><code><span class="cookie-key">${name}</span>: ${value}</code></div>
            </#list>
        </div>
    </div>
</#if>

</body>
</html>
