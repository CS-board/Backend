# Monitoring

This project can run Spring Boot, MySQL, Redis, Prometheus, and Grafana through Docker Compose.

## Run

Default local Docker stack:

```powershell
docker compose up -d --build
```

Development stack:

```powershell
docker compose -f docker-compose.dev.yml up -d --build
```

Production stack:

```powershell
docker compose -f docker-compose.prod.yml up -d --build
```

## URLs

- Spring health: http://localhost:8080/actuator/health
- Spring Prometheus metrics: http://localhost:8080/actuator/prometheus
- Prometheus UI: http://localhost:9090
- Grafana UI: http://localhost:3000

## Prometheus

Prometheus reads `monitoring/prometheus/prometheus.yml` and scrapes the Spring Boot application at:

```text
app:8080/actuator/prometheus
```

In the Prometheus UI, open `Status > Targets` and verify that the `spring-boot-app` target is `UP`.

## Grafana

Open Grafana at http://localhost:3000.

Default credentials:

```text
admin / admin
```

Add Prometheus as a data source:

```text
Connections > Data sources > Add data source > Prometheus
URL: http://prometheus:9090
Save & test
```

If Grafana asks you to change the default password on first login, set a new password before adding the data source.

## Future Exporters

Redis and MySQL exporter placeholders are left as comments in the Compose files and Prometheus config. Add them later when service-level Redis/MySQL metrics are needed.
