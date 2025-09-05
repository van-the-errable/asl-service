package com.austinscotchlovers.asl_service.users.security;

import com.austinscotchlovers.asl_service.users.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUser.TestSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "testUser";

    String email() default "test@example.com";

    String[] roles() default "USER";

    long id() default 1L;

    class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
            User user = User.builder()
                    .withUsername(annotation.username())
                    .withEmail(annotation.email())
                    .withRole(Role.valueOf(annotation.roles()[0]))
                    .build();
            user.setId(annotation.id());

            CustomUserPrincipal principal = new CustomUserPrincipal(user);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    "password",
                    Arrays.stream(annotation.roles())
                            .map(role -> "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .toList());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);

            return context;
        }
    }
}
