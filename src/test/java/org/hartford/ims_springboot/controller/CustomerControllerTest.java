package org.hartford.ims_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.ims_springboot.dto.CustomerDTO;
import org.hartford.ims_springboot.dto.CustomerRegistrationRequest;
import org.hartford.ims_springboot.service.CustomerService;
import org.hartford.ims_springboot.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private PolicyService policyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerRegistrationRequest registrationRequest;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        registrationRequest = CustomerRegistrationRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

        customerDTO = CustomerDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();
    }

    @Test
    void registerCustomer_Success() throws Exception {
        when(customerService.registerCustomer(any(CustomerRegistrationRequest.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void registerCustomer_ValidationFailure_InvalidEmail() throws Exception {
        registrationRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCustomer_ValidationFailure_ShortPhone() throws Exception {
        registrationRequest.setPhoneNumber("123");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }
}
