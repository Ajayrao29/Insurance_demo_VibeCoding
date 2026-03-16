package org.hartford.ims_springboot.service;

import org.hartford.ims_springboot.dto.ClaimDTO;
import org.hartford.ims_springboot.dto.SubmitClaimRequest;
import org.hartford.ims_springboot.model.Claim;
import org.hartford.ims_springboot.model.Policy;
import org.hartford.ims_springboot.repository.ClaimRepository;
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
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private ClaimService claimService;

    private SubmitClaimRequest claimRequest;
    private Policy policy;
    private Claim claim;

    @BeforeEach
    void setUp() {
        policy = Policy.builder()
                .id(1L)
                .coverageAmount(new BigDecimal("10000.00"))
                .build();

        claimRequest = SubmitClaimRequest.builder()
                .description("Accident repair")
                .claimDate(LocalDate.now())
                .claimAmount(new BigDecimal("2000.00"))
                .policyId(1L)
                .build();

        claim = Claim.builder()
                .id(1L)
                .claimNumber("CLM-123")
                .description("Accident repair")
                .claimDate(LocalDate.now())
                .claimAmount(new BigDecimal("2000.00"))
                .claimStatus("Submitted")
                .policy(policy)
                .build();
    }

    @Test
    void submitClaim_Success() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        ClaimDTO result = claimService.submitClaim(claimRequest);

        assertNotNull(result);
        assertEquals(claimRequest.getClaimAmount(), result.getClaimAmount());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void submitClaim_AmountExceedsCoverage_ThrowsException() {
        claimRequest.setClaimAmount(new BigDecimal("15000.00"));
        
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            claimService.submitClaim(claimRequest);
        });

        assertTrue(exception.getMessage().contains("Claim amount cannot exceed policy coverage amount"));
        verify(claimRepository, never()).save(any(Claim.class));
    }
}
