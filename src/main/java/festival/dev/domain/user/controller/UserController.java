package festival.dev.domain.user.controller;

import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.dto.UserDto;
import festival.dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        return ResponseEntity.ok(userService.findById(id));
//    }


    @GetMapping("/{email}")
    public UserDto getUser(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}

