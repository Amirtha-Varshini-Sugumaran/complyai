INSERT INTO roles (id, name) VALUES
  (1, 'ROLE_SUPER_ADMIN'),
  (2, 'ROLE_TENANT_ADMIN'),
  (3, 'ROLE_COMPLIANCE_MANAGER'),
  (4, 'ROLE_USER');

INSERT INTO tenants (id, name, slug, status, contact_email, region) VALUES
  (1, 'Northwind Health', 'northwind-health', 'ACTIVE', 'privacy@northwind.example', 'EU'),
  (2, 'BlueHarbor Retail', 'blueharbor-retail', 'ACTIVE', 'privacy@blueharbor.example', 'EU');

INSERT INTO users (id, tenant_id, first_name, last_name, email, password_hash, status) VALUES
  (1, NULL, 'Platform', 'Admin', 'superadmin@complyai.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (2, 1, 'Nora', 'Admin', 'tenantadmin@northwind.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (3, 1, 'Luca', 'Compliance', 'compliance@northwind.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (4, 1, 'Eleni', 'User', 'user@northwind.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (5, 2, 'Bram', 'Admin', 'tenantadmin@blueharbor.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (6, 2, 'Sofia', 'Compliance', 'compliance@blueharbor.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE'),
  (7, 2, 'Marta', 'User', 'user@blueharbor.dev', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5IAWk0u1T1aQq8N0UEhoJaMItZmoe', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) VALUES
  (1, 1),
  (2, 2), (2, 3),
  (3, 3),
  (4, 4),
  (5, 2), (5, 3),
  (6, 3),
  (7, 4);

INSERT INTO data_inventory (tenant_id, title, data_category, data_subject_type, processing_purpose, lawful_basis, storage_location, retention_period_days, sensitivity_flag, source_system, status, justification) VALUES
  (1, 'Patient Intake CRM', 'Contact Data', 'Customer', 'Service delivery and support', 'CONTRACT', 'eu-west-1/rds', 365, false, 'HubSpot', 'ACTIVE', NULL),
  (1, 'Employee HR Folder', 'Employment Data', 'Employee', 'Payroll and HR administration', 'LEGAL_OBLIGATION', 'SharePoint EU', 2555, true, 'BambooHR', 'ACTIVE', 'Sensitive payroll and health leave data used for HR operations'),
  (2, 'Marketing Newsletter List', 'Marketing Preferences', 'Lead', 'Email campaigns and newsletter delivery', 'CONSENT', 'Mailchimp EU', 540, false, 'Mailchimp', 'ACTIVE', NULL),
  (2, 'Customer Returns Log', 'Support Data', 'Customer', 'Refund dispute handling', '', 'Postgres warehouse', 730, true, 'Zendesk', 'UNDER_REVIEW', NULL);

INSERT INTO consent_records (tenant_id, subject_identifier, consent_type, date_granted, source, expiry_date, proof_reference, status) VALUES
  (1, 'cust-1102', 'MARKETING_EMAIL', CURRENT_DATE - 120, 'website_form', CURRENT_DATE + 240, 'consent://northwind/1102', 'ACTIVE'),
  (1, 'cust-1188', 'COOKIE_ANALYTICS', CURRENT_DATE - 400, 'cookie_banner', CURRENT_DATE - 30, NULL, 'EXPIRED'),
  (2, 'lead-780', 'MARKETING_EMAIL', CURRENT_DATE - 90, 'landing_page', CURRENT_DATE + 275, NULL, 'PENDING_RENEWAL');

INSERT INTO data_subject_requests (tenant_id, requester_user_id, requester_email, request_type, submission_date, due_date, assigned_user_id, status, completion_notes) VALUES
  (1, 4, 'user@northwind.dev', 'ACCESS', CURRENT_DATE - 10, CURRENT_DATE + 20, 3, 'IN_REVIEW', NULL),
  (1, NULL, 'external.subject@example.eu', 'DELETION', CURRENT_DATE - 40, CURRENT_DATE - 5, 3, 'APPROVED', NULL),
  (2, 7, 'user@blueharbor.dev', 'CORRECTION', CURRENT_DATE - 5, CURRENT_DATE + 25, 6, 'SUBMITTED', NULL);

INSERT INTO data_subject_request_audit (request_id, from_status, to_status, changed_by, changed_at, note) VALUES
  (1, 'SUBMITTED', 'IN_REVIEW', 3, CURRENT_TIMESTAMP - INTERVAL '7 days', 'Initial triage completed'),
  (2, 'IN_REVIEW', 'APPROVED', 3, CURRENT_TIMESTAMP - INTERVAL '2 days', 'Deletion confirmed with system owners');

INSERT INTO audit_logs (tenant_id, actor_user_id, action, entity_type, entity_id, metadata_summary) VALUES
  (1, 2, 'LOGIN', 'USER', 2, 'Tenant admin login'),
  (1, 3, 'REQUEST_STATUS_CHANGED', 'DATA_SUBJECT_REQUEST', 1, 'Request moved to IN_REVIEW'),
  (2, 6, 'CONSENT_REVIEWED', 'CONSENT_RECORD', 3, 'Consent evidence missing'),
  (NULL, 1, 'TENANT_PROVISIONED', 'TENANT', 2, 'BlueHarbor Retail provisioned');

INSERT INTO ai_analysis_results (tenant_id, target_type, target_id, input_source, risk_level, summary, recommended_actions, flagged_issues_json, model_mode, created_by) VALUES
  (1, 'DATA_SUBJECT_REQUEST', 2, 'request_record', 'HIGH', 'Deletion request is overdue and involves data spread across multiple systems.', 'Prioritize deletion workflow, confirm downstream system coverage, and capture completion evidence.', '["Overdue request","High coordination risk"]', 'mock', 3),
  (2, 'DATA_INVENTORY', 4, 'inventory_record', 'HIGH', 'Sensitive support data lacks lawful basis and justification.', 'Add lawful basis, document sensitivity rationale, and review retention policy.', '["Missing lawful basis","Sensitive data without justification"]', 'mock', 6);

SELECT setval('tenants_id_seq', 2, true);
SELECT setval('users_id_seq', 7, true);
