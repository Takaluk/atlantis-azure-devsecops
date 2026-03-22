output "key_vault_name" {
  description = "Key Vault 이름"
  value       = azurerm_key_vault.shared.name
}

output "key_vault_id" {
  description = "Key Vault ID"
  value       = azurerm_key_vault.shared.id
}

output "postgres_admin_password_secret_name" {
  description = "PostgreSQL 비밀번호 secret 이름"
  value       = azurerm_key_vault_secret.postgres_admin_password.name
}
