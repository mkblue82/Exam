<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>店舗新規登録</title>
<link rel="stylesheet" href="../css/style.css">
</head>
<body>


<h2>店舗新規登録</h2>

<form action="<%= request.getContextPath() %>/RegisterStoreServlet" method="post" enctype="multipart/form-data">
    <table>
        <tr>
            <th>店舗名:</th>
            <td><input type="text" name="storeName" required></td>
        </tr>
        <tr>
            <th>店舗住所:</th>
            <td><input type="text" name="address" required></td>
        </tr>
        <tr>
            <th>電話番号:</th>
            <td><input type="tel" name="phoneNumber" required pattern="[0-9]{2,4}-[0-9]{2,4}-[0-9]{3,4}" title="例: 000-0000-0000"></td>
        </tr>
        <tr>
            <th>メールアドレス (ログインID):</th>
            <td><input type="email" name="email" required></td>
        </tr>
        <tr>
            <th>パスワード:</th>
            <td><input type="password" name="password" required></td>
        </tr>
        <tr>
            <th>営業許可書 (PDF/画像):</th>
            <td><input type="file" name="licenseFile" accept=".pdf,.jpg,.jpeg,.png" required></td>
        </tr>
    </table>
    <input type="submit" value="新規登録">
</form>


</body>
</html>