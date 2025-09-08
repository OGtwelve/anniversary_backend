package org.zhejianglab.dxjh.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;


public class JwtUtil {

    private final Key key;
    private final long expireMinutes;

    public JwtUtil(String base64Secret, long expireMinutes) {
        // Base64 -> bytes -> HMAC key
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expireMinutes = expireMinutes;
    }

    public String generateToken(String username, Collection<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireMinutes * 60_000L);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", String.join(";", roles))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public List<String> getRoles(String token) {
        Object v = parse(token).getBody().get("roles");
        if (v == null) return Collections.emptyList();
        return Arrays.stream(v.toString().split(";"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public Set<String> extractRoles(Claims claims) {
        if (claims == null) return Collections.emptySet();
        Object raw = claims.get("roles"); // 你签发时用的字段名
        if (raw == null) {
            // 兜底：有些人把角色放在 authorities/scope
            raw = claims.get("authorities");
            if (raw == null) raw = claims.get("scope");
        }

        if (raw instanceof Collection<?>) {
            return ((Collection<?>) raw).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        }
        if (raw instanceof String) {
            String s = (String) raw;
            // 兼容逗号/空格分隔
            return Arrays.stream(s.split("[,\\s]+"))
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
