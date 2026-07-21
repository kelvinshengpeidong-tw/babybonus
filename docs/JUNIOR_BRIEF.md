# Baby Bonus Enrollment Service — Software Engineer Assessment

## Background

The Baby Bonus Scheme supports parents of newborn Singapore Citizens with two financial benefits:

- A **cash gift** paid directly to the parent's bank account
- A **Child Development Account (CDA)** — a ring-fenced savings account seeded by government top-ups, usable only at approved merchants

Parents apply to enrol their newborn child. The system checks eligibility against government sources of truth, creates an enrollment record, and initiates disbursement.

---

## Scenario

You are building a **Baby Bonus Enrollment Service** — a Kotlin/Spring Boot backend service that handles enrollment applications.

You are provided with mock data representing two external sources of truth:

- **ICA (Immigration & Checkpoints Authority)** — citizenship and identity records for children
- **IROAS** — identity records for parents and guardians

You do **not** need to integrate with real external APIs. Load the provided mock data files as in-memory stubs.

---

## Domain Model

```
Child
  nric:         String        // Singapore NRIC (e.g. T2400001A)
  name:         String
  dateOfBirth:  LocalDate
  citizenship:  SINGAPORE_CITIZEN | PERMANENT_RESIDENT | FOREIGNER

Parent / Guardian
  nric:         String
  name:         String
  relationship: FATHER | MOTHER | LEGAL_GUARDIAN

Enrollment
  id:           UUID
  childNric:    String
  parentNric:   String
  status:       PENDING | ENROLLED | INELIGIBLE
  enrolledAt:   Instant?
  createdAt:    Instant

Disbursement
  id:           UUID
  enrollmentId: UUID
  type:         CASH_GIFT | CDA_DEPOSIT
  amount:       BigDecimal
  status:       PENDING | PROCESSED | FAILED
  processedAt:  Instant?
```

---

## Eligibility Rules

A child is eligible for enrolment if **all** of the following are true:

1. The child exists in ICA records
2. The child is a **Singapore Citizen**
3. The parent/guardian exists in IROAS records
4. The child has **not** been previously enrolled

Upon successful enrolment, initiate a **cash gift disbursement of $3,000**.

---

## Mock Data

Mock data is provided in `mock-data/`:

- `mock-data/ica_children.json` — child identity and citizenship records
- `mock-data/iroas_parents.json` — parent/guardian identity records

Your service should load these files at startup and use them to simulate calls to ICA and IROAS.

---

## Requirements

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/enrollments` | Submit an enrollment application |
| `GET` | `/api/v1/enrollments/{id}` | Retrieve enrollment status and disbursement details |

**POST /api/v1/enrollments** — request body:
```json
{
  "childNric": "T2400001A",
  "parentNric": "S8001234A"
}
```

**GET /api/v1/enrollments/{id}** — example response:
```json
{
  "id": "a1b2c3d4-...",
  "childNric": "T240****A",
  "status": "ENROLLED",
  "enrolledAt": "2024-08-01T10:00:00Z",
  "disbursement": {
    "type": "CASH_GIFT",
    "amount": 3000.00,
    "status": "PROCESSED"
  }
}
```

### Technical Requirements

- **Framework**: Kotlin with Spring Boot
- **Persistence**: In-memory H2 database (no external database setup required)
- **Error handling**: Return appropriate HTTP status codes with meaningful, non-implementation-leaking error messages
- **Tests**: Cover the service layer at minimum (happy path + key error cases)
- **README.md**: Setup instructions, how to run, assumptions made, and a "What I would do next" section noting known gaps or next priorities
- **AI_USAGE.md**: Document which AI tools you used, what you used them for, how you reviewed and validated the output, and any cases where you discarded AI-generated code. You will be asked about this in the debrief — uncritical over-reliance is a red flag; thoughtful, verified use is a strength

---

## Submission

- Submit via a **GitHub repository** (public, or shared with your reviewers)
- Include a `README.md` at the root with clear setup and run instructions
- **Commit incrementally** — your commit history is part of the assessment; a single large commit is a red flag
- Do not include: real PII or NRIC data beyond the provided mock set

**Before you submit, confirm you have:**
- [ ] Service runs with the steps in your README
- [ ] `AI_USAGE.md` completed honestly
- [ ] "What I would do next" section in your README
- [ ] No secrets or credentials committed

**Note on honesty:** You will be asked about your choices in a debrief. We value candidates who understand the trade-offs they made — including where they cut corners — over candidates who submit more code but cannot explain it. If something is mocked, say so. If AI wrote something you are not fully confident in, say so. Clarity and self-awareness are strengths.

---

## What We Value

- **Clarity over cleverness**: Simple, readable code beats impressive abstractions
- **Security by default**: Treat NRIC and financial data as sensitive throughout — not as an afterthought
- **Judgment under constraints**: You will not be able to complete everything perfectly. Show us how you prioritise and what trade-offs you consciously make
- **Communication**: We should be able to run and understand your service without asking you questions
