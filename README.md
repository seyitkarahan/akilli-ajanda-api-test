# Akıllı Ajanda API

Spring Boot tabanlı akıllı ajanda uygulamasının backend API ve test projesi. Java 21, Spring Boot 3.5, PostgreSQL ve Thymeleaf kullanılmaktadır.

---

## 🧪 Test

Proje üç katmanlı test stratejisi kullanır: **birim testleri**, **entegrasyon testleri** ve **Selenium sistem testleri**. Tüm testler JUnit 5 ile yazılmıştır.

### Test Kategorileri

| Kategori | Paket | Açıklama |
|----------|--------|----------|
| **Birim Testleri** | `com.seyitkarahan.akilli_ajanda_api.service.*` | Service katmanı iş mantığı testleri |
| **Entegrasyon Testleri** | `com.seyitkarahan.akilli_ajanda_api.integration.*` | Controller + servis uçtan uca API testleri |
| **Selenium Testleri** | `com.seyitkarahan.akilli_ajanda_api.seleniumTest.*` | Tarayıcı tabanlı end-to-end testler |

### Birim Testleri

Service sınıflarını mock’layarak iş kurallarını test eder:

```bash
./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.service.*"
```

Örnek test sınıfları: `AuthServiceTest`, `CategoryServiceTest`, `NoteServiceTest`, `TaskServiceTest`, `EventServiceTest`, `TagServiceTest`, `RecurringTaskRuleServiceTest`, `EventNotificationServiceTest`, `TaskNotificationServiceTest`, `UserSettingsServiceTest`.

### Entegrasyon Testleri

Controller’ları gerçek HTTP istekleriyle test eder; test ortamında H2 veya test profili kullanılır:

```bash
./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.integration.*"
```

Örnek test sınıfları: `AuthIntegrationTest`, `CategoryControllerIntegrationTest`, `NoteControllerIntegrationTest`, `TaskControllerIntegrationTest`, `EventControllerIntegrationTest`, `TagControllerIntegrationTest`, `ImageControllerIntegrationTest`, `RecurringTaskRuleControllerIntegrationTest`, `EventNotificationControllerIntegrationTest`, `TaskNotificationControllerIntegrationTest`, `UserSettingsControllerIntegrationTest`.

### Selenium (Sistem) Testleri

Uygulama Docker ile ayağa kalktıktan sonra çalıştırılır. Headless Chrome ile giriş, kategori, not, görev, etkinlik ve resim özelliklerini tarayıcı üzerinden test eder:

| Test Sınıfı | Kapsam |
|-------------|--------|
| `SeleniumIntegrationTest` | Kimlik doğrulama (login) |
| `CategoryPageTest` | Kategori CRUD |
| `NotePageTest` | Not CRUD |
| `TaskPageTest` | Görev CRUD |
| `EventPageTest` | Etkinlik CRUD |
| `ImageFilePageTest` | Dosya/resim yükleme |

Selenium testlerini **sadece** backend ve veritabanı çalışırken (ör. `docker compose up` sonrası) çalıştırın:

```bash
./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.SeleniumIntegrationTest"
./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.CategoryPageTest"
# ... diğer selenium test sınıfları
```

### Tüm Testleri Çalıştırma

```bash
./gradlew test
```

### Kod Kapsamı (JaCoCo)

JaCoCo ile birim/entegrasyon testleri için kod kapsam raporu üretilir:

```bash
./gradlew test jacocoTestReport
```

Raporlar:

- **HTML:** `build/reports/jacoco/test/html/index.html`
- **XML:** CI/CD (Jenkins vb.) için `build/reports/jacoco/test/jacocoTestReport.xml`

---

## 🐳 Docker

Uygulama çok aşamalı (multi-stage) bir Dockerfile ile derlenir; çalışma ortamı Docker Compose ile tanımlanır.

### Dockerfile

- **Build aşaması:** `gradle:jdk21-jammy` ile `gradle clean build -x test` (testler image build’de çalışmaz).
- **Çalışma aşaması:** `eclipse-temurin:21-jre-jammy`, tek JAR ile çalışır.
- Port: **8081**.
- `/app/uploads` dizini dosya yüklemeleri için kullanılır.

Yerel image oluşturmak için (proje kökünde):

```bash
docker build -t akilli-ajanda-api .
docker run -p 8081:8081 akilli-ajanda-api
```

### Docker Compose

`docker-compose.yml` üç servis tanımlar:

| Servis | Image/Build | Port | Açıklama |
|--------|-------------|------|----------|
| **app** | `build: .` (Dockerfile) | 8081 | Spring Boot API |
| **db** | `postgres:16-alpine` | 5433→5432 | PostgreSQL veritabanı |
| **pgadmin** | `dpage/pgadmin4` | 5050→80 | pgAdmin arayüzü |

**Başlatma:**

```bash
docker compose up -d --build
```

**Durdurma (volume’lar dahil):**

```bash
docker compose down -v
```

**Ortam değişkenleri:** Veritabanı bağlantısı ve dosya yükleme dizini `docker-compose.yml` içindeki `environment` ile ayarlanır. Şifre ve hassas bilgileri production’da ortam değişkeni veya secrets ile vermeniz önerilir.

**Not:** Selenium testleri çalıştırmadan önce `docker compose up -d` ile uygulama ve veritabanının ayakta olduğundan emin olun.

---

## 🔧 Jenkins

CI/CD pipeline `Jenkinsfile` ile tanımlanmıştır. GitHub push tetikleyicisi kullanılır; timeout 40 dakikadır.

### Pipeline Aşamaları

| # | Aşama | Açıklama |
|---|--------|----------|
| 1 | **Checkout Source Code** | Repo’dan kaynak kodu alır |
| 2 | **Build Application** | `./gradlew clean build -x test` ile uygulamayı derler |
| 3 | **Unit Tests** | `service.*` paketindeki birim testlerini çalıştırır; JUnit XML raporu arşivlenir |
| 4 | **Integration Tests** | `integration.*` paketindeki entegrasyon testlerini çalıştırır; JUnit XML arşivlenir |
| 5 | **Start System with Docker** | `docker compose down -v \|\| true` sonrası `docker compose up -d --build` ile sistemi ayağa kaldırır; backend’in `http://localhost:8081/login` ile hazır olması beklenir |
| 6.1 | **Selenium: Authentication** | `SeleniumIntegrationTest` |
| 6.2 | **Selenium: Category Features** | `CategoryPageTest` |
| 6.3 | **Selenium: Note Features** | `NotePageTest` |
| 6.4 | **Selenium: Task Features** | `TaskPageTest` |
| 6.5 | **Selenium: Event Features** | `EventPageTest` |
| 6.6 | **Selenium: Image Features** | `ImageFilePageTest` |
| 7 | **Coverage Report** | `./gradlew jacocoTestReport`; HTML raporu artifact olarak arşivlenir |

Test aşamaları (3, 4, 6.x) `catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE')` ile pipeline’ı kırmadan hata verebilir; böylece tüm aşamalar ve raporlar tamamlanır.

### Ortam

- `CHROME_HEADLESS=true` — Selenium için headless Chrome.
- `PATH` — Gradle, Homebrew ve Docker’ın bulunabilmesi için güncellenir.

### Post Actions

- **always:** `docker compose down` ile konteynerler durdurulur.
- **success / failure:** İlgili mesajlar loglanır.

### Jenkins’te Gereksinimler

- Jenkins sunucusunda **Docker** ve **Docker Compose** kurulu olmalı.
- **Java 21** (Gradle wrapper kullanılıyor; JDK genelde Gradle ile gelir).
- JUnit eklentisi ile test sonuçları `build/test-results/test/*.xml` üzerinden gösterilir.
- İsteğe bağlı: JaCoCo eklentisi ile `build/reports/jacoco/test/html` artifact’ı kullanılabilir.

---

## 📋 Özet Komutlar

| Amaç | Komut |
|------|--------|
| Derleme (test olmadan) | `./gradlew clean build -x test` |
| Tüm testler | `./gradlew test` |
| Birim testler | `./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.service.*"` |
| Entegrasyon testleri | `./gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.integration.*"` |
| JaCoCo raporu | `./gradlew test jacocoTestReport` |
| Docker build & run | `docker compose up -d --build` |
| Docker durdur | `docker compose down -v` |

---

## Teknolojiler

- **Java 21**, **Spring Boot 3.5**
- **Spring Security**, **JWT (jjwt)**
- **PostgreSQL**, **Spring Data JPA**
- **Thymeleaf**, **SpringDoc OpenAPI**
- **JUnit 5**, **Selenium**, **JaCoCo**
- **Gradle**, **Docker**, **Jenkins**
