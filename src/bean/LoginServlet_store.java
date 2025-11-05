package com.onlinemall.servlet.sho;

import java.io.IOException;
import java.util.logging.LogManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.onlinemall.model.Shop;
import com.onlinemall.service.ShopAuthService;
import com.onlinemall.util.ValidationUtil;

/**
 * 店舗側専用ログイン処理Servlet
 * 
 * フロー:
 * 1. 店舗ID・パスワード入力
 * 2. 入力値バリデーション（未入力チェック、形式チェック）
 * 3. 認証処理（アカウントシステム連携）
 * 4. セッション作成
 * 5. 店舗管理画面へ遷移
 * 
 * @author Online Mall Development Team
 * @version 1.0
 */
@WebServlet(name = "ShopLoginServlet", urlPatterns = {"/shop/login"})
public class ShopLoginServlet extends HttpServlet {
    
    private static final Logger logger = LogManager.getLogger(ShopLoginServlet.class);
    private static final long serialVersionUID = 1L;
    
    // セッションタイムアウト設定
    private static final int SESSION_TIMEOUT_DEFAULT = 1800; // 30分
    private static final int SESSION_TIMEOUT_REMEMBER = 604800; // 7日間
    
    // ログイン試行制限
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    // サービス層
    private ShopAuthService shopAuthService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        shopAuthService = new ShopAuthService();
        logger.info("ShopLoginServlet initialized successfully");
    }
    
    /**
     * GET: ログイン画面表示
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 既にログイン済みの店舗はダッシュボードへ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("shopId") != null) {
            logger.info("Shop already logged in (ID: {}), redirecting to dashboard", 
                session.getAttribute("shopId"));
            response.sendRedirect(request.getContextPath() + "/shop/dashboard.jsp");
            return;
        }
        
        // ログイン画面を表示
        request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
    }
    
    /**
     * POST: ログイン認証処理
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 文字エンコーディング設定
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // リクエストパラメータ取得
        String shopId = request.getParameter("shopId");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // クライアント情報取得
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        logger.info("Shop login attempt - ShopID: {}, IP: {}", shopId, ipAddress);
        
        // ============================================
        // ステップ1: 入力値バリデーション
        // ============================================
        ValidationResult validation = validateLoginInput(shopId, password);
        if (!validation.isValid()) {
            logger.warn("Validation failed for shop login - ShopID: {}, Reason: {}", 
                shopId, validation.getErrorMessage());
            forwardWithError(request, response, validation.getErrorMessage(), shopId);
            return;
        }
        
        // ============================================
        // ステップ2: ログイン試行回数チェック
        // ============================================
        if (shopAuthService.isAccountLocked(shopId, ipAddress)) {
            logger.warn("Account locked - ShopID: {}, IP: {}", shopId, ipAddress);
            forwardWithError(request, response, 
                "ログイン試行回数が上限に達しました。" + LOCKOUT_DURATION_MINUTES + 
                "分後に再度お試しください", shopId);
            return;
        }
        
        try {
            // ============================================
            // ステップ3: 認証処理（アカウントシステム連携）
            // ============================================
            Shop shop = shopAuthService.authenticate(shopId, password);
            
            // ID・PWのどちらかが未入力または間違っている場合
            if (shop == null) {
                logger.warn("Authentication failed - ShopID: {}, IP: {}", shopId, ipAddress);
                
                // 失敗回数を記録
                int failCount = shopAuthService.recordFailedLogin(shopId, ipAddress);
                
                // 失敗回数に応じたエラーメッセージ
                String errorMsg = buildFailedLoginMessage(failCount);
                forwardWithError(request, response, errorMsg, shopId);
                return;
            }
            
            // ============================================
            // ステップ4: アカウント状態チェック
            // ============================================
            
            // 店舗アカウントが無効化されている場合
            if (!shop.isActive()) {
                logger.warn("Inactive shop account login attempt - ShopID: {}", shopId);
                forwardWithError(request, response, 
                    "この店舗アカウントは無効化されています。サポートセンターにお問い合わせください", 
                    shopId);
                return;
            }
            
            // 審査中の店舗の場合
            if ("pending".equals(shop.getStatus())) {
                logger.warn("Pending shop account login attempt - ShopID: {}", shopId);
                forwardWithError(request, response, 
                    "現在審査中です。審査完了までお待ちください", shopId);
                return;
            }
            
            // 出店停止中の店舗の場合
            if ("suspended".equals(shop.getStatus())) {
                logger.warn("Suspended shop account login attempt - ShopID: {}", shopId);
                forwardWithError(request, response, 
                    "出店が一時停止されています。詳細はサポートセンターまでお問い合わせください", 
                    shopId);
                return;
            }
            
            // ============================================
            // ステップ5: ログイン成功 - セッション作成
            // ============================================
            createShopSession(request, shop, rememberMe, ipAddress, userAgent);
            
            // 最終ログイン日時・IPアドレスを更新
            shopAuthService.updateLastLogin(shop.getShopId(), ipAddress);
            
            // ログイン失敗回数をリセット
            shopAuthService.resetFailedLoginCount(shopId, ipAddress);
            
            // ログイン成功ログ
            logger.info("Shop login successful - ShopID: {}, ShopName: {}, IP: {}", 
                shop.getShopId(), shop.getShopName(), ipAddress);
            
            // ============================================
            // ステップ6: メイン画面へ遷移
            // ============================================
            String redirectUrl = determineRedirectUrl(request, shop);
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            logger.error("Unexpected error during shop login - ShopID: " + shopId, e);
            forwardWithError(request, response, 
                "システムエラーが発生しました。しばらくしてから再度お試しください", shopId);
        }
    }
    
    /**
     * 入力値バリデーション
     * ロバストネス図: ID,PWのどちらかが未入力の場合のチェック
     */
    private ValidationResult validateLoginInput(String shopId, String password) {
        
        // 店舗ID未入力チェック
        if (shopId == null || shopId.trim().isEmpty()) {
            return ValidationResult.error("店舗IDを入力してください");
        }
        
        // パスワード未入力チェック
        if (password == null || password.trim().isEmpty()) {
            return ValidationResult.error("パスワードを入力してください");
        }
        
        // 店舗ID形式チェック（英数字、アンダースコア、ハイフンのみ）
        if (!ValidationUtil.isValidShopId(shopId)) {
            return ValidationResult.error(
                "店舗IDは英数字、アンダースコア、ハイフンのみで入力してください");
        }
        
        // 店舗ID長チェック（3-50文字）
        if (shopId.length() < 3 || shopId.length() > 50) {
            return ValidationResult.error("店舗IDは3文字以上50文字以内で入力してください");
        }
        
        // パスワード長チェック（8文字以上）
        if (password.length() < 8) {
            return ValidationResult.error("パスワードは8文字以上で入力してください");
        }
        
        // パスワード長チェック（上限100文字）
        if (password.length() > 100) {
            return ValidationResult.error("パスワードは100文字以内で入力してください");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 店舗セッション作成
     */
    private void createShopSession(HttpServletRequest request, 
                                   Shop shop,
                                   String rememberMe, 
                                   String ipAddress, 
                                   String userAgent) {
        
        // セッション固定攻撃対策: 既存セッションを無効化
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
            logger.debug("Old session invalidated for security");
        }
        
        // 新しいセッション作成
        HttpSession session = request.getSession(true);
        
        // セッションタイムアウト設定
        boolean isRememberMe = "on".equals(rememberMe) || "true".equals(rememberMe);
        int timeout = isRememberMe ? SESSION_TIMEOUT_REMEMBER : SESSION_TIMEOUT_DEFAULT;
        session.setMaxInactiveInterval(timeout);
        
        // セッションに店舗情報を格納
        session.setAttribute("shopId", shop.getShopId());
        session.setAttribute("shopName", shop.getShopName());
        session.setAttribute("shopOwnerId", shop.getOwnerId());
        session.setAttribute("shopOwnerName", shop.getOwnerName());
        session.setAttribute("shopEmail", shop.getEmail());
        session.setAttribute("shopPhone", shop.getPhone());
        session.setAttribute("shopCategory", shop.getCategory());
        session.setAttribute("shopStatus", shop.getStatus());
        session.setAttribute("userType", "shop_owner");
        session.setAttribute("loginTime", System.currentTimeMillis());
        session.setAttribute("ipAddress", ipAddress);
        
        // データベースにセッション記録
        shopAuthService.recordSession(
            session.getId(), 
            shop.getShopId(), 
            ipAddress, 
            userAgent,
            timeout
        );
        
        logger.debug("Shop session created - SessionID: {}, ShopID: {}, Timeout: {}s", 
            session.getId(), shop.getShopId(), timeout);
        
        if (isRememberMe) {
            logger.info("Remember me enabled for shop: {} (7 days)", shop.getShopId());
        }
    }
    
    /**
     * リダイレクト先URL決定
     */
    private String determineRedirectUrl(HttpServletRequest request, Shop shop) {
        String contextPath = request.getContextPath();
        
        // セッションに保存された戻り先URLがあればそこへ
        HttpSession session = request.getSession();
        String returnUrl = (String) session.getAttribute("returnUrl");
        if (returnUrl != null && !returnUrl.isEmpty()) {
            session.removeAttribute("returnUrl");
            logger.debug("Redirecting to saved return URL: {}", returnUrl);
            return returnUrl;
        }
        
        // 初回ログインの場合は設定画面へ
        if (shop.isFirstLogin()) {
            logger.debug("First login detected, redirecting to setup");
            return contextPath + "/shop/setup.jsp";
        }
        
        // デフォルトはダッシュボードへ
        return contextPath + "/shop/dashboard.jsp";
    }
    
    /**
     * ログイン失敗メッセージ生成
     */
    private String buildFailedLoginMessage(int failCount) {
        int remainingAttempts = MAX_LOGIN_ATTEMPTS - failCount;
        
        if (remainingAttempts <= 0) {
            return "ログイン試行回数が上限に達しました。" + 
                   LOCKOUT_DURATION_MINUTES + "分後に再度お試しください";
        } else if (remainingAttempts <= 2) {
            return "店舗IDまたはパスワードが正しくありません。\n" +
                   "残り" + remainingAttempts + "回失敗するとアカウントがロックされます";
        } else {
            return "店舗IDまたはパスワードが正しくありません";
        }
    }
    
    /**
     * エラーメッセージ付きでログイン画面へ転送
     */
    private void forwardWithError(HttpServletRequest request, 
                                 HttpServletResponse response,
                                 String errorMessage,
                                 String shopId) 
            throws ServletException, IOException {
        
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("shopId", shopId); // 入力値を保持
        request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
    }
    
    /**
     * クライアントIPアドレス取得（プロキシ対応）
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 複数IPが返される場合は最初のIPを使用
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    @Override
    public void destroy() {
        logger.info("ShopLoginServlet destroyed");
        super.destroy();
    }
    
    /**
     * バリデーション結果クラス
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}