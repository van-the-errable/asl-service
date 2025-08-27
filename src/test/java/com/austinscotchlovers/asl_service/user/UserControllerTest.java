package com.austinscotchlovers.asl_service.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userProfileShouldRedirectWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void userProfileIsSecuredWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/123")
                        .with(authentication(createMockOAuth2Authentication())))
                .andExpect(status().isOk())
                .andExpect(content().string("Viewing profile for user with ID: 123. You are logged in as: mock_user."));
    }

    private Authentication createMockOAuth2Authentication() {
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getName()).thenReturn("mock_user");
        when(oauth2User.getAttributes()).thenReturn(Map.of("name", "mock_user"));

        return new OAuth2AuthenticationToken(oauth2User, Collections.emptyList(), "google");
    }
}