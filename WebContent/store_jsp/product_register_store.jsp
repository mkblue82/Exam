<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>商品登録</title>

<!-- CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick-theme.css"/>

<style>
    body {
        font-family: "游ゴシック", "Yu Gothic", sans-serif;
        background-color: #f9f9f9;
    }
    .container {
        max-width: 600px;
        margin: 50px auto;
        background: #fff;
        padding: 40px;
        border-radius: 10px;
        box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }
    h2 {
        text-align: center;
        color: #a55d00;
        margin-bottom: 30px;
    }
    form label {
        font-weight: bold;
        display: block;
        margin-top: 20px;
    }
    form input, form textarea, form select {
        width: 100%;
        padding: 10px;
        margin-top: 5px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }
    form button {
        margin-top: 30px;
        width: 100%;
        padding: 12px;
        background-color: #a55d00;
        color: white;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
    }
    form button:hover {
        background-color: #c06a00;
    }

    /* プレビュー画像 */
    #imagePreview {
        display: none;
        margin-top: 15px;
        text-align: center;
    }
    #imagePreview img {
        max-width: 100%;
        max-height: 200px;
        border-radius: 5px;
        box-shadow: 0 0 5px rgba(0,0,0,0.2);
    }

    /* エラーメッセージと成功メッセージを非表示 */
    .error-message,
    .success-message {
        display: none;
    }
</style>
</head>
<body>


<div class="container">
    <h2>商品登録</h2>

    <!-- メッセージ部分（非表示） -->
    <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="success-message">${successMessage}</div>
    </c:if>

    <form id="productRegisterForm" action="${pageContext.request.contextPath}/ProductRegisterAction" method="post" enctype="multipart/form-data">
        <label for="productName">商品名</label>
        <input type="text" id="productName" name="productName" required>

        <label for="quantity">個数</label>
        <input type="number" id="quantity" name="quantity" min="1" required>

        <label for="expirationDate">消費期限</label>
        <input type="date" id="expirationDate" name="expirationDate" required>

        <label for="tags">タグ</label>
        <input type="text" id="tags" name="tags" placeholder="例：食品ロス, 野菜, 無料">

        <label for="productImage">商品画像</label>
        <input type="file" id="productImage" name="productImage" accept="image/*" onchange="previewImage(this)">

        <!-- 画像プレビュー -->
        <div id="imagePreview">
            <img id="preview" src="#" alt="プレビュー画像">
        </div>

        <button type="submit">登録する</button>
    </form>
</div>


<script>
    // 画像プレビュー機能
    function previewImage(input) {
        const preview = document.getElementById('preview');
        const previewContainer = document.getElementById('imagePreview');

        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                previewContainer.style.display = 'block';
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            previewContainer.style.display = 'none';
        }
    }

    // フォームバリデーション
    document.getElementById('productRegisterForm').addEventListener('submit', function(e) {
        const expirationDate = new Date(document.getElementById('expirationDate').value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (expirationDate < today) {
            e.preventDefault();
            alert('消費期限は今日以降の日付を指定してください。');
            return false;
        }

        const fileInput = document.getElementById('productImage');
        if (fileInput.files.length > 0) {
            const fileSize = fileInput.files[0].size;
            const maxSize = 5 * 1024 * 1024; // 5MB

            if (fileSize > maxSize) {
                e.preventDefault();
                alert('画像ファイルのサイズは5MB以下にしてください。');
                return false;
            }
        }
    });
</script>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>

</body>
</html>
