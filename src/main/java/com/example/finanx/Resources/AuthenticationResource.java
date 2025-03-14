package com.example.finanx.Resources;

import com.example.finanx.DTO.AuthenticationDTO;
import com.example.finanx.DTO.LoginResponseDTO;
import com.example.finanx.DTO.RegisterDTO;
import com.example.finanx.Entities.User;
import com.example.finanx.Infra.Security.TokenService;
import com.example.finanx.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository _repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data){
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            User user = (User) auth.getPrincipal();
            var token = tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(token, user.getId(), user.getEmail()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated RegisterDTO data){
        if(this._repository.findByEmail(data.email()) != null){
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        User newUser = new User(data.name(), data.lastName(), data.monthLimit(), data.email(), encryptedPassword, data.role());

        this._repository.save(newUser);

        return ResponseEntity.ok().build();
    }

}
