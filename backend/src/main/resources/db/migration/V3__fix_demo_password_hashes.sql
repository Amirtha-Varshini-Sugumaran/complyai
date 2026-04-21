UPDATE users
SET password_hash = '$2a$10$2nc5D5ZF38ipu2EOZUPE1e2lgeymaBPlv5TV6aVq5g01wn9DIiyKG'
WHERE email IN (
  'superadmin@complyai.dev',
  'tenantadmin@northwind.dev',
  'compliance@northwind.dev',
  'user@northwind.dev',
  'tenantadmin@blueharbor.dev',
  'compliance@blueharbor.dev',
  'user@blueharbor.dev'
);
