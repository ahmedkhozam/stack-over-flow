package com.example.demo.service.impl;


import com.example.demo.dto.StackUserDto;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StackUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StackUserServiceImplTest {

    @InjectMocks
    private StackUserServiceImpl stackUserService;

    @Mock
    private StackUserRepository stackUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateStackUserSuccessfully() {
        // Arrange
        StackUserDto dto = StackUserDto.builder()
                .username("ahmed")
                .email("ahmed@test.com")
                .password("123456")
                .bio("Java Dev")
                .build();

        StackUser saved = StackUser.builder()
                .id(1L)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .bio(dto.getBio())
                .reputation(0)
                .build();

        when(stackUserRepository.save(any(StackUser.class))).thenReturn(saved);

        // Act
        StackUserDto result = stackUserService.createStackUser(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ahmed@test.com");

        verify(stackUserRepository, times(1)).save(any(StackUser.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        StackUserDto dto = StackUserDto.builder()
                .username("ali")
                .email("ali@test.com")
                .password("123456")
                .bio("Dev")
                .build();

        // نخلي الريبو يرجع true لما ننده عليه بـ existsByEmail
        when(stackUserRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> stackUserService.createStackUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");

        verify(stackUserRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        // Arrange
        StackUser user = StackUser.builder()
                .id(1L)
                .username("ali")
                .email("ali@test.com")
                .password("123456")
                .bio("Java Dev")
                .reputation(10)
                .build();

        when(stackUserRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        StackUserDto result = stackUserService.getStackUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("ali");

        verify(stackUserRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserIdNotFound() {
        // Arrange
        Long userId = 100L;
        when(stackUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> stackUserService.getStackUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("StackUser not found with id: " + userId);

        verify(stackUserRepository, times(1)).findById(userId);
    }
    @Test
    void shouldUpdateStackUserSuccessfully() {
        // Arrange
        Long userId = 1L;

        StackUser existingUser = StackUser.builder()
                .id(userId)
                .username("oldUser")
                .email("old@test.com")
                .bio("old bio")
                .password("oldpass")
                .reputation(10)
                .build();

        StackUserDto updateDto = StackUserDto.builder()
                .username("newUser")
                .email("new@test.com")
                .bio("updated bio")
                .password("newpass") // مش هيستخدم، بس موجود
                .build();

        StackUser updatedUser = StackUser.builder()
                .id(userId)
                .username("newUser")
                .email("new@test.com")
                .bio("updated bio")
                .password("oldpass") // الباسورد مش بيتغير هنا
                .reputation(10)
                .build();

        when(stackUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(stackUserRepository.save(any(StackUser.class))).thenReturn(updatedUser);

        // Act
        StackUserDto result = stackUserService.updateStackUser(userId, updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newUser");
        assertThat(result.getEmail()).isEqualTo("new@test.com");

        verify(stackUserRepository, times(1)).findById(userId);
        verify(stackUserRepository, times(1)).save(any(StackUser.class));
    }
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        // Arrange
        Long userId = 100L;

        StackUserDto updateDto = StackUserDto.builder()
                .username("anyUser")
                .email("any@test.com")
                .bio("any bio")
                .password("123456")
                .build();

        when(stackUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> stackUserService.updateStackUser(userId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("StackUser not found with id: " + userId);

        verify(stackUserRepository, times(1)).findById(userId);
        verify(stackUserRepository, never()).save(any());
    }
    @Test
    void shouldDeleteExistingUserSuccessfully() {
        // Arrange
        Long userId = 1L;

        StackUser existingUser = StackUser.builder()
                .id(userId)
                .username("toDelete")
                .email("delete@test.com")
                .bio("some bio")
                .password("123456")
                .reputation(0)
                .build();

        when(stackUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(stackUserRepository).delete(existingUser);

        // Act
        stackUserService.deleteStackUser(userId);

        // Assert
        verify(stackUserRepository, times(1)).findById(userId);
        verify(stackUserRepository, times(1)).delete(existingUser);
    }
    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        // Arrange
        Long userId = 99L;
        when(stackUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> stackUserService.deleteStackUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("StackUser not found with id: " + userId);

        verify(stackUserRepository, times(1)).findById(userId);
        verify(stackUserRepository, never()).delete(any());
    }

}
