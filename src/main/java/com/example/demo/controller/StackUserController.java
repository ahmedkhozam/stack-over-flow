package com.example.demo.controller;


import com.example.demo.dto.StackUserDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.service.StackUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class StackUserController {

    private final StackUserService stackUserService;

    public StackUserController(StackUserService stackUserService) {
        this.stackUserService = stackUserService;
    }

    // Create StackUser
    @PostMapping
    public ResponseEntity<StackUserDto> createStackUser(@Valid @RequestBody StackUserDto stackUserDto) {
        StackUserDto createdUser = stackUserService.createStackUser(stackUserDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Get StackUser by ID
    @GetMapping("/{id}")
    public ResponseEntity<StackUserDto> getStackUserById(@PathVariable Long id) {
        StackUserDto userDto = stackUserService.getStackUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<StackUserDto>> getAllStackUsers() {
        List<StackUserDto> users = stackUserService.getAllStackUsers();
        return ResponseEntity.ok(users);
    }

    // Update StackUser
    @PutMapping("/{id}")
    public ResponseEntity<StackUserDto> updateStackUser(@PathVariable Long id, @Valid @RequestBody StackUserDto stackUserDto) {
        StackUserDto updatedUser = stackUserService.updateStackUser(id, stackUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete StackUser
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStackUser(@PathVariable Long id) {
        stackUserService.deleteStackUser(id);
        return ResponseEntity.ok("StackUser deleted successfully!");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(stackUserService.getCurrentUserProfile());
    }


}
