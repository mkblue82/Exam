<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新規店舗申請</title>
</head>
<body>
    <h2>新規店舗申請</h2>

    <form action="StoreRegisterServlet" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td>店舗名：</td>
                <td><input type="text" name="storeName"></td>
            </tr>
            <tr>
                <td>店舗住所：</td>
                <td><input type="text" name="storeAddress"></td>
            </tr>
            <tr>
                <td>店舗電話番号：</td>
                <td><input type="text" name="storePhone"></td>
            </tr>
            <tr>
                <td>店舗メールアドレス：</td>
                <td><input type="email" name="storeEmail"></td>
            </tr>
            <tr>
                <td>パスワード：</td>
                <td><input type="password" name="password"></td>
            </tr>
            <tr>
                <td>営業許可証（画像またはPDF）：</td>
                <td><input type="file" name="permitFile" accept=".jpg,.png,.pdf"></td>
            </tr>
        </table>

        <br>
        <input type="submit" value="申請">
    </form>

    <br>
    <a href="login.jsp">ログイン画面に戻る</a>
</body>
</html>
