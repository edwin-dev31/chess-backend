package com.chess.game.config.jwt;

import com.chess.game.persistence.entity.PlayerEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final long expiration = 3600000;

	public String generateToken(PlayerEntity player) {
		return Jwts.builder()
			.setSubject(player.getEmail())
			.claim("id", player.getId())
			.claim("username", player.getUsername())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(key)
			.compact();
	}

	public String extractEmail(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
