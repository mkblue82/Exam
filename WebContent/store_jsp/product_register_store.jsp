<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品登録 - Sample Online Mall</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* リセット */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            padding: 20px;
        }

        .register-wrapper {
            width: 100%;
            max-width: 500px;
            margin: 3rem auto;
        }

        .register-container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2.5rem;
        }

        .register-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .register-header h1 {
            color: #c77c4a;
            font-size: 2rem;
            font-weight: 700;
            margin: 0 0 1rem 0;
        }

        .register-header::after {
            content: '';
            display: block;
            width: 70%;
            height: 3px;
            background: #c77c4a;
            margin: 0 auto;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-size: 0.9rem;
            word-wrap: break-word;
        }

        .success-message {
            background: #e8f5e9;
            color: #2e7d32;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #2e7d32;
            font-size: 0.9rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 1rem;
        }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
            background: #fff;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c77c4a;
        }

        .form-group input[type="file"] {
            width: 100%;
            padding: 0.5rem;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 0.95rem;
            background: #fff;
            cursor: pointer;
        }

        .form-group input[type="file"]::-webkit-file-upload-button {
            padding: 0.5rem 1rem;
            border: none;
            background: #c77c4a;
            color: #fff;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 1rem;
            font-weight: 600;
        }

        .form-group input[type="file"]::-webkit-file-upload-button:hover {
            background: #b56c3a;
        }

        .image-preview {
            margin-top: 1rem;
            text-align: center;
        }

        .image-preview img {
            max-width: 100%;
            max-height: 200px;
            border-radius: 4px;
            border: 2px solid #ddd;
        }

        .btn-submit,
        .btn-cancel {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .btn-submit {
            background: #c77c4a;
            color: #fff;
        }

        .btn-submit:hover {
            background: #b56c3a;
        }

        .btn-cancel {
            background: #fff;
            color: #c77c4a;
            border: 2px solid #c77c4a;
        }

        .btn-cancel:hover {
            background: #c77c4a;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .register-container {
                padding: 2rem 1.5rem;
            }

            .register-header h1 {
                font-size: 1.5rem;
            }
        }
            .error-message,
    .success-message {
        display: none;
    }
    </style>
</head>
<body>
    <div class="register-wrapper">
        <div class="register-container">
            <div class="register-header">
                <h1>商品登録</h1>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="error-message" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="success-message" role="alert">
                    ${successMessage}
                </div>
            </c:if>



            <form action="${pageContext.request.contextPath}/product_register"
                  method="post"
                  enctype="multipart/form-data"
                  id="productRegisterForm">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="form-group">
                    <label for="productName">商品名</label>
                    <input type="text"
                           id="productName"
                           name="productName"
                           required
                           maxlength="100"
                           value="${param.productName}">
                </div>

                <div class="form-group">
                    <label for="quantity">個数</label>
                    <input type="number"
                           id="quantity"
                           name="quantity"
                           required
                           min="1"
                           max="9999"
                           value="${param.quantity}">
                </div>

                <div class="form-group">
                    <label for="expirationDate">消費期限</label>
                    <input type="date"
                           id="expirationDate"
                           name="expirationDate"
                           required
                           value="${param.expirationDate}">
                </div>

                <div class="form-group">
                    <label for="tags">タグ</label>
                    <input type="text"
                           id="tags"
                           name="tags"
                           maxlength="200"
                           value="${param.tags}">
                </div>

                <div class="form-group">
                    <label for="productImage">画像</label>
                    <input type="file"
                           id="productImage"
                           name="productImage"
                           accept="image/*"
                           required
                           onchange="previewImage(this)">
                    <div id="imagePreview" class="image-preview" style="display:none;">
                        <img id="preview" src="" alt="画像プレビュー">
                    </div>
                </div>

                <button type="submit" class="btn-submit">登録する</button>
                <button type="button" class="btn-cancel"
                        onclick="location.href='${pageContext.request.contextPath}/jsp/store_menu.jsp'">
                    キャンセル
                </button>
            </form>
        </div>
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
