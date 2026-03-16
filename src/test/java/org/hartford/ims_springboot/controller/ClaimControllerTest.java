package org.hartford.ims_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hartford.ims_springboot.dto.ClaimDTO;
import org.hartford.ims_springboot.dto.SubmitClaimRequest;
import org.hartford.ims_springboot.service.ClaimService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaimController.class)
public class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @Autowired
    private ObjectMapper objectMapper;

    private SubmitClaimRequest claimRequest;
    private ClaimDTO claimDTO;

    @BeforeEach
    void setUp() {
        claimRequest = SubmitClaimRequest.builder()
                .description("Hospitalization")
                .claimDate(LocalDate.now())
                .claimAmount(new BigDecimal("1500.00"))
                .policyId(1L)
                .build();

        claimDTO = ClaimDTO.builder()
                .id(1L)
                .claimNumber("CLM-789")
                .description("Hospitalization")
                .claimDate(LocalDate.now())
                .claimAmount(new BigDecimal("1500.00"))
                .claimStatus("Submitted")
                .policyId(1L)
                .build();
    }

    @Test
    void submitClaim_Success() throws Exception {
        when(claimService.submitClaim(any(SubmitClaimRequest.class))).thenReturn(claimDTO);

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimNumber").value("CLM-789"))
                .andExpect(jsonPath("$.claimAmount").value(1500.00));
    }

    @Test
    void submitClaim_ValidationFailure_MissingDescription() throws Exception {
        claimRequest.setDescription("");

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitClaim_ValidationFailure_ZeroAmount() throws Exception {
        claimRequest.setClaimAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimRequest)))
                .andExpect(status().isBadRequest());
    }
}
