package org.hartford.ims_springboot.service;

import org.hartford.ims_springboot.dto.CreatePolicyRequest;
import org.hartford.ims_springboot.dto.PolicyDTO;
import org.hartford.ims_springboot.dto.UpdatePremiumRequest;
import org.hartford.ims_springboot.model.Customer;
import org.hartford.ims_springboot.model.Policy;
import org.hartford.ims_springboot.repository.CustomerRepository;
import org.hartford.ims_springboot.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PolicyService policyService;

    private CreatePolicyRequest policyRequest;
    private Customer customer;
    private Policy policy;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .build();

        policyRequest = CreatePolicyRequest.builder()
                .type("Health")
                .premiumAmount(new BigDecimal("500.00"))
                .coverageAmount(new BigDecimal("10000.00"))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .customerId(1L)
                .build();

        policy = Policy.builder()
                .id(1L)
                .policyNumber("POL-12345")
                .type("Health")
                .premiumAmount(new BigDecimal("500.00"))
                .coverageAmount(new BigDecimal("10000.00"))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .customer(customer)
                .build();
    }

    @Test
    void createPolicy_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        PolicyDTO result = policyService.createPolicy(policyRequest);

        assertNotNull(result);
        assertEquals(policyRequest.getType(), result.getType());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void createPolicy_InvalidDates_ThrowsException() {
        policyRequest.setStartDate(LocalDate.now().plusDays(10));
        policyRequest.setEndDate(LocalDate.now().plusDays(5));
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            policyService.createPolicy(policyRequest);
        });

        assertEquals("Policy start date must be before end date", exception.getMessage());
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    void updatePremiumAmount_Success() {
        UpdatePremiumRequest updateRequest = new UpdatePremiumRequest(new BigDecimal("600.00"));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        PolicyDTO result = policyService.updatePremiumAmount(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getPremiumAmount(), result.getPremiumAmount());
    }
}
