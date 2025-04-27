package com.example.demo.controller;


import com.example.demo.dto.StackUserDto;
import com.example.demo.service.StackUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StackUserController {

    private final StackUserService stackUserService;

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

}
