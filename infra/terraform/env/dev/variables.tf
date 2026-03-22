variable "postgres_server_name" {
  description = "PostgreSQL Flexible Server 이름"
  type        = string
  default     = "psql-stocklens-dev-yslee0419"
}

variable "postgres_admin_username" {
  description = "PostgreSQL 관리자 계정"
  type        = string
  default     = "stocklensadmin"
}

variable "postgres_admin_password" {
  description = "PostgreSQL 관리자 비밀번호"
  type        = string
  sensitive   = true
}

variable "postgres_sku_name" {
  description = "PostgreSQL SKU"
  type        = string
  default     = "B_Standard_B1ms"
}

variable "postgres_storage_mb" {
  description = "PostgreSQL 저장소 크기(MB)"
  type        = number
  default     = 32768
}

variable "postgres_version" {
  description = "PostgreSQL 버전"
  type        = string
  default     = "16"
}

variable "postgres_backup_retention_days" {
  description = "PostgreSQL 백업 보관일"
  type        = number
  default     = 7
}

variable "redis_name" {
  description = "Redis 이름"
  type        = string
  default     = "redis-stocklens-dev"
}

variable "redis_sku_name" {
  description = "Redis SKU"
  type        = string
  default     = "Basic"
}

variable "redis_family" {
  description = "Redis 패밀리"
  type        = string
  default     = "C"
}

variable "redis_capacity" {
  description = "Redis 용량"
  type        = number
  default     = 0
}
