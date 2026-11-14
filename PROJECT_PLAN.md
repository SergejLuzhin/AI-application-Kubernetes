# 🎯 Итоговый проект: ИИ-приложение анализа тональности в Kubernetes

> Живой документ-план. Отмечай выполненные шаги `[x]` по мере продвижения.
> Среда: macOS (Apple Silicon), Docker Desktop, Minikube, IntelliJ IDEA Community (Maven).

## 0. Что уже готово (переиспользуем из ДЗ №2)
- [x] Minikube + kubectl + Helm + Docker Desktop установлены
- [x] Кластер Minikube запущен (2 CPU / 4 ГБ, 1 нода)
- [x] Prometheus (chart prometheus-community/prometheus; configmapReload отключён из-за ARM64)
- [x] Grafana (PVC 5Gi; datasource Prometheus подключён автоматом из values.yaml)
- [x] Spring Boot + Micrometer проект (образ spring-micrometer-demo:1.0)
- [x] Дашборд JVM Custom Dashboard + алерт по G1 Eden Space

## 1. Этап 1 — Инфраструктура (Критерий 1, 6 баллов)
- [ ] 1.1 Аудит: `sysctl -n hw.memsize`, `minikube status`, `kubectl get nodes`, `kubectl get pods -n monitoring`
- [ ] 1.2 Вторая нода. Путь А (рекомендуется): `minikube node add` — сохраняет мониторинг, суммарно 4 CPU / 8 ГБ.
      Путь Б: новый профиль `minikube start -p ai-project --cpus=4 --memory=8192 --nodes=2` (мониторинг ставить заново)
- [ ] 1.3 Аддоны: `minikube addons enable metrics-server` (нужен для HPA), `minikube addons enable ingress`
- [ ] 1.4 Диагностика: `kubectl get nodes -o wide`, `kubectl get pods -A`, `kubectl top nodes` → скриншоты

## 2. Этап 2 — Приложение и контейнеризация (Критерий 2, 8 баллов)
- [ ] 2.1 `SentimentController` + `SentimentService`: GET /api/sentiment?text=... → {"sentiment","score"} (лексиконная модель)
- [ ] 2.2 Локальный тест в IDEA: curl "localhost:8080/api/sentiment?text=hello" → скриншот кода и вывода
- [ ] 2.3 Dockerfile <150 МБ: multi-stage + jlink custom JRE на Alpine (запасной вариант: bellsoft/liberica-openjre-alpine:17)
- [ ] 2.4 `docker build -t sentiment-ai:2.0 .` → `docker images` (скриншот размера) → `docker run` + curl → `minikube image load sentiment-ai:2.0`

## 3. Этап 3 — Развертывание (Критерий 3, 8 баллов)
- [ ] 3.1 Манифесты: namespace ai-app; Deployment replicas=3 (обязательно requests.cpu!); Service LoadBalancer; Ingress /api; HPA v2 CPU 60%, min 3, max 6
- [ ] 3.2 `kubectl apply -f k8s/` → `kubectl get pods -n ai-app -o wide` → скриншот трёх подов (желательно на разных нодах)
- [ ] 3.3 `minikube tunnel` (sudo, отдельный терминал) → EXTERNAL-IP → curl через LoadBalancer → скриншот
- [ ] 3.4 Ingress: curl с Host-заголовком на /api/sentiment → скриншот `kubectl get ingress` + вывода
- [ ] 3.5 Нагрузка → `kubectl get hpa -w` → рост реплик 3→6 и спад после снятия → скриншоты до/во время/после

## 4. Этап 4 — Мониторинг (Критерий 4, 10 баллов)
- [ ] 4.1 Аннотации prometheus.io/* + тег application="sentiment-ai"; Prometheus → Status → Targets → UP → скриншот
- [ ] 4.2 Дашборд Sentiment AI Monitoring: RPS по uri, latency p95, JVM heap/CPU → скриншот
- [ ] 4.3 Скриншот всплеска RPS/CPU во время нагрузочного теста (связка мониторинга и HPA)

## 5. Этап 5 — Аналитика arXiv (Критерий 5, 12 баллов)
- [ ] 5.1 Разбор пяти статей (ссылки ниже, проверены на arXiv)
- [ ] 5.2 Таблица 5+ статей: название / авторы / год / arXiv ID / ключевая идея / применимость к проекту
- [ ] 5.3 Отчёт ≥10 страниц: три темы (ИИ-оптимизация ресурсов; serverless-ИИ в K8s; RL/динамическое планирование) + выводы о перспективах оркестрации

### Статьи (верифицированы)
| Тема | Статья | arXiv |
|---|---|---|
| Оптимизация памяти при serving | Efficient Memory Management for LLM Serving with PagedAttention (vLLM) | https://arxiv.org/abs/2309.06180 |
| Serverless-ИИ, cold start | ServerlessLLM: Low-Latency Serverless Inference for LLMs | https://arxiv.org/abs/2401.14351 |
| Динамическое планирование запросов | Llumnix: Dynamic Scheduling for LLM Serving | https://arxiv.org/abs/2406.03243 |
| Serving на вытесняемых инстансах | SpotServe: Serving Generative LLMs on Preemptible Instances | https://arxiv.org/abs/2311.15566 |
| Мультиплексирование моделей | AlpaServe: Statistical Multiplexing with Model Parallelism | https://arxiv.org/abs/2302.11665 |

## 6. Этап 6 — Оформление и сдача (Критерий 6, 6 баллов)
- [ ] 6.1 Публичный GitHub-репозиторий: код, Dockerfile, все YAML, README (архитектура + quickstart)
- [ ] 6.2 Отчёт PDF ≥10 страниц: титул / введение / архитектура / развертывание / мониторинг / тренды / заключение / ссылки / приложения
- [ ] 6.3 Презентация 8–15 слайдов по структуре задания
- [ ] 6.4 Финальный чек-лист «критерий → доказательство»

## 7. Карта критериев и доказательств
| Критерий | Баллы | Доказательства |
|---|---|---|
| 1 Инфраструктура | 6 | скриншоты установки, аудита, нод, аддонов |
| 2 Контейнеризация | 8 | Dockerfile, docker images <150MB, код эндпоинта, curl, ссылка на Git |
| 3 Развертывание | 8 | YAML Deployment/Service/Ingress/HPA, поды, tunnel, масштабирование HPA |
| 4 Мониторинг | 10 | Prometheus targets UP, рабочий дашборд Grafana, всплеск под нагрузкой |
| 5 Аналитика | 12 | PDF ≥10 страниц, 3+ статьи (у нас 5), таблица, выводы |
| 6 Оформление | 6 | презентация 8–15 слайдов, структура, код и скриншоты в отчёте |

## 8. Риски и проверенные обходы
- ARM64: config-reloader без arm64-сборки → отключён через --set configmapReload.*.enabled=false; аддоны и curlimages/curl мультиархитектурны
- github.io блокируется (connection reset by peer) → чарты ставим из локального .tgz через GitHub Releases API
- port-forward хрупкий (sandbox not found) → нагрузку даём изнутри кластера: kubectl run loadgen
- minikube tunnel требует sudo и отдельного терминала
- HPA не работает без metrics-server → аддон обязателен
- base64: GNU vs BSD; на этом Mac GNU из Homebrew, поэтому --decode работает

## 9. Журнал прогресса и ошибок
| Дата | Этап/шаг | Результат или ошибка | Решение |
|---|---|---|---|
| | | | |

## 10. Следующее действие
Выполнить аудит (шаг 1.1) и выбрать Путь А или Б для шага 1.2.
