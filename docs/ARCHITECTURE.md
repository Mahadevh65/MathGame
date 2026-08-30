# THE THINKING REALMS
## Master Architecture & Specification (v2 — Animation as a First-Class Requirement)

---

## 1. Product Vision

The Thinking Realms is an adaptive learning game where mathematics is the *medium* and thinking development is the *goal*. Students progress through an original game world, but every mechanic — missions, bosses, rewards, animation — is designed to reinforce the learning loop: discover, think, attempt, fail productively, analyze, retry, generalize, transfer.

The product must never feel like a quiz app with a skin. Motion, pacing, and feedback are core product surface area, not cosmetic polish added at the end.

---

## 2. Core Objectives

1. Teach mathematics progressively from foundational numeracy to advanced mathematics.
2. Develop domain-general thinking skills (reasoning, pattern recognition, decomposition, estimation, strategy selection, error detection, spatial reasoning, critical thinking, creativity, generalization, communication, decision-making) and measure them independently of math mastery.
3. Deliver this through a coherent, premium-feeling game — not a school LMS.
4. Make every animation purposeful: it must communicate progress, discovery, achievement, feedback, or cause/effect.

---

## 3. User Types

```text
STUDENT   — learn, practice, play, track progress
TEACHER   — view students, assign work, monitor progress
ADMIN     — manage curriculum, questions, users, game content
```

Authorization architecture is built for all three roles from day one, even though teacher/admin functionality is limited in the MVP.

---

## 4. Mathematics Curriculum Architecture

```text
Foundation → Arithmetic → Algebra → Geometry → Trigonometry
→ Statistics & Probability → Advanced Mathematics (Calculus, Linear Algebra, Number Theory)
```

Modeled as a directed graph: `math_topics` → `math_subtopics`, with `math_prerequisites` edges enabling prerequisite-aware recommendations. MVP topics: Numbers, Arithmetic, Fractions, Percentages, Basic Algebra.

---

## 5. Thinking Skill Architecture

Twelve thinking skills exist as first-class entities (`thinking_skills` table), each independently trackable. Every question maps to 0–N thinking skills via a join table, in addition to its math skill/topic mapping. MVP thinking skills: Pattern Recognition, Logical Reasoning, Estimation, Problem Decomposition, Strategy Selection, Error Detection.

Thinking skills are **learning/performance indicators**, never presented as IQ or innate-ability measures.

---

## 6. Learning Loop

```text
Discover → Observe → Understand → Think → Choose Strategy
→ Attempt → Mistake/Success → Analyze → Retry → Explain
→ Generalize → Apply to New Problem
```

Every screen in the product — lesson, practice, boss challenge — is a station along this loop, not a bare question-answer-score cycle. The animation system (Section 12) exists specifically to make each stage of this loop *feel* distinct.

---

## 7. Question Engine

Supported types: standard calculation, pattern recognition, find-the-mistake, multiple-solution, estimation, strategy-selection, what-if, explain-your-reasoning, real-world, constraint, unfamiliar/transfer.

Question metadata model:
```text
id, questionText, questionType, difficulty, mathTopic, mathSkill,
thinkingSkills[], expectedTime, hints[], solution, alternativeSolutions[],
explanation, commonMistakes[], solutionStrategies[], relatedQuestions[],
prerequisites[]
```

Questions are data-driven and parameterized (templated) where appropriate, with generated instances validated by a math evaluation layer before being served.

---

## 8. Assessment Engine

New students take a diagnostic covering calculation, pattern recognition, logic, estimation, algebra, geometry, decomposition, error detection, strategy selection, and one unfamiliar problem. Output: an initial Mathematics Profile + Thinking Profile, which seeds the personalized learning path.

Assessment Mode disables the AI tutor's answer-giving capability (Section 21) — hints and step-by-step help are only available in Learning Mode.

---

## 9. Adaptive Learning Engine

Rule-based (MVP), transparent, explainable:

```text
High accuracy + low hint usage + efficient reasoning → increase difficulty
Repeated mistakes + high hint usage → reduce difficulty → check prerequisites → recommend review
```

Architected so a statistical/ML model can be swapped in later without changing the API surface consumed by the frontend.

---

## 10. Recommendation Engine

Combines math mastery, thinking skill mastery, prerequisites, recent mistakes, difficulty curve, hint usage, attempt history, and transfer performance to recommend: next lesson, next practice set, thinking challenge, review topic, or boss challenge.

---

## 11. Game Architecture

**World: The Thinking Realms**

```text
Central Hub
 ├── Number Forest
 ├── Logic City
 ├── Geometry World
 ├── Puzzle Lab
 ├── Strategy Tower
 └── Master's Realm
```

Progression hierarchy:
```text
World → Region → Chapter → Mission → Lesson → Practice
→ Thinking Challenge → Boss Challenge → Reward → Unlock
```

Boss challenges are reasoning challenges (multi-constraint, multi-strategy problems), evaluated on correctness, strategy, efficiency, and reasoning quality — not "combat."

Progression and rewards are designed around **personal-best comparisons** rather than leaderboards, consistent with the product's improvement-focused philosophy.

---

## 12. Motion & Animation Design System

Animation is a formally specified subsystem, owned by a dedicated design-system module, not left to individual components.

### 12.1 Motion Tokens

```text
Duration:
  instant    100ms        — button press, toggle
  micro      150–250ms    — hover, tap feedback, icon transitions
  standard   300–500ms    — panel open, card flip, route change
  reward     700–1200ms   — XP burst, correct-answer celebration
  cinematic  1000–2000ms  — world unlock, boss victory, level up
```

### 12.2 Easing

```text
entrance:  cubic-bezier(0.16, 1, 0.3, 1)     — fast start, gentle settle
exit:      cubic-bezier(0.7, 0, 0.84, 0)     — quick, unobtrusive
bounce:    spring(stiffness: 300, damping: 20) — playful, reward-only
linear:    progress bars and timers ONLY — never for UI transitions
```

### 12.3 Centralized Configuration

```text
frontend/src/design-system/motion/
  motion.ts          — exported Framer Motion transition presets
  motion.css         — CSS custom properties (--duration-*, --ease-*)
  reducedMotion.ts   — reduced-motion variants for every reward/cinematic preset
```

No component defines its own duration or easing curve without a documented exception recorded in the design system's changelog.

### 12.4 Signature Animations (formally specified)

**Correct Answer**
```text
Submit → element scale 1→1.08→1 (micro)
       → soft glow/color wash radiates and fades (standard)
       → 8–12 particles travel toward XP counter (reward)
       → XP counter rolls upward, synced to particle arrival
```
Sequenced, not simultaneous. Tone: satisfying, not childish.

**Wrong Answer**
```text
Soft 2–3px horizontal shake, 2 cycles, ~150ms
Relevant reasoning step highlighted in amber (not the whole question marked "wrong")
"Let's look at this together" prompt slides in from below
```
No red failure states, no punishment sound. Mistakes are treated as information.

**XP / Level Up**
```text
Normal: XP bar eases up, slight overshoot, settles
Level-up: bar flashes → briefly exceeds 100% → resets → badge flip-reveal
          → subtle screen-edge emphasis
```

**World / Region Unlock**
```text
Map → camera-style zoom/pan (CSS/SVG transform, not a real camera)
    → fog-of-war mask dissolves
    → region revealed
    → idle glow/pulse invites exploration
```

**Boss Challenge**
```text
Entry:   UI chrome fades → background intensifies → question panel rises from below
Victory: reasoning summary shown FIRST → then strategy/result → then rewards (XP, achievements)
```
Rewards are deliberately sequenced after comprehension, never dumped simultaneously.

### 12.5 Reduced Motion

Every reward-tier and cinematic-tier animation has a `prefers-reduced-motion` counterpart that preserves 100% of the *information* conveyed (e.g., "you leveled up") via fade/instant-state-change instead of motion. Reduced motion is never an excuse to hide state changes.

---

## 13. Reward Sequencing Architecture

A `RewardSequencer` is the single orchestrator for post-attempt feedback. It receives a queue of typed events from the backend attempt-evaluation response and plays them in deliberate order, never letting components animate independently and collide.

```text
Attempt submitted
   → backend evaluates (correctness, mastery deltas, rewards, achievements)
   → frontend receives an ordered RewardEvent[] payload
   → RewardSequencer plays: xpGained → achievementUnlocked → levelUp → regionUnlocked → rewardGranted
```

Each event type has a registered animation handler in the design system; the sequencer only controls timing/order, not the animation implementation itself. This keeps signature animations reusable outside of the reward flow too (e.g., XP animation also plays after a daily challenge).

No animation state is persisted — the sequencer is purely a frontend runtime concern, replayable from the same event payload.

---

## 14. Frontend Architecture

```text
frontend/
  src/
    design-system/
      motion/            (Section 12.3)
      tokens/            (color, spacing, type scale)
      components/        (Button, Card, GlassPanel, ProgressBar, etc.)
    features/
      auth/
      assessment/
      lessons/
      practice/
      thinking-challenges/
      game-map/
      missions/
      boss-challenges/
      dashboard/
      rewards/           (RewardSequencer + event animation handlers)
    shared/
      api/               (TanStack Query hooks)
      hooks/
      utils/
    app/
      router/
      providers/
```

Stack: React, TypeScript, Vite, Tailwind, React Router, TanStack Query, Framer Motion, tsParticles (particle bursts), Lottie/Rive (mascot & idle loops), SVG/Canvas for the world map.

---

## 15. Backend Architecture

Modular monolith, Java + Spring Boot.

```text
Controller → Service → Repository → Supabase PostgreSQL
```

Modules: `auth, student, curriculum, lesson, question, assessment, thinking, progress, recommendation, game, achievement, reward, analytics`.

Spring Security + JWT, Spring Data JPA/Hibernate, Bean Validation, Maven, OpenAPI/Swagger. DTOs at every API boundary — entities never exposed directly.

The reward/achievement calculation that feeds the `RewardSequencer` lives in the `reward` and `achievement` modules and returns an ordered event payload — the backend decides *what* happened; the frontend decides *how it looks*.

---

## 16. Supabase Architecture

- PostgreSQL as primary datastore
- Supabase Auth for identity (or backend-issued JWT if custom auth is preferred — decide in Phase 1)
- Supabase Storage for game assets (avatar cosmetics, Lottie/Rive files, region art)
- Row Level Security enforced per role (student can only read/write their own progress rows)
- Realtime reserved for future features only if genuinely needed (e.g., live teacher dashboards) — not used in MVP

---

## 17. Database ER Design (MVP scope)

```text
users, students, teachers
math_topics, math_subtopics, math_prerequisites
thinking_skills
lessons
questions, question_options, question_hints, question_solutions
question_math_skills, question_thinking_skills
assessments, assessment_questions, assessment_attempts
question_attempts
student_progress, skill_mastery, thinking_skill_mastery
learning_sessions
game_worlds, game_regions, game_levels, missions, challenges
achievements, student_achievements
rewards, student_rewards
recommendations
```

No animation/UI state is persisted — `RewardSequencer` state is ephemeral and rebuilt from the attempt/reward event payload each time. UUIDs used for all externally exposed IDs; internal sequential IDs never leave the backend.

Full column-level ER diagram to be produced in Phase 1 once auth/storage decisions are finalized.

---

## 18. API Architecture

RESTful, versioned, consistent status codes:

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/math/topics
GET  /api/math/topics/{id}
GET  /api/lessons/{id}
GET  /api/questions/{id}
POST /api/questions/{id}/attempt        → returns ordered RewardEvent[]
GET  /api/students/me/progress
GET  /api/students/me/thinking-profile
GET  /api/recommendations
GET  /api/game/worlds
GET  /api/game/missions/{id}
POST /api/game/missions/{id}/complete   → returns ordered RewardEvent[]
```

Attempt/completion endpoints are the contract point between backend reward logic and the frontend `RewardSequencer`.

---

## 19. Authentication & Security

Role-based access (STUDENT/TEACHER/ADMIN), Supabase RLS, input/API validation, rate limiting on attempt endpoints, secure password handling, secrets in environment variables only, no sensitive data shipped to frontend.

---

## 20. Analytics

Tracked per attempt: correctness, time, hints used, attempt count, difficulty, math skill, thinking skill, strategy selected, classified mistake type, transfer success. Feeds the recommendation engine; not used for animation decisions (animation responds to the reward event payload, not raw analytics).

---

## 21. AI Tutor Architecture

Secondary, isolated module. Available in Learning Mode for hints, explanations, and doubts; disabled from giving direct answers in Assessment Mode. Deterministic math validation never depends on the AI — it uses backend logic/math libraries. AI service is decoupled so the app functions if the provider is unavailable.

---

## 22. Accessibility

Keyboard navigation, screen-reader-friendly structure, adequate contrast, large-text option, and full `prefers-reduced-motion` support (Section 12.5) are treated as launch requirements, not follow-ups. No task can be blocked by an animation.

---

## 23. Responsive Design

Mobile-first. Motion timings and particle counts scale down on lower-powered devices (e.g., reduced particle count on mobile) without changing the *meaning* of the animation.

---

## 24. Testing Strategy

Backend: unit/service/repository/controller/integration tests, with explicit coverage of difficulty adjustment, mastery calculation, recommendation logic, answer validation, prerequisite detection, XP calculation, and achievement unlocking.

Frontend: component tests, user-flow tests, and dedicated tests for `RewardSequencer` ordering logic (given a shuffled event payload, does it always sequence xpGained → achievementUnlocked → levelUp → regionUnlocked → rewardGranted correctly, and does the reduced-motion path still convey full information).

---

## 25. Deployment Architecture

To be finalized in Phase 1: containerized Spring Boot backend, static frontend build (Vite output) on a CDN-friendly host, Supabase as managed backend platform. No infrastructure decisions locked yet.

---

## 26. MVP Scope

```text
Auth → Initial Assessment → Math + Thinking Profile → Personalized Path
→ Lesson → Practice → Thinking Challenge → Game Challenge
→ Evaluation → Progress → Recommendation
```

Math: Numbers, Arithmetic, Fractions, Percentages, Basic Algebra.
Thinking skills: Pattern Recognition, Logical Reasoning, Estimation, Problem Decomposition, Strategy Selection, Error Detection.
Game: Central Hub + Number Forest only, with one boss challenge.
Animation: full motion token system, Correct/Wrong Answer, XP/Level-up, and a simplified single-region unlock — boss cinematic and full map camera system deferred.

## 27. Future Expansion

Full curriculum through advanced mathematics; remaining regions (Logic City, Geometry World, Puzzle Lab, Strategy Tower, Master's Realm); AI tutor rollout; teacher/admin platforms; ML-based adaptive engine; avatar cosmetics and Lottie/Rive-driven mascot; realtime teacher dashboards; full boss cinematic sequences.

---

## What Changed Because of the Animation Requirements

- Added **Section 12 (Motion & Animation Design System)** and **Section 13 (Reward Sequencing Architecture)** as formal, numbered architecture sections — previously animation was a UI afterthought (old Sections 12–13).
- Introduced the **`RewardSequencer`** as a named architectural component with a defined contract: backend emits an ordered `RewardEvent[]`, frontend orchestrates timing.
- Formalized **motion tokens and easing curves** as a design-system artifact (`motion.ts`/`motion.css`) rather than implicit per-component choices.
- Elevated **`prefers-reduced-motion`** from a general accessibility note to a per-signature-animation requirement with defined fallback behavior.
- Updated the **API contract** (Section 18) so attempt/completion endpoints explicitly return ordered reward events, not just correctness booleans.
- Added a testing requirement specifically for **sequencer ordering and reduced-motion information parity** (Section 24).
- Confirmed no animation/UI state is persisted to the database — kept the Section 17 ER design unchanged except for this explicit note.

## What Will Be Created Later (Not Now)

- `frontend/src/design-system/motion/` implementation files
- `RewardSequencer` component and event handler registry
- Particle system integration (tsParticles)
- Lottie/Rive mascot and idle-loop assets
- World map SVG/Canvas camera system
- Boss challenge cinematic sequence
- AI tutor module
- Advanced mathematics curriculum content
- Teacher/admin dashboards

## What Should Be Built First

Per Section 26/Phase 1: project setup, Supabase connection, authentication, `users`/`students` tables, and a minimal UI shell — **before** any signature animation is implemented, so there's a real attempt-submission flow for the `RewardSequencer` to eventually attach to.

## What Should NOT Be Built in the MVP

- Full 12-thinking-skill coverage (start with 6)
- Full curriculum beyond Numbers/Arithmetic/Fractions/Percentages/Basic Algebra
- More than one game region and one boss challenge
- AI tutor
- ML-based adaptive engine
- Teacher/admin platforms beyond basic role scaffolding
- Full cinematic boss/world-unlock sequences (simplified versions only)

---

Waiting for your instruction: **"Start Phase 1"**
