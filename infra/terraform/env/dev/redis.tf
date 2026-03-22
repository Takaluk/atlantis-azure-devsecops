resource "azurerm_redis_cache" "auth" {
  # checkov:skip=CKV_AZURE_31: dev에서는 firewall rule 미적용
  # checkov:skip=CKV_AZURE_89: dev 세션 저장용이라 persistence 미적용
  # checkov:skip=CKV_AZURE_226: dev에서는 zone redundancy 미적용

  name                = var.redis_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  capacity            = var.redis_capacity
  family              = var.redis_family
  sku_name            = var.redis_sku_name

  minimum_tls_version           = "1.2"
  public_network_access_enabled = true
}
