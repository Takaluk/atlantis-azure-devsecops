# 리소스 그룹
resource "azurerm_resource_group" "rg" {
  name     = "rg-stocklens-msa"
  location = "koreacentral"
}

# VNet
resource "azurerm_virtual_network" "vnet" {
  name                = "vnet-stocklens"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
}

# 서브넷 
resource "azurerm_subnet" "msa_subnet" {
  name                 = "snet-msa"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

# 네트워크 보안 그룹 NSG
resource "azurerm_network_security_group" "nsg" {
  name                = "nsg-stocklens-default"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name

  # 기본적으로 모든 인바운드 트래픽을 차단
  security_rule {
    name                       = "DenyAllInbound"
    priority                   = 4096
    direction                  = "Inbound"
    access                     = "Deny"
    protocol                   = "*"
    source_port_range          = "*"
    destination_port_range     = "*"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}

# 서브넷과 NSG 연결
resource "azurerm_subnet_network_security_group_association" "nsg_assoc" {
  subnet_id                 = azurerm_subnet.msa_subnet.id
  network_security_group_id = azurerm_network_security_group.nsg.id
}