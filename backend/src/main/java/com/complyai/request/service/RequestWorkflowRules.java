package com.complyai.request.service;

import com.complyai.common.enums.RequestStatus;
import com.complyai.common.exception.BusinessRuleViolationException;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RequestWorkflowRules {

    private static final Map<RequestStatus, Set<RequestStatus>> ALLOWED = Map.of(
            RequestStatus.SUBMITTED, Set.of(RequestStatus.IN_REVIEW),
            RequestStatus.IN_REVIEW, Set.of(RequestStatus.APPROVED, RequestStatus.REJECTED),
            RequestStatus.APPROVED, Set.of(RequestStatus.COMPLETED)
    );

    public void validateTransition(RequestStatus from, RequestStatus to) {
        if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) {
            throw new BusinessRuleViolationException("Invalid request status transition");
        }
    }
}
