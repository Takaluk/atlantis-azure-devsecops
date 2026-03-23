# atlantis-azure-devsecops

## Repository Structure

```
atlantis-azure-devsecops/
├── .github/
│   └── workflows/               # GitHub Actions 워크플로우
│       ├── pr-checks.yml        # develop PR 시 terraform fmt/validate와 Checkov
│       └── prowler-scan.yml     # main 변경 시 또는 매일 자정 Prowler
├── infra/
│   └── terraform/
│       └── env/
│           └── dev/             # Azure dev 환경, backend 구성
├── scripts/
│   ├── setup.sh                 # Azure 초기 설정 및 Atlantis 컨테이너 배포
│   └── .env.example             # 환경변수 예시
├── docker/                      # 로컬 MSA 실행용 Docker Compose 및 Dockerfile
│   └── .env.example             # 환경변수 예시
├── auth-service/                # 인증 서비스
├── news-service/                # 뉴스 서비스
├── stock-service/               # 주식 서비스 (Alpha Vantage API 호출)
└── frontend/                    # 프론트엔드
```

## 파이프라인 흐름

`develop PR` → `Checkov` → `Atlantis` → `main merge` → `Prowler`

- **Checkov**: Terraform 보안 검증
- **Atlantis**: Terraform plan/apply 자동화
- **Prowler**: Azure 보안 점검

## Run Locally

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

## Azure Setup

```bash
./scripts/setup.sh
```

## Azure Migration Status

- PostgreSQL Flexible Server와 Redis는 `infra/terraform/env/dev` 기준으로 dev 환경에 배포되도록 정의되어 있습니다.
- Container Apps는 `frontend`, `news-service`, `stock-service`, `auth-service`까지 배포되도록 구성되어 있습니다.
- `frontend`만 외부 공개되고, 백엔드 서비스는 Container Apps 환경 내부 FQDN으로만 통신합니다.
- `auth-service`는 Azure Redis 연결을 위해 TLS(`AUTH_REDIS_SSL_ENABLED=true`)와 access key를 사용합니다.
