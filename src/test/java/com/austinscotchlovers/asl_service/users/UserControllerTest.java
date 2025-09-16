package com.austinscotchlovers.asl_service.users;

import com.austinscotchlovers.asl_service.exceptions.DuplicateUserException;
import com.austinscotchlovers.asl_service.users.dto.UserUpdateDto;
import com.austinscotchlovers.asl_service.users.security.Role;
import com.austinscotchlovers.asl_service.users.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    private UserUpdateDto testUserUpdateDto;

    @BeforeEach
    void setUp() {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        testUser = User.builder()
                .withEmail("test@example.com")
                .withUsername("testUser")
                .withRole(Role.MEMBER)
                .withFirstName("Test")
                .withLastName("User")
                .withName("Test User")
                .withProfilePictureUrl("http://example.com/pic.jpg")
                .withPhoneNumber("555-123-4567")
                .build();
        testUser.setId(1L);

        testUserUpdateDto = new UserUpdateDto(
                "updated@example.com",
                "updatedUser",
                "Updated",
                "User",
                "Updated User",
                "http://updated-example.com/pic.jpg",
                "555-987-6543",
                null
        );
    }

    @Test
    @WithMockCustomUser(username = "adminUser", roles = "ADMIN")
    void should_return_all_users_when_admin_gets_all() throws Exception {
        List<User> userList = Arrays.asList(
                testUser,
                User.builder().withEmail("test2@example.com").withUsername("testUser2").withRole(Role.MEMBER).build()
        );
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockCustomUser(username = "adminUser", roles = "ADMIN")
    void should_return_user_when_admin_gets_by_id() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockCustomUser(username = "adminUser", roles = "ADMIN")
    void should_update_user_when_admin_puts_by_id() throws Exception {
        User updatedUser = User.builder()
                .withEmail("updated@example.com")
                .withUsername("updatedUser")
                .withRole(Role.ADMIN)
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserUpdateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockCustomUser(username = "adminUser", roles = "ADMIN")
    void should_delete_user_when_admin_deletes_by_id() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockCustomUser(roles = "ADMIN")
    void should_return_not_found_when_user_to_update_does_not_exist() throws Exception {
        UserUpdateDto updatedDto = new UserUpdateDto(
                "nonexistent@example.com",
                "nonexistent",
                "validFirst",
                "validLast",
                "validName",
                null,
                null,
                null
        );
        when(userService.updateUser(eq(100L), any(UserUpdateDto.class))).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/v1/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockCustomUser(roles = "ADMIN")
    void should_return_bad_request_when_updating_with_invalid_data() throws Exception {
        UserUpdateDto invalidDto = new UserUpdateDto(
                "",
                "valid_username",
                "valid_first",
                "valid_last",
                "valid_name",
                "not-a-url",
                "123",
                null
        );

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_conflict_when_creating_user_with_duplicate_data() throws Exception {
        User newUser = User.builder()
                .withEmail("existing@example.com")
                .withUsername("existingUser")
                .withRole(Role.MEMBER)
                .build();

        when(userService.saveUser(any(User.class))).thenThrow(new DuplicateUserException("Email or username already exists"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockCustomUser(roles = "MEMBER")
    void should_return_user_when_member_gets_self_by_id() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockCustomUser(roles = "MEMBER")
    void should_update_user_when_member_puts_self_by_id() throws Exception {
        User updatedUser = User.builder()
                .withEmail("updated@example.com")
                .withUsername("updatedUser")
                .withRole(Role.MEMBER)
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserUpdateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockCustomUser(roles = "MEMBER")
    void should_delete_user_when_member_deletes_self_by_id() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockCustomUser(username = "regularUser", roles = "MEMBER")
    void should_return_forbidden_when_member_gets_all_users() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "anotherUser", email = "another@example.com", roles = "MEMBER", id = 2L)
    void should_return_forbidden_when_member_gets_another_user_by_id() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(username = "anotherUser", email = "another@example.com", roles = "MEMBER", id = 2L)
    void should_return_forbidden_when_member_deletes_another_user_by_id() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_create_user_when_guest_posts_new_user() throws Exception {
        User newUser = User.builder()
                .withEmail("newuser@example.com")
                .withUsername("newUser")
                .withRole(Role.MEMBER)
                .build();

        when(userService.saveUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void should_return_unauthorized_when_guest_gets_all_users() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_unauthorized_when_guest_gets_user_by_id() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_unauthorized_when_guest_updates_user() throws Exception {
        UserUpdateDto updatedDto = new UserUpdateDto("guest@example.com", "guest", null, null, null, null, null, null);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_unauthorized_when_guest_deletes_user() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}