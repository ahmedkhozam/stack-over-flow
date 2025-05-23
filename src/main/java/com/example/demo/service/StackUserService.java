package com.example.demo.service;


import com.example.demo.dto.StackUserDto;
import com.example.demo.dto.UserProfileDto;

import java.util.List;

public interface StackUserService {

    StackUserDto createStackUser(StackUserDto stackUserDto);

    StackUserDto getStackUserById(Long userId);

    StackUserDto updateStackUser(Long userId, StackUserDto stackUserDto); // ➡️ ميثود التحديث الجديدة

    void deleteStackUser(Long userId); // ➡️ ميثود الديليت الجديدة

    List<StackUserDto> getAllStackUsers();

    UserProfileDto getCurrentUserProfile();


}
