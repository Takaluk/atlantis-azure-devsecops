locals {
  postgres_databases = {
    auth  = "authdb"
    news  = "newsdb"
    stock = "stockdb"
  }
}

resource "azurerm_postgresql_flexible_server" "postgres" {
  # checkov:skip=CKV2_AZURE_28: dev에서는 public access를 임시 사용
  # checkov:skip=CKV2_AZURE_57: dev에서는 private endpoint 미구성
  # checkov:skip=CKV_AZURE_136: dev에서는 geo backup 미적용
  # checkov:skip=CKV_AZURE_212: dev에서는 CMK 미적용

  name                   = var.postgres_server_name
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = var.postgres_version
  delegated_subnet_id    = null
  administrator_login    = var.postgres_admin_username
  administrator_password = data.azurerm_key_vault_secret.postgres_admin_password.value
  zone                   = "1"
  storage_mb             = var.postgres_storage_mb
  sku_name               = var.postgres_sku_name
  backup_retention_days  = var.postgres_backup_retention_days

  public_network_access_enabled = true
  geo_redundant_backup_enabled  = false
}

resource "azurerm_postgresql_flexible_server_database" "databases" {
  for_each = local.postgres_databases

  name      = each.value
  server_id = azurerm_postgresql_flexible_server.postgres.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
