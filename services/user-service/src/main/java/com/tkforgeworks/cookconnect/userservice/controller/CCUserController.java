package com.tkforgeworks.cookconnect.userservice.controller;

import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.UpdateCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.service.CCUserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CCUserController {
    private final CCUserService ccUserService;

    //GET Mappings
    @GetMapping("/")
    @RateLimiter(name = "main")
    public ResponseEntity<List<CCUserDto>> getAllCCUsers() {
        return ResponseEntity.ok(ccUserService.getAllUsers());
    }

    @GetMapping("/{ccUserId}")
    @RateLimiter(name = "main")
    public ResponseEntity<CCUserDto> getCCUser(@PathVariable("ccUserId") String ccUserId) {
        return ResponseEntity.ok(ccUserService.findUser(ccUserId));
    }

    //POST Mappings
    @PostMapping("/")
    public ResponseEntity<CCUserDto> createCCUser(@RequestBody CCUserDto ccUserDto) {
        CCUserDto createdUser = ccUserService.createUser(ccUserDto);
        URI location = URI.create("/cc-user/" + createdUser.id());
        return ResponseEntity.created(location).body(createdUser);
    }

    //PUT Mapping
    @PutMapping("/{ccUserId}")
    public ResponseEntity<CCUserDto> updateCCUser(@PathVariable("ccUserId") String ccUserId,
                                                  @RequestBody UpdateCCUserDTO updateCCUserDTO) {
        return ResponseEntity.ok(ccUserService.updateUser(ccUserId, updateCCUserDTO));
    }

    //DELETE Mapping
    @DeleteMapping("/{ccUserId}")
    public ResponseEntity<String> deleteCCUser(@PathVariable("ccUserId") String ccUserId) {
        return ResponseEntity.ok(ccUserService.deleteUserById(ccUserId));
    }

}
