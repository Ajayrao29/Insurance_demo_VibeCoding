package org.hartford.ims_springboot.service;

import org.hartford.ims_springboot.dto.CustomerDTO;
import org.hartford.ims_springboot.dto.CustomerRegistrationRequest;
import org.hartford.ims_springboot.model.Customer;
import org.hartford.ims_springboot.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRegistrationRequest registrationRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        registrationRequest = CustomerRegistrationRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();
    }

    @Test
    void registerCustomer_Success() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.registerCustomer(registrationRequest);

        assertNotNull(result);
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void registerCustomer_EmailAlreadyExists_ThrowsException() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(registrationRequest);
        });

        assertEquals("Customer with email john.doe@example.com already exists", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void registerCustomer_UnderAge_ThrowsException() {
        registrationRequest.setDateOfBirth(LocalDate.now().minusYears(17));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(registrationRequest);
        });

        assertEquals("Customer must be at least 18 years old", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
