package org.hartford.ims_springboot.service;

import org.hartford.ims_springboot.dto.CreatePolicyRequest;
import org.hartford.ims_springboot.dto.PolicyDTO;
import org.hartford.ims_springboot.dto.UpdatePremiumRequest;
import org.hartford.ims_springboot.exception.ResourceNotFoundException;
import org.hartford.ims_springboot.model.Customer;
import org.hartford.ims_springboot.model.Policy;
import org.hartford.ims_springboot.repository.CustomerRepository;
import org.hartford.ims_springboot.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyService(PolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    public PolicyDTO createPolicy(CreatePolicyRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        // Business Validation: Start date must be before end date
        if (request.getStartDate().isAfter(request.getEndDate()) || request.getStartDate().isEqual(request.getEndDate())) {
            throw new IllegalArgumentException("Policy start date must be before end date");
        }

        Policy policy = Policy.builder()
                .policyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .type(request.getType())
                .premiumAmount(request.getPremiumAmount())
                .coverageAmount(request.getCoverageAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .customer(customer)
                .build();

        Policy savedPolicy = policyRepository.save(policy);
        return mapToDTO(savedPolicy);
    }

    public List<PolicyDTO> getPoliciesByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return policyRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PolicyDTO updatePremiumAmount(Long policyId, UpdatePremiumRequest request) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + policyId));

        policy.setPremiumAmount(request.getPremiumAmount());
        Policy updatedPolicy = policyRepository.save(policy);
        return mapToDTO(updatedPolicy);
    }

    private PolicyDTO mapToDTO(Policy policy) {
        return PolicyDTO.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .type(policy.getType())
                .premiumAmount(policy.getPremiumAmount())
                .coverageAmount(policy.getCoverageAmount())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .customerId(policy.getCustomer().getId())
                .build();
    }
}
