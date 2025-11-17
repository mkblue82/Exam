<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Favorite" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>通知設定</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
    /* 通知設定画面専用のスタイル */
    .notification-settings-container {
        max-width: 800px;
        margin: 20px auto;
        padding: 0 15px;
    }

    section h2 {
        color: #c07148;
        text-align: center;
        font-size: 1.8rem;
        border-bottom: 2px solid #c07148;
        padding-bottom: 1rem;
        margin-bottom: 2rem;
    }

    .description {
        color: #666;
        margin-bottom: 30px;
        font-size: 14px;
    }

    .empty-message {
        text-align: center;
        padding: 60px 20px;
        color: #999;
        background-color: #fafafa;
        border-radius: 8px;
        margin: 20px 0;
    }

    .empty-message-icon {
        font-size: 48px;
        margin-bottom: 15px;
    }

    .store-list {
        margin-bottom: 30px;
    }

    .store-item {
        border: 1px solid #e0e0e0;
        border-radius: 6px;
        padding: 20px;
        margin-bottom: 15px;
        background-color: #fafafa;
        transition: background-color 0.2s;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .store-item:hover {
        background-color: #f0f0f0;
    }

    .store-content {
        flex: 1;
    }

    .store-header {
        margin-bottom: 10px;
    }

    .store-name {
        font-size: 18px;
        font-weight: bold;
        color: #333;
    }

    .store-info {
        color: #666;
        font-size: 13px;
        margin-bottom: 15px;
    }

    .notification-toggle {
        display: flex;
        align-items: center;
        gap: 15px;
        flex-direction: row;
    }

    .toggle-label {
        font-size: 14px;
        color: #555;
        white-space: nowrap;
    }

    /* カスタムトグルスイッチ */
    .switch {
        position: relative;
        display: inline-block;
        width: 50px;
        height: 26px;
    }

    .switch input {
        opacity: 0;
        width: 0;
        height: 0;
    }

    .slider {
        position: absolute;
        cursor: pointer;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: #ccc;
        transition: .4s;
        border-radius: 26px;
    }

    .slider:before {
        position: absolute;
        content: "";
        height: 20px;
        width: 20px;
        left: 3px;
        bottom: 3px;
        background-color: white;
        transition: .4s;
        border-radius: 50%;
    }

    input:checked + .slider {
        background-color: #4CAF50;
    }

    input:checked + .slider:before {
        transform: translateX(24px);
    }

    .button-container {
        display: flex;
        gap: 15px;
        justify-content: center;
        margin-top: 30px;
    }

    .btn {
        padding: 12px 40px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s, transform 0.1s;
        font-weight: bold;
    }

    .btn:active {
        transform: scale(0.98);
    }

    .btn-primary {
        background: #c07148;
        color: #fff;
        box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
    }

    .btn-primary:hover {
        background: #a85d38;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
    }

    .btn-secondary {
        background-color: #999;
        color: white;
    }

    .btn-secondary:hover {
        background-color: #888;
    }

    .error-message {
        background-color: #ffebee;
        color: #c62828;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #c62828;
    }

    .success-message {
        background-color: #e8f5e9;
        color: #2e7d32;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #2e7d32;
    }
</style>
</head>
<body>
<div id="container">
  <!-- ヘッダー読み込み -->
  <jsp:include page="header_user.jsp" />

  <!-- メインコンテンツ -->
  <main class="column">
    <div class="main-contents">
      <section>
        <h2>通知設定</h2>

        <div class="notification-settings-container">
          <p class="description">お気に入り店舗ごとに通知の受け取り設定ができます</p>

          <!-- エラーメッセージ表示 -->
          <% String errorMessage = (String)request.getAttribute("errorMessage"); %>
          <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message">
              <%= errorMessage %>
            </div>
          <% } %>

          <!-- 成功メッセージ表示 -->
          <% String successMessage = (String)request.getAttribute("successMessage"); %>
          <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="success-message">
              <%= successMessage %>
            </div>
          <% } %>

          <form action="${pageContext.request.contextPath}/foodloss/NotificationSettings.action" method="post">
            <!-- お気に入り店舗取得 -->
            <%
            List<Favorite> favoriteStores = (List<Favorite>)request.getAttribute("favoriteStores");
            %>

            <% if (favoriteStores == null || favoriteStores.isEmpty()) { %>
              <div class="empty-message">
                <div class="empty-message-icon">📭</div>
                <p>お気に入り店舗が登録されていません</p>
              </div>
            <% } %>

            <!-- お気に入り店舗リスト -->
            <% if (favoriteStores != null && !favoriteStores.isEmpty()) { %>
              <div class="store-list">
                <% for (Favorite favorite : favoriteStores) { %>
                  <div class="store-item">
                    <div class="store-content">
                      <div class="store-header">
                        <div class="store-name"><%= favorite.getStoreName() %></div>
                      </div>
                      <% if (favorite.getStoreAddress() != null && !favorite.getStoreAddress().isEmpty()) { %>
                      <div class="store-info">
                        📍 <%= favorite.getStoreAddress() %>
                      </div>
                      <% } %>
                    </div>
                    <div class="notification-toggle">
                      <span class="toggle-label">通知を受け取る</span>
                      <label class="switch">
                        <input type="checkbox"
                               name="notification_<%= favorite.getStoreId() %>"
                               value="1"
                               <%= favorite.getNotificationSetting() ? "checked" : "" %>>
                        <span class="slider"></span>
                      </label>
                    </div>
                  </div>
                <% } %>
              </div>

              <!-- ボタン -->
              <div class="button-container">
                <button type="submit" class="btn btn-primary">保存</button>
              </div>
            <% } %>
          </form>
        </div>
      </section>
    </div>
  </main>

  <!-- フッター読み込み -->
  <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
