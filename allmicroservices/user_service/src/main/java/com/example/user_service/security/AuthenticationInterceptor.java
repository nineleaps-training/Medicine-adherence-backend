package com.example.user_service.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.user_service.config.filter.UserDetailService;
import com.example.user_service.util.JwtUtil;

@Service
public class AuthenticationInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserDetailService userDetailService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        final String id = request.getParameter("userId");

        logger.info(authorizationHeader);

        try {
            if ((authorizationHeader != null) && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                logger.info(username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (username != null) {
            try {
                logger.info("Authenticating user with id :{}", id);

                UserDetails userDetails = userDetailService.loadUserByUsername(id);

                if (Boolean.FALSE.equals(jwtUtil.validateToken(jwt.trim(), userDetails, request))) {
                    if (request.getAttribute("expired").equals("true")) {
                        logger.info("expired jwt : {}", id);
                        response.setStatus(401);

                        return false;
                    }

                    logger.error("Unauthorized user : {}", id);
                    response.setStatus(403);

                    return false;
                } else {
                    logger.info("User Authenticated : {}", userDetails.getUsername());
                    response.setHeader("jwt", jwt);

                    return true;
                }
            } catch (Exception usernameNotFoundException) {
                logger.error("Error in finding user with Id :{}", id);
                response.setStatus(404);

                return false;
            }
        }

        response.setStatus(401);

        return false;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
