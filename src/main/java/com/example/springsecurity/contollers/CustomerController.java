package com.example.springsecurity.contollers;


import com.example.springsecurity.models.Customer;
import com.example.springsecurity.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/allCustomer")
    Iterable<Customer> findAll(){
        return  customerRepository.findAll();
    }

}
