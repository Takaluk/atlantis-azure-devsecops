# 모니터링을 위한 Log Analytics Workspace (ACA 필수 요구사항)
resource "azurerm_log_analytics_workspace" "law" {
  name                = "law-stocklens-dev"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# 컨테이너 앱 환경
resource "azurerm_container_app_environment" "cae" {
  name                       = "cae-stocklens-dev"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.law.id
}

# 관리형 ID 발급
resource "azurerm_user_assigned_identity" "aca_identity" {
  name                = "mi-aca-stocklens"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
}

# RBAC - ID에 ACR 이미지 pull 권한 부여
resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.aca_identity.principal_id
}

# frontend container app 배포
resource "azurerm_container_app" "frontend" {
  name                         = "ca-frontend"
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  # 관리형 ID 연결
  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.aca_identity.id]
  }

  # ACR 연결
  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.aca_identity.id
  }

  # 외부 인터넷 접속 허용 및 설정
  ingress {
    allow_insecure_connections = false
    external_enabled           = true # 외부 접속 오픈
    target_port                = 8080
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  template {
    container {
      name   = "frontend"
      image  = "${azurerm_container_registry.acr.login_server}/stocklens-frontend:v1.0.0"
      cpu    = 0.5
      memory = "1Gi"
    }
  }

  # 권한 부여가 끝난 후 앱을 배포하도록 설정
  depends_on = [azurerm_role_assignment.acr_pull]
}