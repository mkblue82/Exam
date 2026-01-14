<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Store" %>
<%
    Store store = (Store) session.getAttribute("store");
    boolean hasDiscount = false;
    String currentTime = "";
    int currentRate = 0;

    if (store != null && store.getDiscountTime() != null && store.getDiscountRate() > 0) {
        hasDiscount = true;
        currentTime = store.getDiscountTime().toString().substring(0, 5); // HH:mm形式
        currentRate = store.getDiscountRate();
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>割引設定</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .discount-setting-container {
            max-width: 500px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }
        .discount-setting-container h1 {
            color: #c07148;
            margin-bottom: 1.5rem;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            text-align: center;
        }

        /* 現在の設定表示 */
        .current-setting {
            background: #f0f8ff;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
            border: 2px solid #4a90e2;
        }
        .current-setting h3 {
            color: #4a90e2;
            margin-bottom: 10px;
            font-size: 1.1rem;
        }
        .current-setting-content {
            font-size: 1.1rem;
            color: #333;
        }
        .no-discount {
            background: #f5f5f5;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
            color: #666;
        }

        .form-group {
            margin-bottom: 1.5rem;
            text-align: left;
        }
        .form-row {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
            margin-bottom: 10px;
        }
        .form-row label {
            color: #555;
            font-size: 1rem;
            font-weight: 500;
            min-width: 120px;
            text-align: right;
        }
        .input-wrapper {
            position: relative;
            width: 180px;
        }
        .time-input-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .time-input-wrapper {
            position: relative;
            width: 80px;
        }
        .time-separator {
            font-size: 1.2rem;
            font-weight: bold;
            color: #666;
        }
        .form-row input[type="number"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1rem;
            transition: 0.3s;
        }
        .form-row input[type="number"]:focus {
            outline: none;
            border-color: #c07148;
        }
        .unit {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #666;
            font-size: 0.9rem;
            pointer-events: none;
        }
        .error-message {
            color: #e74c3c;
            font-size: 0.85rem;
            margin-top: 5px;
            text-align: center;
            display: none;
        }
        .error-message.show {
            display: block;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
            display: none;
        }
        .success-message.show {
            display: block;
        }
        .btn-submit {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            font-family: inherit;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            margin-top: 1rem;
        }
        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        /* 割引取り消しボタン */
        .btn-cancel-discount {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #dc3545;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #fff;
            color: #dc3545;
            margin-top: 0.8rem;
            font-family: inherit;
        }
        .btn-cancel-discount:hover {
            background: #dc3545;
            color: #fff;
        }

        .btn-back-list {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #c07148;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #fff;
            color: #c07148;
            margin-top: 0.8rem;
            font-family: inherit;
        }
        .btn-back-list:hover {
            background: #c07148;
            color: #fff;
        }

        /* 確認ダイアログ */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: #fff;
            margin: 15% auto;
            padding: 30px;
            border-radius: 10px;
            width: 400px;
            max-width: 90%;
            text-align: center;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
        }
        .modal-content h3 {
            color: #dc3545;
            margin-bottom: 20px;
        }
        .modal-buttons {
            display: flex;
            gap: 15px;
            margin-top: 25px;
        }
        .modal-btn {
            flex: 1;
            padding: 10px;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            transition: 0.3s;
        }
        .modal-btn-confirm {
            background: #dc3545;
            color: white;
        }
        .modal-btn-confirm:hover {
            background: #c82333;
        }
        .modal-btn-cancel {
            background: #6c757d;
            color: white;
        }
        .modal-btn-cancel:hover {
            background: #5a6268;
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_store.jsp" />

    <!-- メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="discount-setting-container">
                <h1>割引設定</h1>

                <!-- 成功メッセージ -->
                <div id="successMessage" class="success-message">
                    割引設定が完了しました。
                </div>

                <!-- 現在の設定表示 -->
                <% if (hasDiscount) { %>
                    <div class="current-setting">
                        <h3>📋 現在の割引設定</h3>
                        <div class="current-setting-content">
                            <strong><%= currentTime %></strong> 以降、
                            <strong style="color: #dc3545; font-size: 1.2rem;"><%= currentRate %>%OFF</strong>
                        </div>
                    </div>
                <% } else { %>
                    <div class="no-discount">
                        現在、割引設定はありません
                    </div>
                <% } %>

                <form id="discountForm" method="post" action="${pageContext.request.contextPath}/foodloss/DiscountSetting.action">
                    <!-- 時刻入力 -->
                    <div class="form-group">
                        <div class="form-row">
                            <label for="hour">開始時刻</label>
                            <div class="time-input-group">
                                <div class="time-input-wrapper">
                                    <input type="number"
                                           id="hour"
                                           name="hour"
                                           min="0"
                                           max="23"
                                           placeholder="18"
                                           required>
                                </div>
                                <span class="time-separator">:</span>
                                <div class="time-input-wrapper">
                                    <input type="number"
                                           id="minute"
                                           name="minute"
                                           min="0"
                                           max="59"
                                           placeholder="00"
                                           required>
                                </div>
                            </div>
                        </div>
                        <div class="error-message" id="timeError">時間は0〜23、分は0〜59の範囲で入力してください</div>
                    </div>

                    <!-- 割引パーセント入力 -->
                    <div class="form-group">
                        <div class="form-row">
                            <label for="discount">値引パーセント</label>
                            <div class="input-wrapper">
                                <input type="number"
                                       id="discount"
                                       name="discount"
                                       min="1"
                                       max="100"
                                       placeholder="例: 30"
                                       required>
                                <span class="unit">%</span>
                            </div>
                        </div>
                        <div class="error-message" id="discountError">割引率は1〜100の範囲で入力してください</div>
                    </div>

                    <!-- 送信ボタン -->
                    <button type="submit" class="btn-submit">設定</button>
                </form>

                <!-- 割引取り消しボタン -->
                <% if (hasDiscount) { %>
                    <button type="button" class="btn-cancel-discount" onclick="showCancelModal()">
                        割引設定を取り消す
                    </button>
                <% } %>

                <!-- 一覧に戻るボタン -->
                <button type="button" class="btn-back-list"
                        onclick="location.href='${pageContext.request.contextPath}/foodloss/MerchandiseList.action'">
                    一覧に戻る
                </button>
            </div>
        </div>
    </main>
</div>

<!-- 確認モーダル -->
<div id="cancelModal" class="modal">
    <div class="modal-content">
        <h3>⚠️ 確認</h3>
        <p>割引設定を取り消してもよろしいですか？</p>
        <p style="color: #666; font-size: 0.9rem;">この操作は元に戻せません。</p>
        <div class="modal-buttons">
            <button class="modal-btn modal-btn-cancel" onclick="closeCancelModal()">キャンセル</button>
            <button class="modal-btn modal-btn-confirm" onclick="cancelDiscount()">取り消す</button>
        </div>
    </div>
</div>

<!-- 取り消し用フォーム（非表示） -->
<form id="cancelForm" method="post" action="${pageContext.request.contextPath}/foodloss/DiscountCancel.action" style="display:none;">
</form>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    // モーダル表示
    function showCancelModal() {
        document.getElementById('cancelModal').style.display = 'block';
    }

    // モーダル閉じる
    function closeCancelModal() {
        document.getElementById('cancelModal').style.display = 'none';
    }

    // 割引取り消し実行
    function cancelDiscount() {
        document.getElementById('cancelForm').submit();
    }

    // モーダル外クリックで閉じる
    window.onclick = function(event) {
        const modal = document.getElementById('cancelModal');
        if (event.target === modal) {
            closeCancelModal();
        }
    }

    // フォームバリデーション
    const form = document.getElementById('discountForm');
    const hourInput = document.getElementById('hour');
    const minuteInput = document.getElementById('minute');
    const discountInput = document.getElementById('discount');
    const timeError = document.getElementById('timeError');
    const discountError = document.getElementById('discountError');

    // 時間のバリデーション
    function validateTime() {
        const hourValue = parseInt(hourInput.value);
        const minuteValue = parseInt(minuteInput.value);

        let isValid = true;

        if (isNaN(hourValue) || hourValue < 0 || hourValue > 23) {
            isValid = false;
        }

        if (isNaN(minuteValue) || minuteValue < 0 || minuteValue > 59) {
            isValid = false;
        }

        if (!isValid) {
            timeError.classList.add('show');
            hourInput.style.borderColor = '#e74c3c';
            minuteInput.style.borderColor = '#e74c3c';
        } else {
            timeError.classList.remove('show');
            hourInput.style.borderColor = '#ddd';
            minuteInput.style.borderColor = '#ddd';
        }

        return isValid;
    }

    hourInput.addEventListener('input', validateTime);
    minuteInput.addEventListener('input', validateTime);

    // 割引率のバリデーション
    discountInput.addEventListener('input', function() {
        const value = parseInt(this.value);
        if (isNaN(value) || value < 1 || value > 100) {
            discountError.classList.add('show');
            this.style.borderColor = '#e74c3c';
        } else {
            discountError.classList.remove('show');
            this.style.borderColor = '#ddd';
        }
    });

    // フォーム送信時の最終チェック
    form.addEventListener('submit', function(e) {
        const hourValue = parseInt(hourInput.value);
        const minuteValue = parseInt(minuteInput.value);
        const discountValue = parseInt(discountInput.value);

        let hasError = false;

        if (isNaN(hourValue) || hourValue < 0 || hourValue > 23 ||
            isNaN(minuteValue) || minuteValue < 0 || minuteValue > 59) {
            timeError.classList.add('show');
            hourInput.style.borderColor = '#e74c3c';
            minuteInput.style.borderColor = '#e74c3c';
            hasError = true;
        }

        if (isNaN(discountValue) || discountValue < 1 || discountValue > 100) {
            discountError.classList.add('show');
            discountInput.style.borderColor = '#e74c3c';
            hasError = true;
        }

        if (hasError) {
            e.preventDefault();
            return false;
        }
    });

    // URLパラメータから成功メッセージを表示
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        document.getElementById('successMessage').classList.add('show');
        setTimeout(() => {
            document.getElementById('successMessage').classList.remove('show');
        }, 3000);
    }
</script>

</body>
</html>