# pet-health-tracker

## Design notes

- **Single-user MVP:** the current version has no users, login or sign-up. The API (`designs/api-design/pet-health-api.yaml`) and ERD (`designs/db-design/pet-health-erd.md`) are designed for one user.
- **UI design:** `designs/ui-design/Pet Health Tracker.png` still includes the log-in and sign-up pages and the welcome messages for users. Ignore those screens for now; they're kept for a future multi-user version.

## Stack

- **Backend:** Java Spring
- **Frontend:** React (built with Vite)
- **Database:** Microsoft SQL Server

## Local setup

### Database (SQL Server)

Requirements: SQL Server 2022 and SQL Server Management Studio (SSMS), with SQL Server Authentication and TCP/IP (port 1433) enabled.

1. In SSMS, connect with **Windows Authentication** and click **New Query**.
2. Run the script below, replacing `<your-password>` with a password of your own:

   ```sql
   CREATE DATABASE pet_health_tracker;
   GO
   CREATE LOGIN pethealth_app WITH PASSWORD = '<your-password>';
   GO
   USE pet_health_tracker;
   CREATE USER pethealth_app FOR LOGIN pethealth_app;
   ALTER ROLE db_owner ADD MEMBER pethealth_app;
   ```

   This creates the database and an app login that can only access this database. The tables are created by the Spring Boot backend, so there's no table script to run.

> Never commit the real password. Keep it in local config only.

### Backend (Spring Boot)

Requirements: JDK 17 or newer, with `JAVA_HOME` pointing at the JDK folder (for example `C:\Program Files\Java\jdk-17`). Maven isn't needed; the project includes the Maven wrapper (`mvnw`).

1. Copy `backend/application-local.properties.example` to `backend/application-local.properties` and replace `<your-password>` with the `pethealth_app` password from the database step. This file is in `.gitignore`, so the password is never committed.
2. Start the server from the `backend` folder:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

   On the first run, Hibernate creates the `pet` table in `pet_health_tracker`. The API runs at http://localhost:8080 (try http://localhost:8080/pets).
3. Run the tests (they use an in-memory H2 database, so SQL Server doesn't need to be running):

   ```bash
   ./mvnw test
   ```

So far only the `/pets` endpoints are built. Health records, medications and appointments come next.
