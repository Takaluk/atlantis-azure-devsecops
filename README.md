# atlantis-azure-devsecops
- 로컬 Docker 기반 MSA 서비스를 Azure로 마이그레이션
- Git Flow 기반으로 Checkov, Atlantis, Prowler 연계
- 인프라 보안 검증과 배포 자동화 구현

[Service Link](https://ca-frontend.whitemeadow-4d313429.koreacentral.azurecontainerapps.io/)

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

## Azure, Atlantis Setup

```bash
./scripts/setup.sh
```
