package org.example.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.entity.UserDTO;

import java.util.Date;

public class JwtUtil {

    private static final String KEY = "douyin login password for signature";
    public static String createToken(long userId, String username) {
        String jws = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * 24))
                .claim("userId", "id:" + userId)
                .claim("username", username)
                .signWith(Keys.hmacShaKeyFor(KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return jws;
    }

    public static UserDTO parseToken(String jws) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(KEY.getBytes()))
                    .build()
                    .parseClaimsJws(jws);
            String userId = (String) claimsJws.getBody().get("userId");
            String username = (String) claimsJws.getBody().get("username");
            return new UserDTO(Long.valueOf(userId.split(":")[1]), username);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        } catch (JwtException e) {
            throw new RuntimeException("Token非法");
        }
    }

    public static void main(String[] args) {
        String jws = createToken(1l, "duuhiy");
        parseToken(jws);
    }
}
