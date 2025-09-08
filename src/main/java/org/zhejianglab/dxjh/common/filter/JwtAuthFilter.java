package org.zhejianglab.dxjh.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.var;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zhejianglab.dxjh.common.utils.JwtUtil;
import org.zhejianglab.dxjh.common.utils.SecurityPaths;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final AntPathMatcher matcher = new AntPathMatcher();

    // 白名单：根据你的项目自行增减
    private static final List<String> WHITELIST = Arrays.asList(SecurityPaths.PUBLIC);

    public JwtAuthFilter(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getServletPath();
        return WHITELIST.stream().anyMatch(p -> matcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(req); // 从 Authorization/Cookie 中取
        try {
            if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = jwt.parse(token).getBody();
                String username = claims.getSubject();
                if (StringUtils.hasText(username)) {
                    Set<String> roles = jwt.extractRoles(claims); // 你已有的方法
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    roles.stream()
                                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                            .collect(Collectors.toList())
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            chain.doFilter(req, res);
        } catch (ExpiredJwtException ex) {
            // 过期给 401，更友好
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"message\":\"Token 已过期，请重新登录\"}");
        } catch (Exception ex) {
            // 其他异常，清上下文后继续交由后面的过滤器/异常处理
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        // 可选：支持从 Cookie 读取
        if (req.getCookies() != null) {
            for (var c : req.getCookies()) {
                if ("Authorization".equals(c.getName()) || "token".equalsIgnoreCase(c.getName())) {
                    String v = c.getValue();
                    if (StringUtils.hasText(v)) {
                        return v.startsWith("Bearer ") ? v.substring(7) : v;
                    }
                }
            }
        }
        return null;
    }
}
