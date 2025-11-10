<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>商品登録</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }

        .register-wrapper {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .register-container {
            background-color: #fff;
            border-radius: 16px;
            padding: 40px 60px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            width: 480px;
        }

        .register-header {
            text-align: center;
            margin-bottom: 24px;
        }

        .register-header h1 {
            color: #a3541c;
            border-bottom: 3px solid #a3541c;
            display: inline-block;
            padding-bottom: 4px;
        }

        .error-message {
            background-color: #ffe5e5;
            color: #d40000;
            border: 1px solid #d40000;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 8px;
        }

        .success-message {
            background-color: #e7ffe5;
            color: #008f00;
            border: 1px solid #008f00;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 8px;
        }

        form label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
        }

        form input[type="text"],
        form input[type="number"],
        form input[type="date"],
        form input[type="file"],
        form textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 8px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        form button {
            width: 100%;
            background-color: #a3541c;
            color: #fff;
            border: none;
            padding: 12px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
        }

        form button:hover {
            background-color: #87400f;
        }

        /* 画像プレビュー */
        #imagePreview {
            display: none;
            margin-bottom: 20px;
            text-align: center;
        }

        #preview {
            max-width: 100%;
            max-height: 200px;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    <div class="register-wrapper">
        <div class="register-container">
            <div class="register-header">
                <h1>商品登録</h1>
            </div>

            <!-- エラーメッセージ -->
            <c:if test="${not empty errorMessage}">
                <div class="error-message" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <!-- 成功メッセージ -->
            <c:if test="${not empty successMessage}">
                <div class="success-message" role="alert">
                    ${successMessage}
                </div>
            </c:if>

            <!-- 登録フォーム -->
            <form action="${pageContext.request.contextPath}/product_register"
                  method="post" enctype="multipart/form-data" id="productRegisterForm">

                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <label for="productName">商品名</label>
                <input type="text" id="productName" name="productName" required>

                <label for="quantity">個数</label>
                <input type="number" id="quantity" name="quantity" min="1" required>

                <label for="expirationDate">消費期限</label>
                <input type="date" id="expirationDate" name="expirationDate" required>

                <label for="tags">タグ</label>
                <input type="text" id="tags" name="tags" placeholder="例: 野菜, 無添加">

                <label for="productImage">商品画像</label>
                <input type="file" id="productImage" name="image" accept="image/*" onchange="previewImage(this)">

                <div id="imagePreview">
                    <img id="preview" src="#" alt="プレビュー画像">
                </div>

                <button type="submit">登録する</button>
            </form>
        </div>
    </div>

    <!-- JavaScript -->
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

    <!-- 外部JSライブラリ -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
    <script src="../js/slick.js"></script>
    <script src="../js/main.js"></script>
    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>
