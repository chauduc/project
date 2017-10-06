package com.duc.aws.project.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.duc.aws.project.model.JwtUser;
import com.duc.aws.project.services.JwtService;

import io.jsonwebtoken.JwtException;

/**
 * @author m00306
 *
 */
@WebFilter(urlPatterns = { "/api/secure/*" })
public class JwtFilter implements Filter {
	@Autowired
    private JwtService jwtTokenService;

    @Value("${jwt.auth.header}")
    String authHeader;

    @Override 
    public void init(FilterConfig filterConfig) throws ServletException  {}
    
    @Override 
    public void destroy() {}

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String authHeaderVal = httpRequest.getHeader(authHeader);

        if (null==authHeaderVal)
        {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try
        {
            JwtUser jwtUser = jwtTokenService.getUser(authHeaderVal);
            httpRequest.setAttribute("jwtUser", jwtUser);
        }
        catch(JwtException e)
        {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        chain.doFilter(httpRequest, httpResponse);
    }
}
