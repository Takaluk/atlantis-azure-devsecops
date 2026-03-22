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
