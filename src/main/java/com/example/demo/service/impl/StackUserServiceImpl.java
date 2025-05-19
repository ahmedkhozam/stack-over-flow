package com.example.demo.service.impl;


import com.example.demo.dto.StackUserDto;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.service.StackUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StackUserServiceImpl implements StackUserService {

    private final StackUserRepository stackUserRepository;

    @Override
    public StackUserDto createStackUser(StackUserDto stackUserDto) {
        if (stackUserRepository.existsByEmail(stackUserDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        StackUser stackUser = StackUser.builder()
                .username(stackUserDto.getUsername())
                .email(stackUserDto.getEmail())
                .password(stackUserDto.getPassword()) // هنشفره لاحقًا مع Spring Security
                .bio(stackUserDto.getBio())
                .build();

        StackUser savedStackUser = stackUserRepository.save(stackUser);


        return mapToDto(savedStackUser);
    }

    @Override
    public StackUserDto getStackUserById(Long userId) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        return mapToDto(stackUser);
    }

    @Override
    public StackUserDto updateStackUser(Long userId, StackUserDto stackUserDto) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        // نحدث الفيلدز اللي مسموح بتحديثها
        stackUser.setUsername(stackUserDto.getUsername());
        stackUser.setEmail(stackUserDto.getEmail());
        stackUser.setBio(stackUserDto.getBio());
        // الباسورد مش هنحدثه هنا (نعمله EndPoint خاص بيه)

        StackUser updatedStackUser = stackUserRepository.save(stackUser);

        return mapToDto(updatedStackUser);
    }

    @Override
    public void deleteStackUser(Long userId) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        stackUserRepository.delete(stackUser);
    }

    @Override
    public List<StackUserDto> getAllStackUsers() {
        return stackUserRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    private StackUserDto mapToDto(StackUser stackUser) {
        StackUserDto stackUserDto = new StackUserDto();
        BeanUtils.copyProperties(stackUser, stackUserDto);
        return stackUserDto;
    }
}
