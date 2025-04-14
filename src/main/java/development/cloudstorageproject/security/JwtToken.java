package development.cloudstorageproject.security;

import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.exception.InvalidInputExpiredException;
import development.cloudstorageproject.exception.InvalidInputSignatureException;
import development.cloudstorageproject.exception.InvalidInputTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtToken {
    @Value("${jwt.signingKey}")
    private String secretKey;

    @Value("${jwt.ExpirationTime}")
    private long expirationTime;

    public String generateToken(@NonNull UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getLogin())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String getLoginFromAuthToken(String authToken) {
        String login = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken)
                .getPayload()
                .getSubject();

        return login;
    }

    public boolean validateJwtToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return (!isTokenExpired(expiration));
        } catch (ExpiredJwtException e) {
            throw new InvalidInputExpiredException("JWT token просрочился");
        } catch (SignatureException e) {
            throw new InvalidInputSignatureException("Неверная подпись JWT");
        } catch (Exception e) {
            throw new InvalidInputTokenException("Недопустимый токен JWT");
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    public Claims getAccessClaims(@NonNull String token) {
        return extractAllClaims(token);
    }
}
