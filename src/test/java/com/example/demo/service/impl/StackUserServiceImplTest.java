package com.example.demo.service.impl;


import com.example.demo.dto.StackUserDto;
import com.example.demo.entity.StackUser;
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

}
