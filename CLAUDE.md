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


## Project Structure

The project is a multi-module sbt build:

- `root`: Aggregator project
- `lib`: Core library module containing the implementation (in `lib/` directory)

All source code lives in `lib/src/main/scala/` and tests in `lib/src/test/scala/`.

### Directory Organization

**IMPORTANT**: Keep the directory structure flat and linear. Do not create nested subdirectories within `lib/src/main/scala/jsonschema/`.

- ✅ All Scala files should be directly in `lib/src/main/scala/jsonschema/`
- ❌ Do not create nested directories like `jsonschema/derivation/`, `jsonschema/core/`, etc.

This project prefers a flat structure for simplicity and ease of navigation.

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

## Scala 3 Syntax Conventions

Use modern Scala 3 optional braces syntax throughout — `:` instead of `{}` for traits, classes, objects, defs, and tests. Scalafmt enforces this automatically via `newSyntax` rewrite rules. Braces are still used for map/collection literals and match expressions.

## Commit Message Convention

All commit messages should start with a lowercase letter (e.g., "add feature" not "Add feature").

Examples:
- ✅ `add support for Option types`
- ✅ `fix constraint extraction in macros`
- ❌ `Add support for Option types`
- ❌ `Fix constraint extraction in macros`
