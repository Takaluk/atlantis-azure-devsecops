resource "random_password" "postgres_admin" {
  length           = 24
  special          = true
  override_special = "!@#$%&*()-_=+[]{}<>:?"
}

resource "azurerm_key_vault" "shared" {
  # checkov:skip=CKV_AZURE_109: dev에서는 public access를 임시 사용

  name                          = var.key_vault_name
  location                      = azurerm_resource_group.rg.location
  resource_group_name           = azurerm_resource_group.rg.name
  tenant_id                     = data.azurerm_client_config.current.tenant_id
  sku_name                      = var.key_vault_sku_name
  soft_delete_retention_days    = 7
  purge_protection_enabled      = true
  public_network_access_enabled = true
}

resource "azurerm_key_vault_secret" "postgres_admin_password" {
  name         = var.postgres_admin_password_secret_name
  value        = random_password.postgres_admin.result
  key_vault_id = azurerm_key_vault.shared.id
}

data "azurerm_client_config" "current" {}
