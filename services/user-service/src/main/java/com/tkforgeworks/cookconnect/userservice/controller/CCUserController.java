package com.tkforgeworks.cookconnect.userservice.controller;

import com.tkforgeworks.cookconnect.userservice.model.dto.CCUserDto;
import com.tkforgeworks.cookconnect.userservice.model.dto.UpdateCCUserDTO;
import com.tkforgeworks.cookconnect.userservice.service.CCUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/cc-user")
@RequiredArgsConstructor
public class CCUserController {
    private final CCUserService ccUserService;

    //GET Mappings
    @GetMapping
    public ResponseEntity<List<CCUserDto>> getAllCCUsers() {
        return ResponseEntity.ok(ccUserService.getAllUsers());
    }

    @GetMapping("/{ccUserId}")
    public ResponseEntity<CCUserDto> getCCUser(@PathVariable("ccUserId") Long ccUserId) {
        return ResponseEntity.ok(ccUserService.findUser(ccUserId));
    }

    //POST Mappings
    @PostMapping
    public ResponseEntity<CCUserDto> createCCUser(@RequestBody CCUserDto ccUserDto) {
        CCUserDto createdUser = ccUserService.createUser(ccUserDto);
        URI location = URI.create("/cc-user/" + createdUser.id());
        return ResponseEntity.created(location).body(createdUser);
    }

//    @PostMapping("/{ccUserId}/password-new")
//    public ResponseEntity<String> addNewPassword(@PathVariable("ccUserId") Long ccUserId,
//                                                 @RequestBody PasswordDto password) {
//        ccUserService.addNewPassword(ccUserId, password);
//        return ResponseEntity.ok("success");
//    }

    //PUT Mapping
    @PutMapping("/{ccUserId}")
    public ResponseEntity<CCUserDto> updateCCUser(@PathVariable("ccUserId") Long ccUserId,
                                                  @RequestBody UpdateCCUserDTO updateCCUserDTO) {
        return ResponseEntity.ok(ccUserService.updateUser(ccUserId, updateCCUserDTO));
    }

    //DELETE Mapping
    @DeleteMapping("/{ccUserId}")
    public ResponseEntity<String> deleteCCUser(@PathVariable("ccUserId") Long ccUserId) {
        return ResponseEntity.ok(ccUserService.deleteUserById(ccUserId));
    }

}
