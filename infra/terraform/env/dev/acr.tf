resource "azurerm_container_registry" "acr" {
  # checkov:skip=CKV_AZURE_139: 운영 전환 시 비공개 네트워크로 구성 예정
  # checkov:skip=CKV_AZURE_165: 단일 리전 개발 환경에서는 제외하고, 운영 확장 시 다중 리전 복제 적용 예정
  # checkov:skip=CKV_AZURE_166: 추후 CI/CD 보안 통제에 포함 예정
  # checkov:skip=CKV_AZURE_164: 추후 이미지 신뢰성 검증 체계로 보완 예정
  # checkov:skip=CKV_AZURE_167: 운영 고도화 시 이미지 정리 정책 적용 예정
  # checkov:skip=CKV_AZURE_233: 운영 환경 설계 시 가용성 기준에 따라 재검토 예정
  # checkov:skip=CKV_AZURE_237: 추후 네트워크 보안 고도화 시 적용 예정

  name                = "acrstocklensyslee0419"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Standard"

  # 관리자 계정 비활성화
  admin_enabled = false
}