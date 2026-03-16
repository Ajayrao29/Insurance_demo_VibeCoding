package org.hartford.ims_springboot.service;

import org.hartford.ims_springboot.dto.ClaimDTO;
import org.hartford.ims_springboot.dto.SubmitClaimRequest;
import org.hartford.ims_springboot.exception.ResourceNotFoundException;
import org.hartford.ims_springboot.model.Claim;
import org.hartford.ims_springboot.model.Policy;
import org.hartford.ims_springboot.repository.ClaimRepository;
import org.hartford.ims_springboot.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    public ClaimDTO submitClaim(SubmitClaimRequest request) {
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + request.getPolicyId()));

        // Business Validation: Claim amount cannot exceed policy coverage amount
        if (request.getClaimAmount().compareTo(policy.getCoverageAmount()) > 0) {
            throw new IllegalArgumentException("Claim amount cannot exceed policy coverage amount of " + policy.getCoverageAmount());
        }

        Claim claim = Claim.builder()
                .claimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .description(request.getDescription())
                .claimDate(request.getClaimDate())
                .claimAmount(request.getClaimAmount())
                .claimStatus("Submitted")
                .policy(policy)
                .build();

        Claim savedClaim = claimRepository.save(claim);
        return mapToDTO(savedClaim);
    }

    private ClaimDTO mapToDTO(Claim claim) {
        return ClaimDTO.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .description(claim.getDescription())
                .claimDate(claim.getClaimDate())
                .claimAmount(claim.getClaimAmount())
                .claimStatus(claim.getClaimStatus())
                .policyId(claim.getPolicy().getId())
                .build();
    }
}
