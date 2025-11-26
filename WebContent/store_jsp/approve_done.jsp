<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Application" %>
<%
    // セッションから申請情報を取得（もし必要なら）
    Application app = (Application) session.getAttribute("pendingApplication");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>店舗登録完了</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            margin-top: 100px;
            background-color: #f8f9fa;
        }
        .container {
            background-color: #fff;
            display: inline-block;
            padding: 30px 50px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #28a745;
        }
        p {
            font-size: 16px;
            color: #333;
        }
        .close-btn {
            margin-top: 20px;
            padding: 10px 20px;
            font-size: 14px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .close-btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>承認完了</h1>
        <p>店舗登録が完了しました。</p>
        <% if(app != null){ %>
            <p>店舗名: <strong><%= app.getStoreName() %></strong></p>
            <p>メール: <strong><%= app.getStoreEmail() %></strong></p>
        <% } %>
        <button class="close-btn" onclick="window.close();">この画面を閉じる</button>
    </div>
</body>
</html>
