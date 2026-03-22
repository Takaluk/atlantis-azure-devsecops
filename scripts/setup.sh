#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$SCRIPT_DIR/.env" ]; then
  echo "[ERROR] scripts/.env 파일이 없습니다."
  exit 1
fi

set -a
source "$SCRIPT_DIR/.env"
set +a

echo "== Azure Subscription 설정 =="
az account set --subscription "$ARM_SUBSCRIPTION_ID"

echo "== Resource Group 생성 =="
az group create \
  --name "$RESOURCE_GROUP_BACKEND" \
  --location "$AZURE_LOCATION"

echo "== Storage Account 생성 =="
az storage account create \
  --name "$TFSTATE_STORAGE_ACCOUNT" \
  --resource-group "$RESOURCE_GROUP_BACKEND" \
  --location "$AZURE_LOCATION" \
  --sku Standard_LRS \
  --allow-blob-public-access false

echo "== Blob Container 생성 =="
az storage container create \
  --name "$TFSTATE_CONTAINER_NAME" \
  --account-name "$TFSTATE_STORAGE_ACCOUNT" \
  --auth-mode login

echo "== Storage Blob Data Contributor 권한 부여 =="
az role assignment create \
  --assignee "$ARM_CLIENT_ID" \
  --role "Storage Blob Data Contributor" \
  --scope "/subscriptions/$ARM_SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP_BACKEND/providers/Microsoft.Storage/storageAccounts/$TFSTATE_STORAGE_ACCOUNT"

echo "== Atlantis 서버 배포 =="
az container create \
  --resource-group "$RESOURCE_GROUP_BACKEND" \
  --name "$ATLANTIS_CONTAINER_NAME" \
  --image "$ATLANTIS_IMAGE" \
  --os-type Linux \
  --dns-name-label "$ATLANTIS_DNS_LABEL" \
  --ports 4141 \
  --cpu 1 \
  --memory 1.5 \
  --environment-variables \
    ATLANTIS_GH_USER="$ATLANTIS_GH_USER" \
    ATLANTIS_GH_TOKEN="$ATLANTIS_GH_TOKEN" \
    ATLANTIS_GH_WEBHOOK_SECRET="$ATLANTIS_GH_WEBHOOK_SECRET" \
    ATLANTIS_REPO_ALLOWLIST="$ATLANTIS_REPO_ALLOWLIST" \
    ARM_CLIENT_ID="$ARM_CLIENT_ID" \
    ARM_CLIENT_SECRET="$ARM_CLIENT_SECRET" \
    ARM_SUBSCRIPTION_ID="$ARM_SUBSCRIPTION_ID" \
    ARM_TENANT_ID="$ARM_TENANT_ID"

echo ""
echo "완료되었습니다."