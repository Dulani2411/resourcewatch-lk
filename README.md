# ResourceWatch LK 🌧⚡💧

> Community-powered water shortage & power-cut monitoring platform for Sri Lanka — weather forecasting, risk index, outage reporting, and AI recommendations.

![Status](https://img.shields.io/badge/status-in%20development-yellow)
![Phase](https://img.shields.io/badge/phase-1%20foundation-blue)
![Stack](https://img.shields.io/badge/stack-React%20%7C%20Spring%20Boot%20%7C%20PostgreSQL-green)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## What is ResourceWatch LK?

Sri Lanka regularly faces water shortages and power cuts — especially during dry seasons. ResourceWatch LK gives communities a single platform to:

- **See** real-time weather conditions and how they affect resource availability
- **Track** water shortage and power-cut risk levels before they happen
- **Report** outages in their area so others know what's happening on the ground
- **Get AI advice** on how to prepare and reduce usage during high-risk periods

No water board data needed. No official power schedules needed. The platform generates its own risk intelligence from weather data + community reports.

---

## Features

| Feature | Description |
|---|---|
| 🌤 Weather Forecasting | Live conditions + 7-day forecast from OpenWeather API, fetched every 3 hours |
| 💧 Water Risk Index | Scores 0–100 based on rainfall, temperature, dry days, and community reports |
| ⚡ Grid Stress Prediction | Estimates power system pressure from weather patterns and seasonal demand |
| 📍 Community Reporting | Users submit outages by district; reports feed the risk index in real time |
| 🤖 AI Recommendations | Local Llama 3 model gives personalised advice based on current conditions |
| 🗺 District Heatmap | Visual overview of HIGH / MEDIUM / LOW risk across all Sri Lanka districts |

---

## Tech Stack

### Application
| Layer | Technology |
|---|---|
| Frontend | React 18 + Vite |
| Backend | Spring Boot 3.3 (Java 21) |
| Database | PostgreSQL 16 |
| AI | Ollama + Llama 3 (runs locally) |
| Weather | OpenWeather API |

### DevOps & Infrastructure
| Tool | Purpose |
|---|---|
| Docker + Docker Compose | Local development stack |
| GitHub Actions | CI/CD pipeline |
| AWS ECR | Container image registry |
| AWS EKS | Kubernetes cluster (production) |
| Terraform | Infrastructure as code |
| Prometheus + Grafana | Monitoring and dashboards |

---

## Project Structure

```
resourcewatch-lk/
├── frontend/                  # React + Vite app
│   ├── src/
│   │   ├── components/        # Reusable UI components
│   │   ├── pages/             # Overview, Water, Power, Report, AI
│   │   └── services/          # API calls to Spring Boot
│   ├── Dockerfile
│   └── vite.config.js
│
├── backend/                   # Spring Boot REST API
│   ├── src/main/java/lk/resourcewatch/
│   │   ├── controller/        # REST endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # JPA database access
│   │   ├── model/             # Entity classes
│   │   └── scheduler/         # Weather fetch cron job
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/init.sql        # Database schema + seed data
│   └── Dockerfile
│
├── infra/
│   ├── k8s/                   # Kubernetes manifests
│   └── terraform/             # AWS infrastructure
│
├── docs/                      # Setup guides and documentation
├── .env.example               # Environment variable template
├── docker-compose.yml         # Local dev — one command to run everything
└── ROADMAP.md                 # Full 12-week development plan
```

---

## Quick Start

### Prerequisites

Make sure you have these installed:

- [Git](https://git-scm.com)
- [Node.js 20+](https://nodejs.org)
- [Java JDK 21](https://adoptium.net)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/resourcewatch-lk.git
cd resourcewatch-lk
```

### 2. Set up environment variables

```bash
cp .env.example .env
```

Open `.env` and add your OpenWeather API key (free at [openweathermap.org](https://openweathermap.org/api)):

```env
OPENWEATHER_API_KEY=your_key_here
```

### 3. Start the full stack

```bash
docker-compose up --build
```

This starts all four services automatically:

| Service | URL | Description |
|---|---|---|
| Frontend | http://localhost:5173 | React dashboard |
| Backend API | http://localhost:8080/api | Spring Boot REST API |
| Health check | http://localhost:8080/actuator/health | Should return `{"status":"UP"}` |
| Ollama | http://localhost:11434 | Local AI service |

### 4. Pull the AI model (one time only, ~4.7GB)

```bash
docker exec rw_ollama ollama pull llama3
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/weather/latest` | Current weather conditions |
| GET | `/api/weather/forecast` | 7-day forecast |
| GET | `/api/water/risk` | Water risk index + score |
| GET | `/api/power/stress` | Grid stress level |
| GET | `/api/reports` | All community reports |
| POST | `/api/reports` | Submit a new outage report |
| GET | `/api/districts/risk-summary` | Risk level by district (for heatmap) |
| POST | `/api/ai/ask` | Ask the AI advisor a question |

---

## Water Risk Index

The risk score is calculated without any water board data:

```
Score = 0

Rainfall last 24h < 10mm   → +30 points
Temperature > 34°C         → +20 points
Consecutive dry days > 10  → +25 points
Community reports > 20     → +15 points
Humidity < 50%             → +10 points

0–30   = LOW    🟢
31–60  = MEDIUM 🟡
61–100 = HIGH   🔴
```

---

## Development Commands

```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# View logs for a service
docker-compose logs -f backend

# Stop everything
docker-compose down

# Stop and delete database (fresh start)
docker-compose down -v

# Rebuild a single service
docker-compose up --build backend

# Open PostgreSQL shell
docker exec -it rw_postgres psql -U rw_user -d resourcewatch
```

---

## Roadmap

The project is built across 6 phases over 12 weeks:

| Phase | Focus | Status |
|---|---|---|
| 1 | Foundation — project setup, Docker, OpenWeather API | 🔄 In progress |
| 2 | Core features — risk engine, community reports, heatmap | ⏳ Planned |
| 3 | AI Advisor — Ollama + Llama 3 integration | ⏳ Planned |
| 4 | CI/CD — GitHub Actions, Docker builds, AWS ECR | ⏳ Planned |
| 5 | Kubernetes + AWS — EKS deploy, Terraform infra | ⏳ Planned |
| 6 | Monitoring — Prometheus metrics, Grafana dashboards | ⏳ Planned |

See [ROADMAP.md](./ROADMAP.md) for the full breakdown with tasks and deliverables.

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `OPENWEATHER_API_KEY` | Free API key from openweathermap.org | `a1b2c3d4...` |
| `DB_NAME` | PostgreSQL database name | `resourcewatch` |
| `DB_USERNAME` | Database user | `rw_user` |
| `DB_PASSWORD` | Database password | `change_me` |
| `OLLAMA_MODEL` | AI model to use | `llama3` |
| `VITE_API_BASE_URL` | Backend URL for frontend | `http://localhost:8080/api` |

See [.env.example](./.env.example) for the full list.

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/your-feature-name`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to the branch: `git push origin feat/your-feature-name`
5. Open a Pull Request

---

## License

MIT License — see [LICENSE](./LICENSE) for details.

---

*Built for Sri Lanka 🇱🇰 — by the community, for the community.*
