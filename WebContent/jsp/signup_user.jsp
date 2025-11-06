<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新規ユーザー登録</title>
</head>
<body>
    <h2>新規ユーザー登録</h2>

    <form action="StoreRegisterServlet" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td>氏名：</td>
                <td><input type="text" name="name"></td>
            </tr>
            <tr>
                <td>メールアドレス：</td>
                <td><input type="email" name="email"></td>
            </tr>
            <tr>
                <td>パスワード：</td>
                <td><input type="password" name="password"></td>
            </tr>

        </table>

        <br>
        <input type="submit" value="登録">
    </form>

    <br>
    <a href="login.jsp">ログイン画面に戻る</a>
</body>
</html>
