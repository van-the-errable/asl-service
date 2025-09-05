package com.austinscotchlovers.asl_service.users;

import com.austinscotchlovers.asl_service.exceptions.DuplicateUserException;
import com.austinscotchlovers.asl_service.users.dto.UserUpdateDto;
import com.austinscotchlovers.asl_service.users.mapper.UserMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateUserException("Email or username already exists");
        }
    }

    public User updateUser(Long id, UserUpdateDto updatedDto) {
        return userRepository.findById(id).map(user -> {
            userMapper.updateUserFromDto(updatedDto, user);
            try {
                return userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateUserException("Email or username already exists");
            }
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}