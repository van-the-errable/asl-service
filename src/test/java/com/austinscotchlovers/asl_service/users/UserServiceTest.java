package com.austinscotchlovers.asl_service.users;

import com.austinscotchlovers.asl_service.exceptions.DuplicateUserException;
import com.austinscotchlovers.asl_service.users.dto.UserUpdateDto;
import com.austinscotchlovers.asl_service.users.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final Long userId = 1L;
    private UserUpdateDto testUserUpdateDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testUserUpdateDto = new UserUpdateDto(
                "jane.smith@example.com",
                "janesmith",
                "Jane",
                "Smith",
                "Jane Smith",
                "http://new-pic.url/jane.jpg",
                "555-123-4567",
                null
        );
    }

    @Test
    void should_return_all_users_when_getting_all() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("John", users.getFirst().getFirstName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void should_return_user_when_getting_by_id_and_user_exists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void should_return_empty_optional_when_getting_by_id_and_user_does_not_exist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(userId);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void should_save_user_when_saving_new_user() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User savedUser = userService.saveUser(testUser);

        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void should_throw_exception_when_saving_user_with_duplicate_email() {
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(RuntimeException.class, () -> userService.saveUser(testUser));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void should_update_user_when_user_exists() {
        UserUpdateDto updatedDto = new UserUpdateDto(
                "jane.smith@example.com",
                "janesmith",
                "Jane",
                "Smith",
                "Jane Smith",
                "http://new-pic.url/jane.jpg",
                "555-123-4567",
                null
        );

        User expectedUserAfterUpdate = new User();
        expectedUserAfterUpdate.setFirstName("Jane");
        expectedUserAfterUpdate.setLastName("Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(expectedUserAfterUpdate);

        User result = userService.updateUser(userId, updatedDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).updateUserFromDto(updatedDto, testUser);
    }

    @Test
    void should_throw_exception_when_updating_user_with_duplicate_email() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateUserException.class, () -> userService.updateUser(userId, testUserUpdateDto));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void should_throw_exception_when_updating_nonexistent_user() {
        UserUpdateDto updatedDto = new UserUpdateDto(
                "jane.smith@example.com",
                "janesmith",
                "Jane",
                "Smith",
                "Jane Smith",
                "http://new-pic.url/jane.jpg",
                "555-123-4567",
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(userId, updatedDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_delete_user_by_id() {
        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}