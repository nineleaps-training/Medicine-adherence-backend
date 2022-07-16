package com.example.user_service.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.user_service.exception.user.UnauthorizedUserException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.util.Messages;
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

        try {
            if ((authorizationHeader != null) && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);

            }
            if (username != null) {
                try {
                    logger.info("Authenticating user with id :{}", id);

                    return checkforJwt(response,jwt,request,id);
                } catch (Exception usernameNotFoundException) {
                    logger.error("Error in finding user with Id :{}", id);
                    throw new UserExceptionMessage(Messages.USER_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);

        }



        response.setStatus(401);

        return false;
    }

    private boolean checkforJwt(HttpServletResponse response,String jwt,HttpServletRequest request,String id) {
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
    }
}
