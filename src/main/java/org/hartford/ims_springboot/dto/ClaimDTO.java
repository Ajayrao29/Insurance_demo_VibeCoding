package org.hartford.ims_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDTO {
    private Long id;
    private String claimNumber;
    private String description;
    private LocalDate claimDate;
    private BigDecimal claimAmount;
    private String claimStatus;
    private Long policyId;
}
