# GlobalPath: International Internship Application Tracker

**AII003 | Principles of Software Engineering | UML Design-to-Code Mapping**

A small, original Java object-oriented model inspired by the practical challenge of tracking international internship applications. The project focuses on **eligibility, visa sponsorship, deadlines, duplicate prevention, and controlled application status transitions**. All organizations and opportunities in the demo are fictional.

## UML diagram

See [the Mermaid class diagram](docs/class-diagram.md), which GitHub renders automatically.

## UML-to-Java mapping

| UML concept | Java implementation |
|---|---|
| Abstract class | `Opportunity` |
| Generalization / inheritance | `IndustryInternship` and `ResearchInternship` extend `Opportunity` |
| Polymorphism | Each internship subtype overrides `getCategory()` |
| Encapsulation | Private fields, public getters, unmodifiable lists |
| Aggregation | `Organization` stores externally created `Opportunity` instances |
| Composition | `ApplicationTracker` creates and owns `Application` records and an `EligibilityChecker` |
| Association | `Application` references an `Applicant` and an `Opportunity` |
| Enumeration | `ApplicationStatus` |
| Multiplicity | `Organization` holds zero or more opportunities; each application targets one opportunity |
| Constraints | Eligibility validation, duplicate detection, and status transition checks |

## Business rules

1. Applicants who require visa sponsorship cannot apply to opportunities that do not offer it.
2. Applications cannot be created after their deadlines.
3. The same tracker cannot apply to the same opportunity twice.
4. Status changes follow `DRAFT → SUBMITTED → INTERVIEW → OFFER/REJECTED`, with direct decisions from `SUBMITTED` also allowed.
5. Final decisions (`OFFER`, `REJECTED`) cannot be changed.

## Run (Java 17+)

```bash
javac -d out src/Main.java
java -cp out Main
```

## Example output

```text
GlobalPath | Demo Software Engineering Student
Summer AI Research -> INTERVIEW
History: [Created as DRAFT, Changed to SUBMITTED, Changed to INTERVIEW]
Blocked: Deadline passed or visa sponsorship unavailable
Blocked: Duplicate application
Interviews: 1
```

## Design note

This is an educational in-memory domain model, not a production recruitment platform. It does not connect to real internship websites or send notifications. The fixed date in the demo makes the example reproducible.
