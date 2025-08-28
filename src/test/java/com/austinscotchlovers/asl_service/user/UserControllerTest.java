package com.austinscotchlovers.asl_service.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_redirect_when_not_authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void should_return_ok_when_authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/123"))
                .andExpect(status().isOk());
    }
}