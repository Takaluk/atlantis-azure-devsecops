variable "key_vault_name" {
  description = "Key Vault 이름"
  type        = string
  default     = "kv-stocklens-dev-yslee0419"
}

variable "key_vault_sku_name" {
  description = "Key Vault SKU"
  type        = string
  default     = "standard"
}

variable "postgres_admin_password_secret_name" {
  description = "PostgreSQL 비밀번호 secret 이름"
  type        = string
  default     = "postgres-admin-password"
}
