package org.hartford.ims_springboot.controller;

import jakarta.validation.Valid;
import org.hartford.ims_springboot.dto.CustomerDTO;
import org.hartford.ims_springboot.dto.CustomerRegistrationRequest;
import org.hartford.ims_springboot.dto.PolicyDTO;
import org.hartford.ims_springboot.service.CustomerService;
import org.hartford.ims_springboot.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final PolicyService policyService;

    public CustomerController(CustomerService customerService, PolicyService policyService) {
        this.customerService = customerService;
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerDTO customerDTO = customerService.registerCustomer(request);
        return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/policies")
    public ResponseEntity<List<PolicyDTO>> getCustomerPolicies(@PathVariable Long customerId) {
        List<PolicyDTO> policies = policyService.getPoliciesByCustomerId(customerId);
        return ResponseEntity.ok(policies);
    }
}
