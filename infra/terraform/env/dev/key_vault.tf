data "azurerm_key_vault" "shared" {
  name                = var.key_vault_name
  resource_group_name = var.key_vault_resource_group_name
}

data "azurerm_key_vault_secret" "postgres_admin_password" {
  name         = var.postgres_admin_password_secret_name
  key_vault_id = data.azurerm_key_vault.shared.id
}
