package com.complyai.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.complyai.common.enums.RequestStatus;
import com.complyai.request.service.RequestWorkflowRules;
import org.junit.jupiter.api.Test;

class RequestWorkflowRulesTest {

    private final RequestWorkflowRules workflowRules = new RequestWorkflowRules();

    @Test
    void shouldAllowValidTransition() {
        assertThatCode(() -> workflowRules.validateTransition(RequestStatus.SUBMITTED, RequestStatus.IN_REVIEW))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectInvalidTransition() {
        assertThatThrownBy(() -> workflowRules.validateTransition(RequestStatus.SUBMITTED, RequestStatus.COMPLETED))
                .hasMessageContaining("Invalid request status transition");
    }
}
