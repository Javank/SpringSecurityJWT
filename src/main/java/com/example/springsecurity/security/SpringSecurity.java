package com.example.springsecurity.security;


import com.example.springsecurity.models.Customer;
import com.example.springsecurity.repositories.CustomerRepository;
import com.example.springsecurity.security.jwt.JwtTokenFilter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SpringSecurity  {

    private CustomerRepository customerRepository;
    private JwtTokenFilter jwtTokenFilter;

    public SpringSecurity(CustomerRepository customerRepository, JwtTokenFilter jwtTokenFilter) {
        this.customerRepository = customerRepository;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void saveCostumer(){
        Customer customer = new Customer(1l, "test@test.pl", passwordEncoder().encode("test"), passwordEncoder().encode("test"), "test", "2345", "ROLE_ADMIN", true);
        customerRepository.save(customer);

    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User email not found"));
    }

     @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws  Exception {
       return  authenticationConfiguration.getAuthenticationManager();
     }

     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
         http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
         http.authorizeRequests()
                 .antMatchers("/login").permitAll()
                 .antMatchers("/test").hasRole("ADMIN");
         http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

         return http.build();
     }
}