<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新規ユーザー登録</title>
<link rel="stylesheet" href="../css/style.css">
</head>
<body>

<h2>新規ユーザー登録</h2>

<form action="<%= request.getContextPath() %>/RegisterUserServlet" method="post">
    <table>
        <tr>
            <th>氏名：</th>
            <td><input type="text" name="name" required></td>
        </tr>
        <tr>
            <th>メールアドレス：</th>
            <td><input type="email" name="email" required></td>
        </tr>
        <tr>
            <th>パスワード：</th>
            <td><input type="password" name="password" required></td>
        </tr>
    </table>
    <input type="submit" value="新規登録">
</form>


</body>
</html>
