package org.hartford.ims_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.ims_springboot.dto.CreatePolicyRequest;
import org.hartford.ims_springboot.dto.PolicyDTO;
import org.hartford.ims_springboot.dto.UpdatePremiumRequest;
import org.hartford.ims_springboot.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
public class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolicyService policyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreatePolicyRequest policyRequest;
    private PolicyDTO policyDTO;

    @BeforeEach
    void setUp() {
        policyRequest = CreatePolicyRequest.builder()
                .type("Health")
                .premiumAmount(new BigDecimal("500.00"))
                .coverageAmount(new BigDecimal("10000.00"))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .customerId(1L)
                .build();

        policyDTO = PolicyDTO.builder()
                .id(1L)
                .policyNumber("POL-123")
                .type("Health")
                .premiumAmount(new BigDecimal("500.00"))
                .coverageAmount(new BigDecimal("10000.00"))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusYears(1))
                .customerId(1L)
                .build();
    }

    @Test
    void createPolicy_Success() throws Exception {
        when(policyService.createPolicy(any(CreatePolicyRequest.class))).thenReturn(policyDTO);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber").value("POL-123"));
    }

    @Test
    void createPolicy_ValidationFailure_ZeroPremium() throws Exception {
        policyRequest.setPremiumAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePremiumAmount_Success() throws Exception {
        UpdatePremiumRequest updateRequest = new UpdatePremiumRequest(new BigDecimal("600.00"));
        policyDTO.setPremiumAmount(new BigDecimal("600.00"));
        
        when(policyService.updatePremiumAmount(eq(1L), any(UpdatePremiumRequest.class))).thenReturn(policyDTO);

        mockMvc.perform(put("/api/policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.premiumAmount").value(600.00));
    }
}
