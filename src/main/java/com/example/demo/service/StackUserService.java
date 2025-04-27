package com.example.demo.service;


import com.example.demo.dto.StackUserDto;

public interface StackUserService {

    StackUserDto createStackUser(StackUserDto stackUserDto);

    StackUserDto getStackUserById(Long userId);

    StackUserDto updateStackUser(Long userId, StackUserDto stackUserDto); // ➡️ ميثود التحديث الجديدة

    void deleteStackUser(Long userId); // ➡️ ميثود الديليت الجديدة
}
