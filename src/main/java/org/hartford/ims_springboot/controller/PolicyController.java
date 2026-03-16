package org.hartford.ims_springboot.controller;

import jakarta.validation.Valid;
import org.hartford.ims_springboot.dto.CreatePolicyRequest;
import org.hartford.ims_springboot.dto.PolicyDTO;
import org.hartford.ims_springboot.dto.UpdatePremiumRequest;
import org.hartford.ims_springboot.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<PolicyDTO> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        PolicyDTO policyDTO = policyService.createPolicy(request);
        return new ResponseEntity<>(policyDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<PolicyDTO> updatePremiumAmount(@PathVariable Long policyId,
                                                         @Valid @RequestBody UpdatePremiumRequest request) {
        PolicyDTO updatedPolicy = policyService.updatePremiumAmount(policyId, request);
        return ResponseEntity.ok(updatedPolicy);
    }
}
