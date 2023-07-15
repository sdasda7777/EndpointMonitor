package com.sdasda7777.endpointmonitor.security.authentication;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class KeycloakUserService {

    public static String getKeycloakId(HttpServletRequest request) {
        JwtAuthenticationToken principal =
                (JwtAuthenticationToken) request.getUserPrincipal();

        if (principal == null || !principal.isAuthenticated()
                || principal.getName() == null)
            throw new AuthenticationCredentialsNotFoundException("");

        return principal.getName();
    }
}
