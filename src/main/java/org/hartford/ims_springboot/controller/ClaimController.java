package org.hartford.ims_springboot.controller;

import jakarta.validation.Valid;
import org.hartford.ims_springboot.dto.ClaimDTO;
import org.hartford.ims_springboot.dto.SubmitClaimRequest;
import org.hartford.ims_springboot.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> submitClaim(@Valid @RequestBody SubmitClaimRequest request) {
        ClaimDTO claimDTO = claimService.submitClaim(request);
        return new ResponseEntity<>(claimDTO, HttpStatus.CREATED);
    }
}
