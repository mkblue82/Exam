package tool;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = { "/*" })
public class EncodingFilter implements Filter {

    // 静的リソース拡張子（必要なら追加）
    private static final Pattern STATIC_EXT = Pattern.compile(
        ".*\\.(css|js|mjs|png|jpg|jpeg|gif|svg|webp|ico|map|woff|woff2|ttf|eot)$",
        Pattern.CASE_INSENSITIVE
    );

    /**
	 * doFilterメソッド フィルター処理を記述
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {


        HttpServletRequest req = (HttpServletRequest) request;

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }

		System.out.println("フィルタの前処理");

        // 入力側の文字コードを指定
        request.setCharacterEncoding("UTF-8");
		System.out.println("--URL--");
		System.out.println(((HttpServletRequest) request).getRequestURI());
		System.out.println("▼パラメータ▼");
		for (String key : request.getParameterMap().keySet()) {
			System.out.println(key + " : " + request.getParameter(key));
		}
		System.out.println("▲パラメータ▲");


        // 静的リソースの Content-Type を触らない（DefaultServletに任せる）
        if (STATIC_EXT.matcher(uri).matches() || uri.startsWith("/resources/") || uri.startsWith("/static/")
            || uri.startsWith("/assets/") || uri.startsWith("/css/") || uri.startsWith("/js/")
            || uri.startsWith("/images/") ) {
            chain.doFilter(request, response);
            return;
        }


        // 動的レスポンスの文字コードを指定
        response.setCharacterEncoding("UTF-8");
        // Content-Typeの型は各Servlet/JSPに任せること
        // response.setContentType("text/html; charset=UTF-8");

		chain.doFilter(request, response);
		System.out.println("フィルタの後処理");
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
