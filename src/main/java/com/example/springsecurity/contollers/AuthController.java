package com.example.springsecurity.contollers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springsecurity.dto.AuthRequestDto;
import com.example.springsecurity.dto.AuthResponseDto;
import com.example.springsecurity.dto.RegisterDto;
import com.example.springsecurity.models.Customer;
import com.example.springsecurity.repositories.CustomerRepository;
import com.example.springsecurity.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth/")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private CustomerRepository customerRepository;

    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Value("${security.key}")
    private String secretValue;

    public AuthController(AuthenticationManager authenticationManager, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity<?> authenticateCustomer(@RequestBody AuthRequestDto authRequestDto){
        log.info("authenticateCustomer ");
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword()));
            Customer customer = (Customer) authentication.getPrincipal();

                Algorithm algorithm = Algorithm.HMAC256(secretValue);
                String token = JWT.create()
                        .withSubject(customer.getUsername() )
                        .withIssuer("auth0")
                        .withClaim("roles", customer.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .sign(algorithm);

            AuthResponseDto authResponseDto =  new AuthResponseDto(customer.getUsername(), token);
            System.out.println("authRequestDto = " + authRequestDto + "Token: " + token);
            log.info("User is logg in");
            return  ResponseEntity.ok(authResponseDto);

        }catch (UsernameNotFoundException exception){
            log.info("User not found " + exception);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterDto registerDto){
        if(customerRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email exist", HttpStatus.BAD_REQUEST);
        }
        
        Customer customer = new Customer();
        customer.setUsername(registerDto.getUsername());
        customer.setEmail(registerDto.getEmail());
        customer.setPhoneNumber(registerDto.getPhoneNumber());
        customer.setSurname(registerDto.getSurname());
        customer.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        customer.setRole("ROLE_USER");
        customerRepository.save(customer);
        log.info("Register new user");
        return  new ResponseEntity<>("User registered", HttpStatus.OK);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logutCustomer(){
        log.info("User is logout");
        return ResponseEntity.ok("Logout");
    }

}
