# Sunrise Dental Clinic — full-stack system (CIS6003 Advanced Programming)

One Maven project containing everything:
- **Frontend**: plain HTML/CSS/JS under `src/main/resources/static`, served
  by Spring Boot itself (no separate server, no build step, no CORS issues —
  same origin as the API).
- **Backend**: Java (Spring Boot) REST API under `src/main/java`.
- **Database**: MySQL, running under WAMP.

## 1. WAMP setup

1. Start WAMP and confirm the tray icon is green (Apache + MySQL both running).
2. Open phpMyAdmin (`http://localhost/phpmyadmin`) and create a database
   named `dentalclinic` — or skip this, since the connection string below
   uses `createDatabaseIfNotExist=true` and will create it for you on first run.
3. WAMP's default MySQL credentials are user `root` with **no password**,
   on port `3306`. That's already set in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dentalclinic?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=
   ```

   If your WAMP MySQL uses a different port or a root password, change
   those two lines to match.

## 2. Open and run in NetBeans

This is a plain Maven project (`pom.xml` at the root), and NetBeans (12+)
understands Maven natively — no special NetBeans project files are needed.

1. **File > Open Project...**, select the `dental-clinic-system` folder
   (the one containing `pom.xml`). NetBeans recognizes it as a Maven
   project automatically and downloads dependencies on first open.
2. Right-click the project > **Clean and Build** (or `mvn clean install`
   in the embedded terminal).
3. Right-click the project > **Run** (or open
   `DentalClinicApplication.java` and click the green Run arrow). This
   starts the embedded server on port 8080.
4. Open `http://localhost:8080` in a browser.

If NetBeans shows unresolved-dependency errors, right-click the project >
**Reload POM**, then **Clean and Build** again.

## 3. Run the whole app

```
mvn spring-boot:run
```

Hibernate creates all tables in `dentalclinic` automatically
(`spring.jpa.hibernate.ddl-auto=update`), and `DataSeeder` inserts starter
logins and treatment prices on first startup.

Then open **`http://localhost:8080`** in a browser — that's the whole
application, frontend and backend together, on one port.

Seeded logins (username / password / role):
- `admin` / `admin123` / ADMIN
- `receptionist` / `reception123` / RECEPTIONIST
- `dfernando` / `dentist123` / DENTIST (Dr. Fernando)
- `djayasuriya` / `dentist123` / DENTIST (Dr. Jayasuriya)

Run the backend test suite:
```
mvn test
```

## 4. Frontend pages

| Page | File | Purpose |
|---|---|---|
| Login | `index.html` | Role tabs (Admin / Receptionist / Dentist), signs in, stores credentials for the session |
| Dashboard | `dashboard.html` | Admin/Receptionist see all appointments; Dentist sees only their own |
| Register appointment | `register.html` | New-patient/appointment form with validation |
| Search and bill | `search.html` | Look up by appointment number, calculate the bill, print or download it as PDF |
| Manage staff | `staff.html` | Admin-only: create Dentist/Receptionist logins (username + password auto-generated and emailed), enable/disable accounts |
| Help | `help.html` | Step-by-step instructions for new staff |

The login screen's role tabs are a client-side convenience — the server
still returns the account's real role, and the login is rejected with a
clear message if it doesn't match the selected tab, so the check is never
just cosmetic.

The frontend never talks to the database directly — every page calls the
REST API in `js/api.js` over `fetch()`, using the session's Basic Auth
header, exactly as a JavaFX or mobile client would.

## 5. API summary

| Method | Endpoint | Role | Purpose |
|---|---|---|---|
| POST | `/api/auth/login` | public | Verify credentials, return role for menu selection |
| POST | `/api/appointments` | Admin, Receptionist | Register new appointment |
| GET | `/api/appointments/{no}` | Admin, Receptionist, Dentist | Look up one appointment |
| GET | `/api/appointments` | Admin, Receptionist | List all appointments |
| GET | `/api/appointments/dentist/{name}` | Admin, Receptionist, Dentist | A dentist's own schedule |
| POST | `/api/bills/{no}/generate` | Admin, Receptionist | Calculate and store the bill |
| GET | `/api/bills/{no}` | Admin, Receptionist | Retrieve a generated bill (JSON) |
| GET | `/api/bills/{no}/pdf` | Admin, Receptionist | Download the generated bill as a PDF file |
| GET | `/api/treatments` | authenticated | Treatment price list for the form dropdown |
| GET | `/api/admin/staff` | Admin | List Dentist/Receptionist accounts |
| POST | `/api/admin/staff` | Admin | Create a Dentist/Receptionist account (auto-generates username + password, emails them) |
| PATCH | `/api/admin/staff/{id}/status` | Admin | Enable/disable a staff account |

### Email delivery

`app.mail.enabled=false` by default (see `application.properties`), so the
project runs immediately in NetBeans without a real mailbox — staff
creation still succeeds and the generated password is shown once on
screen as a fallback. To send real emails, set `app.mail.enabled=true` and
fill in `spring.mail.username` / `spring.mail.password` with a Gmail App
Password (Google Account > Security > App passwords), or point
`spring.mail.host`/`port` at any other SMTP server.

All `/api/**` endpoints except login require HTTP Basic auth. The static
pages (`/`, `*.html`, `/css/**`, `/js/**`) are open so the browser can load
the login screen before it has credentials.

## 6. Architecture and design patterns (Task B writeup)

- **Layered MVC**: `controller` (REST) → `service` (business rules) →
  `dao` (Spring Data JPA interfaces — the DAO pattern) → `model` (JPA
  entities), with the HTML/CSS/JS frontend as the view layer calling over
  HTTP. Package layout under `src/main/java/lk/icbt/dentalclinic`:
  `controller/`, `service/` (+ `service/impl/`), `dao/`, `model/`,
  `dto/`, `config/`, `security/`, `exception/`, `util/`. Reference SQL for
  the four tables lives in `src/main/resources/db/schema.sql` (Hibernate
  creates them automatically; the file is there for manual inspection).
- **EmailService** — wraps Spring Mail so `StaffServiceImpl` never has to
  know whether SMTP is actually configured; failures are logged and
  swallowed rather than blocking account creation.
- **BillPdfService** — renders a generated bill to a one-page PDF with
  OpenPDF, returned as a downloadable attachment from `BillController`.
- **Singleton** — `AppointmentNumberGenerator`: one shared, thread-safe
  counter for appointment numbers across the whole running app.
- **Builder** — `Appointment` uses Lombok's `@Builder` to assemble the
  entity field-by-field instead of a telescoping constructor.
- **Factory** — `AppointmentFactory` is the single place that turns an
  inbound request into a persisted-ready `Appointment`, wiring the
  Singleton and the Builder together.
- **Strategy** — `BillingStrategy` / `StandardBillingStrategy` isolate the
  fee calculation so a different pricing rule (insurance, discount) can be
  swapped in without touching `BillingServiceImpl`.
- **DAO** — the four Spring Data JPA interfaces in `dao/` (`UserDao`,
  `AppointmentDao`, `BillDao`, `TreatmentDao`).
- Security: Spring Security with a DB-backed `UserDetailsService`, BCrypt
  password hashing, and method-level `@PreAuthorize` role checks — enforced
  server-side, never trusted to the browser.

## 7. Test plan rationale (Task C writeup)

Tests were written test-first against `AppointmentService` and
`BillingService`, before the frontend depended on them:

1. Registering an appointment returns a generated number and `SCHEDULED` status.
2. Looking up an unknown appointment number throws `NotFoundException`.
3. Looking up a known appointment returns its full stored details.
4. Generating a bill adds the fixed consultation fee to the treatment fee
   correctly — this pins down the Strategy pattern's contract.
5. Generating a bill for an unknown appointment, and fetching a bill that
   was never generated, both fail with `NotFoundException`.

Run `mvn test` and screenshot the green run for the documentation appendix.

## 8. Git workflow (Task D)

Suggested commit cadence:
1. `init: project scaffold and pom.xml`
2. `feat: domain model and JPA entities`
3. `feat: DAO interfaces and DTOs`
4. `feat: appointment registration service + factory/singleton/builder patterns`
5. `feat: billing service + strategy pattern`
6. `feat: Spring Security auth and role-based endpoints`
7. `feat: HTML/CSS/JS frontend (login, dashboard, register, search, help)`
8. `chore: switch datasource to MySQL/WAMP`
9. `test: appointment and billing service unit tests`
10. `docs: README, API summary, WAMP setup`

Use feature branches per task (`feature/appointments`, `feature/billing`,
`feature/frontend`) merged via pull requests into `main` to demonstrate a
branching workflow rather than direct commits to `main`.
