<#-- @ftlvariable name="data" type="guru.qa.rangiffler.data.logging.SqlAttachmentData" -->
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!-- Highlight.js -->
    <link rel="stylesheet" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" type="text/css"/>
    <script src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script src="https://yandex.st/highlightjs/8.0/languages/sql.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>

    <!-- Bootstrap (optional but for consistent formatting with other templates) -->
    <link rel="stylesheet" href="https://yastatic.net/bootstrap/3.3.6/css/bootstrap.min.css" crossorigin="anonymous">
    <script src="https://yastatic.net/bootstrap/3.3.6/js/bootstrap.min.js" crossorigin="anonymous"></script>

    <style>
        body {
            padding: 15px;
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        }
        .panel {
            margin-top: 20px;
        }
        .panel-heading {
            font-weight: bold;
            font-size: 16px;
        }
        pre {
            white-space: pre-wrap;
            word-break: break-word;
        }
        code {
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="panel panel-default">
    <div class="panel-heading">SQL Query</div>
    <div class="panel-body">
        <pre><code class="sql">${data.sql}</code></pre>
    </div>
</div>

</body>
</html>
