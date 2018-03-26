import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CookieFilter implements Filter {

	final String cookieName = "my_app_ck";

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain fc)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		boolean flag = manageIdp(sreq, sresp);
		if (flag == true)
			fc.doFilter(sreq, sresp);
	}

	private boolean manageIdp(ServletRequest sreq, ServletResponse sresp) throws IOException {
		String idpUrl = "http://localhost:8081/";
		HttpServletRequest req = (HttpServletRequest) sreq;
		HttpServletResponse resp = (HttpServletResponse) sresp;

		if (validateCookie(req, resp) == true) {
			return true;
		}
		System.out.println("not a valid cookie");
		String clientUrl =req.getRequestURL().toString();// req.getContextPath() + req.getServletPath();

		// value
		final Boolean useSecureCookie = false;
		final int expiryTime = 60 * 5; // 24h in seconds
		final String cookiePath = "/";

		Cookie cookie = new Cookie("clientUrl", clientUrl);

		cookie.setSecure(useSecureCookie); // determines whether the cookie
											// should only be sent using a
											// secure protocol, such as HTTPS or
											// SSL

		cookie.setMaxAge(expiryTime); // A negative value means that the cookie
										// is not stored persistently and will
										// be deleted when the Web browser
										// exits. A zero value causes the cookie
										// to be deleted.

		
		resp.addCookie(cookie);
		resp.sendRedirect(idpUrl);
		return false;
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	public boolean validateCookie(HttpServletRequest req, HttpServletResponse res) {
		boolean flag = false;
		Cookie[] cookies = req.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					String[] cookieArr = cookie.getValue().split(":");
					long cookieTime = Long.valueOf(cookieArr[1]);
					long timeLapsed = System.currentTimeMillis() - cookieTime;
					System.out.println("time: " + timeLapsed);
					if (timeLapsed > 15 * 1000) {
						flag = false;
					} else {
						flag = true;
					}
				}
			}
		}

		return flag;
	}

}
