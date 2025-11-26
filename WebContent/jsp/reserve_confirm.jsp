<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise, bean.Store, bean.User" %>
<%
    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("user") == null) {
        request.setAttribute("errorMessage", "セッションが切れています。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }

    Merchandise merch = (Merchandise) request.getAttribute("merchandise");
    Store store = (Store) request.getAttribute("store");
    Integer quantity = (Integer) request.getAttribute("quantity");
    Integer totalPrice = (Integer) request.getAttribute("totalPrice");

    if (merch == null || quantity == null) {
        request.setAttribute("errorMessage", "予約情報が取得できませんでした。");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>予約内容確認</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.confirm-box {
    max-width: 700px;
    margin: 40px auto;
    padding: 30px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}

.confirm-title {
    font-size: 1.8rem;
    font-weight: bold;
    color: #c07148;
    text-align: center;
    margin-bottom: 30px;
    border-bottom: 2px solid #c07148;
    padding-bottom: 10px;
}

.confirm-info {
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
}

.info-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #e0e0e0;
}

.info-row:last-child {
    border-bottom: none;
}

.info-label {
    width: 150px;
    font-weight: bold;
    color: #555;
}

.info-value {
    flex: 1;
    color: #333;
}

.total-price {
    font-size: 1.3rem;
    color: #c07148;
    font-weight: bold;
}

/* 受け取り時刻選択 */
.pickup-section {
    background: #fff8f0;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 25px;
    border: 2px solid #c07148;
}

.pickup-title {
    font-size: 1.2rem;
    font-weight: bold;
    color: #c07148;
    margin-bottom: 15px;
}

.pickup-inputs {
    display: flex;
    gap: 15px;
    align-items: center;
    flex-wrap: wrap;
}

.pickup-inputs label {
    font-weight: bold;
    color: #333;
}

.pickup-inputs input[type="date"],
.pickup-inputs input[type="time"] {
    padding: 10px;
    font-size: 1rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    min-width: 150px;
}

.required-mark {
    color: #d9534f;
    font-weight: bold;
    margin-left: 5px;
}

/* ボタン */
.button-group {
    display: flex;
    gap: 15px;
    justify-content: center;
    margin-top: 30px;
}

.btn {
    padding: 12px 40px;
    border: none;
    border-radius: 5px;
    font-size: 1.1rem;
    cursor: pointer;
    text-decoration: none;
    display: inline-block;
    transition: opacity 0.3s;
}

.btn-confirm {
    background: #c07148;
    color: white;
}

.btn-cancel {
    background: #999;
    color: white;
}

.btn:hover {
    opacity: 0.8;
}

.error-message {
    color: #d9534f;
    background: #fdeaea;
    padding: 10px;
    border-radius: 5px;
    margin-bottom: 15px;
    display: none;
}
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="header_user.jsp" />

    <main class="column">
        <div class="confirm-box">
            <div class="confirm-title">予約内容の確認</div>

            <div id="errorMsg" class="error-message"></div>

            <!-- 予約内容 -->
            <div class="confirm-info">
                <div class="info-row">
                    <div class="info-label">商品名:</div>
                    <div class="info-value"><%= merch.getMerchandiseName() %></div>
                </div>
                <% if (store != null) { %>
                <div class="info-row">
                    <div class="info-label">店舗名:</div>
                    <div class="info-value"><%= store.getStoreName() %></div>
                </div>
                <% } %>
                <div class="info-row">
                    <div class="info-label">単価:</div>
                    <div class="info-value">¥<%= merch.getPrice() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">予約数量:</div>
                    <div class="info-value">
                        <input type="number" id="quantityInput" name="quantity"
                               min="1" max="<%= merch.getStock() %>" value="<%= quantity %>"
                               style="width:80px; padding:5px; font-size:1rem; border:1px solid #ddd; border-radius:5px;">
                        個 (在庫: <%= merch.getStock() %>個)
                    </div>
                </div>
                <div class="info-row">
                    <div class="info-label">消費期限:</div>
                    <div class="info-value"><%= merch.getUseByDate() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">合計金額:</div>
                    <div class="info-value total-price" id="totalPrice">¥<%= totalPrice %></div>
                </div>
            </div>

            <!-- 受け取り時刻選択 -->
            <div class="pickup-section">
                <div class="pickup-title">
                    受け取り希望日時<span class="required-mark">*</span>
                </div>
                <div class="pickup-inputs">
                    <label for="pickupDate">日付:</label>
                    <input type="date" id="pickupDate" name="pickupDate" required>

                    <label for="pickupTime">時刻:</label>
                    <input type="time" id="pickupTime" name="pickupTime" required>
                </div>
                <p style="margin-top:10px; color:#666; font-size:0.9rem;">
                    ※ 店舗の営業時間内、かつ消費期限(<%= merch.getUseByDate() %>)までにご指定ください
                </p>
            </div>

            <!-- ボタン -->
            <form id="reserveForm" action="<%= request.getContextPath() %>/foodloss/ReserveExecute.action" method="post">
                <input type="hidden" name="merchandiseId" value="<%= merch.getMerchandiseId() %>">
                <input type="hidden" id="quantityHidden" name="quantity">
                <input type="hidden" id="pickupDateTime" name="pickupDateTime">

                <div class="button-group">
                    <button type="submit" class="btn btn-confirm">予約を確定する</button>
                    <a href="javascript:history.back();" class="btn btn-cancel">戻る</a>
                </div>
            </form>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="footer.jsp" />
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const pickupDateInput = document.getElementById('pickupDate');
    const pickupTimeInput = document.getElementById('pickupTime');
    const quantityInput = document.getElementById('quantityInput');
    const totalPriceElement = document.getElementById('totalPrice');
    const form = document.getElementById('reserveForm');
    const errorMsg = document.getElementById('errorMsg');

    const unitPrice = <%= merch.getPrice() %>;
    const maxStock = <%= merch.getStock() %>;
    const useByDate = '<%= merch.getUseByDate() %>'; // 消費期限 (YYYY-MM-DD形式を想定)

    // 数量変更時に合計金額を更新
    quantityInput.addEventListener('input', function() {
        let qty = parseInt(this.value);

        // バリデーション
        if (qty < 1) {
            qty = 1;
            this.value = 1;
        }
        if (qty > maxStock) {
            qty = maxStock;
            this.value = maxStock;
        }

        // 合計金額を更新
        const total = unitPrice * qty;
        totalPriceElement.textContent = '¥' + total.toLocaleString();
    });

    // 今日の日付を最小値に設定
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    pickupDateInput.min = yyyy + '-' + mm + '-' + dd;

    // 消費期限を最大値に設定
    pickupDateInput.max = useByDate;

    // デフォルトで明日の日付を設定
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowStr = tomorrow.getFullYear() + '-' +
                        String(tomorrow.getMonth() + 1).padStart(2, '0') + '-' +
                        String(tomorrow.getDate()).padStart(2, '0');
    pickupDateInput.value = tomorrowStr;

    // デフォルトで10:00を設定
    pickupTimeInput.value = '10:00';

    // フォーム送信時のバリデーション
    form.addEventListener('submit', function(e) {
        e.preventDefault();

        const date = pickupDateInput.value;
        const time = pickupTimeInput.value;
        const quantity = parseInt(quantityInput.value);

        // 入力チェック
        if (!date || !time) {
            showError('受け取り日時を入力してください。');
            return false;
        }

        if (quantity < 1 || quantity > maxStock) {
            showError('予約数量は1個以上、在庫数以下で指定してください。');
            return false;
        }

        // 日時を組み合わせる
        const pickupDateTime = date + ' ' + time + ':00';
        document.getElementById('pickupDateTime').value = pickupDateTime;
        document.getElementById('quantityHidden').value = quantity;

        // 過去の日時チェック
        const selectedDateTime = new Date(date + 'T' + time);
        const now = new Date();
        if (selectedDateTime <= now) {
            showError('受け取り日時は現在より後の日時を指定してください。');
            return false;
        }

        // 消費期限チェック
        const useByDateTime = new Date(useByDate + 'T23:59:59');
        if (selectedDateTime > useByDateTime) {
            showError('受け取り日時は消費期限(' + useByDate + ')内で指定してください。');
            return false;
        }

        // エラーがなければ送信
        form.submit();
    });

    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
});
</script>

</body>
</html>