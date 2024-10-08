package com.example.HotelDewer_app.infra.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private String expirationTime;

    public String generateAccessToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTime)))
                .signWith(getSignature())
                .compact();
    }

    public SecretKey getSignature(){
        byte [] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isValidToken(String token){
        try {
            Jwts.parser().verifyWith(getSignature()).build().parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String getUserFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    public Claims extractAllClaims(String token){
        Jws<Claims> claimsJws = Jwts.parser().verifyWith(getSignature()).build().parseSignedClaims(token);
        return claimsJws.getPayload();
    }
}
