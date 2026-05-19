# Portal Baqueano — Sistema de Gestión

Backend Spring Boot 3.5.x + Frontend React 18 (Vite) + MySQL 8 + Flyway,
empaquetado como WAR para WildFly 39 (Jakarta EE 10).

## Stack

| Capa | Tecnología | Versión |
|---|---|---|
| Java | OpenJDK Temurin | 25.0.3 LTS |
| Build | Maven | 3.9.x |
| Framework | Spring Boot | 3.5.14 |
| ORM | Hibernate (vía Boot) | 6.6.x |
| Migraciones | Flyway + flyway-mysql | 12.6.1 |
| Driver | mysql-connector-j | 9.3.0 |
| Mapeo | MapStruct | 1.6.3 |
| Auth | jjwt + spring-security | 0.13.0 / 6.5.x |
| Frontend | React + Vite | 18.3.1 / 5.4.21 |
| Servidor app | WildFly | 39.0.1.Final |

## Estructura

```
portal_baq/
├── pom.xml                          (padre, packaging=pom)
├── baqueano-backend/                (war, finalName=baqueano)
│   ├── pom.xml
│   ├── src/main/java/ar/com/baqueano/...
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml      (datasource MySQL local en :8081)
│   │   ├── application-prod.yml     (datasource JNDI para WildFly)
│   │   └── db/migration/V*.sql      (Flyway)
│   ├── src/main/webapp/WEB-INF/
│   │   └── jboss-deployment-structure.xml
│   ├── src/test/java/...
│   └── http/auth.http               (scenarios de prueba .http)
├── baqueano-frontend/               (Vite + React)
│   ├── package.json
│   ├── vite.config.js
│   └── src/...
└── scripts/
    ├── db_setup.sql                 (crea BD baqueano y baqueano_test)
    └── wildfly/
        ├── module.xml               (modulo com.mysql)
        ├── setup-mysql-module.ps1   (instala driver MySQL en WildFly)
        └── wildfly-datasource.cli   (crea BaqueanoDS via JNDI)
```

## Setup inicial (una sola vez)

### 1. Base de datos

MySQL 8 corriendo en `localhost:3306`. Como root:

```powershell
mysql -u root -p < scripts\db_setup.sql
```

Crea las BDs `baqueano` y `baqueano_test` y el usuario `baqueano/baqueano` con los grants necesarios.

### 2. WildFly — driver MySQL como módulo

```powershell
# 1. Asegurar que el jar del driver está descargado:
mvn -pl baqueano-backend dependency:resolve

# 2. Copiar al módulo de WildFly:
.\scripts\wildfly\setup-mysql-module.ps1
```

### 3. WildFly — datasource BaqueanoDS

Con WildFly corriendo:

```powershell
& "$env:WILDFLY_HOME\bin\standalone.bat"
```

En otra terminal:

```powershell
& "$env:WILDFLY_HOME\bin\jboss-cli.bat" --connect --file=scripts\wildfly\wildfly-datasource.cli
```

Esto crea el datasource bajo JNDI `java:jboss/datasources/BaqueanoDS` y hace un `reload`.

## Desarrollo local

Backend (puerto 8081, perfil `dev`):

```powershell
cd baqueano-backend
mvn spring-boot:run
```

Frontend (puerto 5173 con proxy a 8081):

```powershell
cd baqueano-frontend
npm install
npm run dev
```

Abrir [http://localhost:5173/](http://localhost:5173/) → login `admin / admin123`.

### Observabilidad y docs API (solo en dev)

- **Swagger UI**: [http://localhost:8081/baqueano/swagger-ui.html](http://localhost:8081/baqueano/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/baqueano/v3/api-docs](http://localhost:8081/baqueano/v3/api-docs)
- **Health**: [http://localhost:8081/baqueano/actuator/health](http://localhost:8081/baqueano/actuator/health)
- **Info (build)**: [http://localhost:8081/baqueano/actuator/info](http://localhost:8081/baqueano/actuator/info)

En perfil `prod` Swagger queda deshabilitado. `health` e `info` se exponen también.

### Logs

- Dev/test: salida humana en consola (con DEBUG en `ar.com.baqueano`).
- Prod: JSON estructurado vía `logstash-logback-encoder` (un evento por línea, listo
  para ingestar en Loki / ELK / CloudWatch). Campos: `@timestamp`, `level`, `logger`,
  `thread`, `msg`, `stack` + `traceId`/`spanId` cuando hay MDC.

Tests:

```powershell
# Backend (Junit + Mockito, requiere baqueano_test creada)
cd baqueano-backend
mvn test

# Frontend (Vitest + React Testing Library)
cd baqueano-frontend
npm test
```

## Build de producción

```powershell
mvn -Pprod -pl baqueano-backend -am clean package
```

El perfil `prod`:
1. Instala Node 24 + npm 11 en `baqueano-backend/target/node-runtime` (aislado del sistema).
2. Ejecuta `npm ci` y `npm run build` en `baqueano-frontend/` (Vite genera `dist/` con `base: /baqueano/`).
3. Copia `baqueano-frontend/dist/**` a `baqueano-backend/target/classes/static/`.
4. Empaqueta todo en `baqueano-backend/target/baqueano.war`.

## Despliegue en WildFly

Antes de deployar por primera vez, **setear el perfil de Spring** que usa el WAR.
Editá `$WILDFLY_HOME\bin\standalone.conf.bat` y agregá:

```bat
set "JAVA_OPTS=%JAVA_OPTS% -Dspring.profiles.active=prod"
set "JAVA_OPTS=%JAVA_OPTS% -DJWT_SECRET=cambiar-por-un-secreto-real-de-32-bytes-minimo"
```

O alternativamente, definir `SPRING_PROFILES_ACTIVE=prod` como variable de entorno antes de
arrancar WildFly. Sin esto, el WAR levantaría con el perfil `dev` y se conectaría con HikariCP
embebido en vez del JNDI de WildFly.

### Deploy (tres opciones)

**Hot deploy (más simple):**

```powershell
Copy-Item baqueano-backend\target\baqueano.war "$env:WILDFLY_HOME\standalone\deployments\"
```

WildFly detecta el WAR y lo despliega. Aparece `baqueano.war.deployed` cuando termina, o
`baqueano.war.failed` con el detalle del error.

**Vía jboss-cli:**

```powershell
& "$env:WILDFLY_HOME\bin\jboss-cli.bat" --connect --command="deploy baqueano-backend\target\baqueano.war"
```

**Vía plugin Maven (opcional, agregar `wildfly-maven-plugin` al pom si se quiere):**

```powershell
mvn -pl baqueano-backend wildfly:deploy
```

### Validación

App disponible en [http://localhost:8080/baqueano/](http://localhost:8080/baqueano/) →
redirige a `/baqueano/login` → admin/admin123. La API responde en `/baqueano/api/v1/...`.

## Endpoints principales

Ver `baqueano-backend/http/auth.http` para los scenarios completos.

| Método | Path | Auth | Permiso |
|---|---|---|---|
| POST | `/api/v1/auth/login` | público | — |
| POST | `/api/v1/auth/refresh` | público (con refresh) | — |
| POST | `/api/v1/auth/logout` | autenticado | — |
| POST | `/api/v1/contactos` | **público** | — (alta desde formulario externo) |
| GET/PUT/DELETE | `/api/v1/contactos/**` | autenticado | VER/EDITAR/ELIMINAR |
| GET/POST/PUT/DELETE | `/api/v1/{usuarios\|perfiles\|parametros}/**` | autenticado | VER/CREAR/EDITAR/ELIMINAR |
| GET | `/api/v1/menu/mio` | autenticado | — (devuelve menú del perfil del usuario) |
| GET | `/api/v1/dashboard/resumen` | autenticado | — |
