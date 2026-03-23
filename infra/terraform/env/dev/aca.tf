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

locals {
  stock_service_image = "${azurerm_container_registry.acr.login_server}/stocklens-stock-service:v1.0.0"
  news_service_image  = "${azurerm_container_registry.acr.login_server}/stocklens-news-service:v1.0.0"
  auth_service_image  = "${azurerm_container_registry.acr.login_server}/stocklens-auth-service:v1.0.0"
  frontend_image      = "${azurerm_container_registry.acr.login_server}/stocklens-frontend:v1.0.0"
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
      image  = local.frontend_image
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "STOCK_SERVICE_URL"
        value = "https://${azurerm_container_app.stock_service.ingress[0].fqdn}"
      }

      env {
        name  = "NEWS_SERVICE_URL"
        value = "https://${azurerm_container_app.news_service.ingress[0].fqdn}"
      }

      env {
        name  = "AUTH_SERVICE_URL"
        value = "https://${azurerm_container_app.auth_service.ingress[0].fqdn}"
      }
    }
  }

  # 권한 부여가 끝난 후 앱을 배포하도록 설정
  depends_on = [
    azurerm_role_assignment.acr_pull,
    azurerm_container_app.auth_service,
    azurerm_container_app.news_service,
    azurerm_container_app.stock_service
  ]
}

resource "azurerm_container_app" "news_service" {
  name                         = "ca-news-service"
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.aca_identity.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.aca_identity.id
  }

  ingress {
    allow_insecure_connections = false
    external_enabled           = false
    target_port                = 8082
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  secret {
    name  = "postgres-admin-password"
    value = data.azurerm_key_vault_secret.postgres_admin_password.value
  }

  secret {
    name  = "alphavantage-api-key"
    value = data.azurerm_key_vault_secret.alphavantage_api_key.value
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "news-service"
      image  = local.news_service_image
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "NEWS_DB_HOST"
        value = azurerm_postgresql_flexible_server.postgres.fqdn
      }

      env {
        name  = "NEWS_DB_PORT"
        value = "5432"
      }

      env {
        name  = "NEWS_DB_NAME"
        value = azurerm_postgresql_flexible_server_database.databases["news"].name
      }

      env {
        name  = "NEWS_DB_USERNAME"
        value = var.postgres_admin_username
      }

      env {
        name        = "NEWS_DB_PASSWORD"
        secret_name = "postgres-admin-password"
      }
    }
  }

  depends_on = [azurerm_role_assignment.acr_pull]
}

resource "azurerm_container_app" "auth_service" {
  name                         = "ca-auth-service"
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.aca_identity.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.aca_identity.id
  }

  ingress {
    allow_insecure_connections = false
    external_enabled           = false
    target_port                = 8083
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  secret {
    name  = "postgres-admin-password"
    value = data.azurerm_key_vault_secret.postgres_admin_password.value
  }

  secret {
    name  = "redis-access-key"
    value = azurerm_redis_cache.auth.primary_access_key
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "auth-service"
      image  = local.auth_service_image
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "AUTH_DB_HOST"
        value = azurerm_postgresql_flexible_server.postgres.fqdn
      }

      env {
        name  = "AUTH_DB_PORT"
        value = "5432"
      }

      env {
        name  = "AUTH_DB_NAME"
        value = azurerm_postgresql_flexible_server_database.databases["auth"].name
      }

      env {
        name  = "AUTH_DB_USERNAME"
        value = var.postgres_admin_username
      }

      env {
        name        = "AUTH_DB_PASSWORD"
        secret_name = "postgres-admin-password"
      }

      env {
        name  = "AUTH_REDIS_HOST"
        value = azurerm_redis_cache.auth.hostname
      }

      env {
        name  = "AUTH_REDIS_PORT"
        value = tostring(azurerm_redis_cache.auth.ssl_port)
      }

      env {
        name        = "AUTH_REDIS_PASSWORD"
        secret_name = "redis-access-key"
      }

      env {
        name  = "AUTH_REDIS_SSL_ENABLED"
        value = "true"
      }
    }
  }

  depends_on = [azurerm_role_assignment.acr_pull]
}

resource "azurerm_container_app" "stock_service" {
  name                         = "ca-stock-service"
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.aca_identity.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.aca_identity.id
  }

  ingress {
    allow_insecure_connections = false
    external_enabled           = false
    target_port                = 8081
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  secret {
    name  = "postgres-admin-password"
    value = data.azurerm_key_vault_secret.postgres_admin_password.value
  }

  secret {
    name  = "alphavantage-api-key"
    value = data.azurerm_key_vault_secret.alphavantage_api_key.value
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "stock-service"
      image  = local.stock_service_image
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "STOCK_DB_HOST"
        value = azurerm_postgresql_flexible_server.postgres.fqdn
      }

      env {
        name  = "STOCK_DB_PORT"
        value = "5432"
      }

      env {
        name  = "STOCK_DB_NAME"
        value = azurerm_postgresql_flexible_server_database.databases["stock"].name
      }

      env {
        name  = "STOCK_DB_USERNAME"
        value = var.postgres_admin_username
      }

      env {
        name        = "STOCK_DB_PASSWORD"
        secret_name = "postgres-admin-password"
      }

      env {
        name  = "NEWS_SERVICE_URL"
        value = "https://${azurerm_container_app.news_service.ingress[0].fqdn}"
      }

      env {
        name        = "ALPHAVANTAGE_API_KEY"
        secret_name = "alphavantage-api-key"
      }
    }
  }

  depends_on = [
    azurerm_role_assignment.acr_pull,
    azurerm_container_app.news_service
  ]
}
