# Contributing to PhonePe PG SDK Java

👍🎉 First off, thanks for taking the time to contribute! 🎉👍

The following is a set of guidelines for contributing to the PhonePe PG SDK for Java. These are just guidelines, not rules — use your best judgment and feel free to propose changes to this document in a pull request.

---

### Table of Contents

- [Issues](#issues)
- [Pull Requests](#pull-requests)
- [Code Style](#code-style)
- [Testing](#testing)
- [Developer Certificate of Origin (DCO)](#developer-certificate-of-origin-dco)
- [Further Reading](#further-reading)

---

## Issues

Issues are created [here](https://github.com/PhonePe/phonepe-pg-sdk-java/issues).

When opening an issue, please provide as much context as possible. For bug reports, include the SDK version, Java version, environment (SANDBOX or PRODUCTION), and steps to reproduce. For feature requests, describe the use case and proposed solution.

If an issue has been closed and you still feel it's relevant, feel free to add a comment.

---

## Pull Requests

Pull Requests are the way concrete changes are made to the code, documentation, and tools contained in this repository.

### Setting Up Your Local Environment

**Step 1: Fork and Clone**

Fork the repository on GitHub, then clone your fork locally:

```bash
git clone https://github.com/<your-username>/phonepe-pg-sdk-java.git
```

```bash
cd phonepe-pg-sdk-java
```

Add the upstream remote:

```bash
git remote add upstream https://github.com/PhonePe/phonepe-pg-sdk-java.git
```

**Step 2: Install Pre-commit Hooks**

This project uses [pre-commit](https://pre-commit.com/) hooks to enforce code formatting. Install them before making any commits:

```bash
pre-commit install
```

**Step 3: Build**

Make sure the project builds successfully before making changes:

```bash
mvn clean verify
```

### Making Changes

**Step 4: Branch**

Create a new branch from `main` for your changes. Use a descriptive name with a prefix:

- `feat/` — for new features
- `fix/` — for bug fixes
- `docs/` — for documentation changes

```bash
git checkout -b feat/your-feature-name
```

**Step 5: Code**

Make your changes. Keep commits focused — one logical change per commit.

**Step 6: Format**

Run the Spotless formatter before committing:

```bash
mvn spotless:apply
```

> The pre-commit hooks will also run this automatically, but it's good practice to run it manually first.

**Step 7: Test**

Run the full test suite to make sure nothing is broken:

```bash
mvn clean verify
```

> All new code must include tests. We use JUnit 5 for unit tests and WireMock for HTTP mocking.

**Step 8: Commit**

Write clear and descriptive commit messages. We recommend using these prefixes:

| Prefix | Use for |
|--------|---------|
| `feat:` | New features |
| `fix:` | Bug fixes |
| `docs:` | Documentation changes |
| `chore:` | Maintenance tasks |
| `test:` | Adding or updating tests |

All commits must include a DCO sign-off (see [DCO section](#developer-certificate-of-origin-dco)):

```bash
git commit -s -m "docs: add CONTRIBUTING.md with DCO guidelines"
```

**Step 9: Push**

Push your branch to your fork:

```bash
git push origin feat/your-feature-name
```

**Step 10: Open a Pull Request**

Open a Pull Request against `main` on the upstream repository. In your PR description:

- Describe what the PR does and why
- Link the related issue (e.g., `Closes #39`)
- Make sure all CI checks pass (SonarCloud, Spotless)

---

## Code Style

| Rule | Detail |
|------|--------|
| Indentation | Tabs, not spaces |
| Formatter | Eclipse formatter via Spotless (`formatter.xml`) |
| Auto-format | `mvn spotless:apply` |
| Verify only | `mvn spotless:check` |

> The pre-commit hooks run both `spotless:apply` and `spotless:check` automatically on every commit.

---

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit tests |
| WireMock | HTTP mocking |
| JaCoCo | Code coverage |
| SonarCloud | Quality reporting |

Run tests locally:

```bash
mvn test
```

---

## Developer Certificate of Origin (DCO)

By contributing to this project, you agree that your contributions are your own work and you have the right to submit them under the Apache 2.0 license.

We require all commits to be signed off using the DCO. Use the `-s` flag when committing:

```bash
git commit -s -m "feat: add new payment method support"
```

This adds a line like:

```
Signed-off-by: Your Name <your.email@example.com>
```

Make sure your git user config is set correctly:

```bash
git config user.name "Your Name"
```

```bash
git config user.email "your.email@example.com"
```

---

## Further Reading

- [PhonePe Developer Documentation](https://developer.phonepe.com/)
- [Standard Checkout SDK Reference](https://developer.phonepe.com/v1/reference/java-sdk-standard-checkout)
- [Subscription SDK Reference](https://developer.phonepe.com/v1/reference/java-sdk-introduction-autopay)
