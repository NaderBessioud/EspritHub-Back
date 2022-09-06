package tn.esprithub.Security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;


@Component
public class JWTTokenProvider {
	
	private String secret="Secret";
	
	
	private long expirationDate=800000000;
	
	private static final Logger logger = LogManager.getLogger(JWTTokenProvider.class);
	
	
	
	public Authentication getAuthentication(String username,Collection<? extends GrantedAuthority> authorities,HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(username,null,authorities);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		System.out.print(secret);
		return authentication;
	}
	
	public String generateToken(UserPrincipale userPrincipal) {
		logger.info(new Date(System.currentTimeMillis()+expirationDate));
		Algorithm algorithm=Algorithm.HMAC256(secret.getBytes());
		String[] claims=getAuthoritiesFromUser(userPrincipal);
		return JWT.create().withIssuer("CROTUN_ADMIN")
				.withIssuedAt(Date.from(Instant.now()))
				
				.withSubject(userPrincipal.getUsername())
				.withArrayClaim("Authorities", claims)
				.withExpiresAt(new Date(System.currentTimeMillis()+expirationDate))
				.sign(algorithm);
		
				
		
	}
	
	
	public String[] getAuthoritiesFromUser(UserPrincipale user) {
		List<String> authorities=new ArrayList<>();
		for(GrantedAuthority grantedAuthority :user.getAuthorities())
			authorities.add(grantedAuthority.getAuthority());
		return authorities.toArray(new String[0]);
	}
	
	private JWTVerifier getVerifier() {
		try {
			Algorithm algorithm=Algorithm.HMAC256(secret);
			JWTVerifier verifier=JWT.require(algorithm).withIssuer("CROTUN_ADMIN").build();
			return verifier;
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
			return null;
		}
	}
	
	
	public String[] getAuthoritiesFromToken(String token) {
		
		JWTVerifier verifier=getVerifier();
		return verifier.verify(token).getClaim("Authorities").asArray(String.class);
			
	}
	
/*	private Boolean isTokenExpired(String token) {
		JWTVerifier verifier=getVerifier();
		return verifier.verify(token).getExpiresAt().after(new Date(System.currentTimeMillis()+expirationDate));
	}*/
	
	public String getSubjectFromToken(String token) {
		/*JWTVerifier verifier=getVerifier();
		System.out.print(JWT.decode(token));
		
		return verifier.verify(token).getSubject();*/
		
		if(isTokenValid(token)) {
		Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
		return claims.getSubject();
		}
		return null;
		
	}
	
	public boolean isTokenValid(String token) {
		try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);

        } catch (SignatureException ex) {
            
        	logger.info("Signature exception");

        } catch (MalformedJwtException ex) {
        	logger.info("Mal foremed Token");

        } catch (ExpiredJwtException ex) {
        	logger.info("token expired");
        	

        } catch (IllegalArgumentException ex) {
        	logger.info("Illegal Argument");
        }
		
        
        return true;
    }

}
