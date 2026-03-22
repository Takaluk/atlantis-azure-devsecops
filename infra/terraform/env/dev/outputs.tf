output "key_vault_name" {
  description = "Key Vault 이름"
  value       = data.azurerm_key_vault.shared.name
}

output "key_vault_id" {
  description = "Key Vault ID"
  value       = data.azurerm_key_vault.shared.id
}

output "postgres_admin_password_secret_name" {
  description = "PostgreSQL 비밀번호 secret 이름"
  value       = var.postgres_admin_password_secret_name
}

output "postgres_fqdn" {
  description = "PostgreSQL 서버 FQDN"
  value       = azurerm_postgresql_flexible_server.postgres.fqdn
}

output "postgres_port" {
  description = "PostgreSQL 포트"
  value       = 5432
}

output "auth_db_name" {
  description = "auth-service DB 이름"
  value       = azurerm_postgresql_flexible_server_database.databases["auth"].name
}

output "news_db_name" {
  description = "news-service DB 이름"
  value       = azurerm_postgresql_flexible_server_database.databases["news"].name
}

output "stock_db_name" {
  description = "stock-service DB 이름"
  value       = azurerm_postgresql_flexible_server_database.databases["stock"].name
}

output "redis_hostname" {
  description = "Redis 호스트명"
  value       = azurerm_redis_cache.auth.hostname
}

output "redis_ssl_port" {
  description = "Redis SSL 포트"
  value       = azurerm_redis_cache.auth.ssl_port
}