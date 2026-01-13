package com.example.jibexample.controller;

import com.example.jibexample.entity.User;
import com.example.jibexample.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController单元测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "测试用户");
        testUser.setId(1L);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_Failure() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("用户名已存在: testuser"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在: testuser"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在，ID: 999"));

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void testGetUserByUsername_Success() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Given
        User user1 = new User("user1", "user1@example.com", "用户1");
        User user2 = new User("user2", "user2@example.com", "用户2");
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Given
        User updatedUser = new User("updateduser", "updated@example.com", "更新用户");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户更新成功"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUser_Failure() throws Exception {
        // Given
        User updatedUser = new User("updateduser", "updated@example.com", "更新用户");
        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("用户不存在，ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在，ID: 999"));

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_Failure() throws Exception {
        // Given
        doThrow(new RuntimeException("用户不存在，ID: 999"))
                .when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在，ID: 999"));

        verify(userService, times(1)).deleteUser(999L);
    }
}
