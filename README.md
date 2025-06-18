# horizon_backend

This project uses Spring Boot. Docker compose is provided for local PostgreSQL and Redis instances.

## Running services

Start the supporting services with docker-compose:

```bash
docker-compose up postgres redis
```

Redis is used for application caching. The application assumes a Redis instance
is available at `localhost:6379`. When using a hosted Redis service, configure
the connection with the `REDIS_HOST`, `REDIS_PORT`, and `REDIS_PASSWORD`
environment variables.

Certain analytics endpoints are cached to improve performance. Both the
overview statistics (returning `List<OverviewStatistic>`) and chart data
(daily count methods) are cached. Cached results expire after 10 minutes by
default as configured in `RedisConfig`.
