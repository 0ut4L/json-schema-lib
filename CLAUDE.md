# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Scala 3 library for compile-time generation of JSON schemas from case classes. The project uses:

- Scala 3.7.3
- Circe for JSON handling (v0.14.15)
- Cats for functional programming (v2.13.0)
- MUnit for testing (v1.2.1)

## Mandatory Workflow

When making changes to this codebase, **ALWAYS** follow these steps:

### 1. Make Your Code Changes
Edit or create the necessary Scala files.

### 2. Run Code Quality Tools
**REQUIRED** - Run both formatting and linting tools:

```bash
sbt scalafmtAll scalafixAll
```

This command:
- `scalafmtAll` - Formats all Scala code (main + test) according to `.scalafmt.conf`
- `scalafixAll` - Runs scalafix rules (OrganizeImports, etc.) according to `.scalafix.conf`

**Never skip this step**. Code must be properly formatted before committing.

### 3. Update Documentation
**REQUIRED** - Update `README.md` whenever:
- Adding new features
- Changing public APIs
- Adding new constraint annotations
- Adding support for new types
- Changing usage patterns

Keep the README examples up-to-date and accurate.

### 4. Run Tests
Verify your changes work:

```bash
sbt test
```

Ensure all tests pass before considering the work complete.

### Complete Workflow Example

```bash
# 1. Make code changes (edit files)

# 2. Format and lint
sbt scalafmtAll scalafixAll

# 3. Update README.md (if needed)

# 4. Test
sbt test

# 5. Commit (if requested by user)
```

### Why This Matters

- **scalafmtAll** - Ensures consistent code style across the project
- **scalafixAll** - Catches common issues and organizes imports properly
- **README updates** - Keeps documentation in sync with code
- **Testing** - Verifies nothing broke

### Additional Notes

- Always use `sbt scalafmtAll` (not just `scalafmt`) to format both main and test code
- Always use `sbt scalafixAll` (not just `scalafix`) to lint both main and test code
- If the user explicitly says "don't format", skip the formatting step only for that specific change
- Documentation updates should be comprehensive and include code examples

## Project Structure

The project is a multi-module sbt build:

- `root`: Aggregator project
- `lib`: Core library module containing the implementation (in `lib/` directory)

All source code lives in `lib/src/main/scala/` and tests in `lib/src/test/scala/`.

## Build Commands

### Compilation

```bash
sbt compile              # Compile the project
sbt lib/compile          # Compile only the lib module
sbt test:compile         # Compile test sources
```

### Testing

```bash
sbt test                 # Run all tests
sbt lib/test             # Run tests for lib module
sbt testOnly <ClassName> # Run a specific test class
```

### Code Quality

```bash
sbt scalafmtAll          # Format all code (main + test)
sbt scalafmtCheck        # Check formatting without modifying files
sbt scalafixAll          # Run scalafix rules (OrganizeImports)
sbt scalafixAll --check  # Check scalafix without modifying
```

### Development

```bash
sbt dependencyUpdates    # Check for dependency updates
sbt clean                # Clean build artifacts
sbt ~compile             # Watch mode - recompile on file changes
```

## Code Style

The project enforces strict formatting and linting:

### Scalafmt Configuration (.scalafmt.conf)

- Scala 3 syntax with new syntax conversions enabled
- Optional braces removed where possible
- Max column width: 102
- ASCII sorted imports
- Asterisk-style docstrings

### Scalafix Configuration (.scalafix.conf)

- OrganizeImports rule enabled with unused import removal
- Targets Scala 3 dialect

## Commit Message Convention

All commit messages should start with a lowercase letter (e.g., "add feature" not "Add feature").

Examples:
- ✅ `add support for Option types`
- ✅ `fix constraint extraction in macros`
- ❌ `Add support for Option types`
- ❌ `Fix constraint extraction in macros`

## SBT Configuration

The project uses several SBT plugins:

- `sbt-tpolecat`: Sensible scalac options
- `sbt-scalafmt`: Code formatting
- `sbt-scalafix`: Linting and refactoring
- `sbt-updates`: Dependency update checking

The build is configured with `semanticdbEnabled := true` for IDE support and code analysis tools.
