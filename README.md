# The Thinking Realms — MVP

An adaptive mathematics + thinking-development game. Full architecture spec is in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

This repository contains a **complete, runnable MVP**: authentication, a rule-based mastery/adaptive engine, a question engine with deterministic answer validation, one game world (Number Forest + Logic City), three missions (including one boss challenge), XP/leveling, achievements, and the full animation/reward-sequencer system described in the architecture doc.

**What this is not (yet):** the full K-12→calculus curriculum, the AI tutor, teacher/admin dashboards, or the remaining four game regions. See "Honest Scope" below.

---

## Project Structure

```text
thinking-realms/
├── docs/
│   └── ARCHITECTURE.md        ← full product & technical spec
├── backend/                   ← Java 17 + Spring Boot (modular monolith)
│   └── src/main/java/com/thinkingrealms/backend/
│       ├── domain/             JPA entities
│       ├── repository/         Spring Data repositories
│       ├── service/             business logic (mastery, XP, achievements, recommendations)
│       ├── controller/          REST controllers
│       ├── dto/                  request/response DTOs
│       ├── security/            JWT auth
│       └── seed/                 DataSeeder — loads MVP curriculum on first run
└── frontend/                  ← React + TypeScript + Vite + Tailwind + Framer Motion
    └── src/
        ├── design-system/motion/   the animation system (motion tokens, reduced-motion)
        ├── features/
        │   ├── auth/                login / register
        │   ├── dashboard/            math + thinking profiles
        │   ├── game/                 world map, regions, missions
        │   ├── practice/              question flow, correct/wrong animations
        │   └── rewards/                RewardSequencer, XP bar
        └── api/                      TanStack Query hooks + axios client
```

---

## Running the Backend

Requires Java 17+ and Maven (or use the included `mvnw` if you add one — this repo assumes a local Maven install).

```bash
cd backend
mvn spring-boot:run
```

The backend starts on `http://localhost:8080` with an **in-memory H2 database** by default — no setup required. On first boot, `DataSeeder` automatically loads:

- 5 math topics (Numbers, Arithmetic, Fractions, Percentages, Basic Algebra)
- 6 thinking skills (Pattern Recognition, Logical Reasoning, Estimation, Problem Decomposition, Strategy Selection, Error Detection)
- 16 questions spanning all the question types in the spec
- 1 game world (The Thinking Realms) with 2 regions (Number Forest, unlocked; Logic City, unlocks at 150 XP)
- 3 missions in Number Forest, the third of which is a boss challenge
- 2 starter achievements

API docs: `http://localhost:8080/swagger-ui.html`
H2 console (dev only): `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:thinkingrealms`, user `sa`, no password)

### Switching to Supabase Postgres

Edit `backend/src/main/resources/application.yml`: comment out the H2 `datasource` block and uncomment the Supabase block, then set the environment variables shown in `backend/.env.example` (get the connection string from Supabase → Project Settings → Database).

### Running backend tests

```bash
cd backend
mvn test
```

Covers the answer-validation layer (fraction/decimal/percent equivalence), the XP/leveling curve, and the mastery-update rule — the core business logic called out in the architecture's testing section.

### Known fixes already applied (post-initial-build)

A few issues surfaced during real-world testing against a live Supabase Postgres instance and were fixed directly in this codebase:

- **Lazy-loading of `@ElementCollection` fields** (`Question.thinkingSkillSlugs`, `Question.hints`, `Mission.questionIds`) — these are lazy by default under Hibernate, and with `open-in-view: false` (the correct setting for production) any read of them outside an active transaction throws `LazyInitializationException`. Fixed by adding class-level `@Transactional(readOnly = true)` to `QuestionService` and `GameService`, plus eagerly copying `thinkingSkillSlugs` into a plain `ArrayList` inside `QuestionService.toResponse()` so Jackson never touches a Hibernate proxy during JSON serialization.
- **Supabase pooler + JDBC prepared statements**: connecting through the transaction-mode pooler (port `6543`) causes `prepared statement "S_1" already exists` errors, because pooled connections get shared across clients while the JDBC driver caches prepared statement names per logical connection. Use the **direct connection** (port `5432`) for local dev instead, as reflected in `.env.example`.
- **`JwtUtil` signing**: uses `signWith(Key)` (auto-detects algorithm) rather than the deprecated `signWith(Key, SignatureAlgorithm)` overload, for compatibility with jjwt 0.12.x.

---

## Running the Frontend

Requires Node 18+.

```bash
cd frontend
npm install
npm run dev
```

Opens on `http://localhost:5173`. The Vite dev server proxies `/api` requests to `http://localhost:8080`, so just start the backend first.

1. Register a new account
2. You'll land on the dashboard (empty profiles at first — that's expected)
3. Click "Enter The Thinking Realms" → pick Number Forest → pick a mission
4. Answer a question correctly to see the full reward sequence: particle burst → XP roll-up → (eventually) achievement/level-up/region-unlock overlays, played in order by `RewardSequencer`
5. Earn enough XP (150) and the next attempt will trigger a **region unlock** event for Logic City

---

## How the Animation System Works (the part you asked to prioritize)

- `frontend/src/design-system/motion/motion.ts` — every duration and easing curve used anywhere in the app. No component invents its own.
- `frontend/src/design-system/motion/reducedMotion.ts` — `prefers-reduced-motion` detection + fallback variants that preserve information without motion.
- `frontend/src/features/rewards/RewardSequencer.tsx` — consumes the backend's ordered `RewardEvent[]` and plays exactly one overlay at a time (XP → achievement → level up → region unlock), matching architecture Section 13.
- `frontend/src/features/practice/QuestionCard.tsx` — the correct-answer scale-bounce + particle burst, and the deliberately gentle wrong-answer shake + amber highlight, from architecture Section 12.4.
- The backend never decides *how* something looks — `AttemptService.java` only decides *what* happened and returns typed events; all animation logic lives in the frontend.

---

## Honest Scope — What's Really Here vs. What's Deferred

**Built and working:**
- Auth (register/login, JWT, BCrypt)
- Rule-based mastery engine (math + thinking, tracked separately, EMA update rule)
- Rule-based recommendation engine
- Deterministic answer validation (0.5 / 1/2 / 50% equivalence)
- Question engine supporting all 11 question-type categories from the spec (seeded examples of most)
- XP, leveling, 2 achievements, region-unlock-by-XP-threshold
- 1 world, 2 regions, 3 missions (1 boss)
- Full signature-animation set + RewardSequencer + reduced-motion support
- Unit tests for the core scoring/mastery/leveling logic

**Deferred (architected for, not built):** AI tutor, full curriculum beyond the 5 MVP topics, remaining 4 regions, teacher/admin dashboards, ML-based adaptive engine, Lottie/Rive mascot assets, real Supabase project (wired for it, not connected to a live one since credentials weren't available here).

If you want any of these built next, say which one and we'll do it properly rather than stub it.
