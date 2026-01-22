# Contributing to Travel Companion

Thank you for your interest in contributing to Travel Companion! This document provides guidelines for contributing to the project.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:

   ```bash
   git clone https://github.com/YOUR_USERNAME/travel-companion.git
   cd travel-companion
   ```

3. **Create a feature branch**:

   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

1. Open the project in Android Studio
2. Add your Google Maps API key to `gradle.properties`:

   ```properties
   MAPS_API_KEY=your_api_key_here
   ```

3. Sync Gradle and build the project
4. Run the app on an emulator or physical device

## Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused (Single Responsibility Principle)
- Use proper indentation (4 spaces)

## Architecture Guidelines

- Follow MVVM architecture pattern
- Use Repository pattern for data access
- Implement dependency injection with Hilt
- Use Kotlin Coroutines for asynchronous operations
- Keep UI logic separate from business logic

## Testing

- Write unit tests for ViewModels and repositories
- Write instrumented tests for database operations
- Ensure all tests pass before submitting PR:

  ```bash
  ./gradlew test
  ./gradlew connectedAndroidTest
  ```

## Commit Messages

Follow the conventional commits format:

- `feat: add new feature`
- `fix: resolve bug`
- `docs: update documentation`
- `style: format code`
- `refactor: restructure code`
- `test: add tests`
- `chore: update dependencies`

Example:

```
feat: add trip export functionality

- Implement CSV export for trip data
- Add export button to settings
- Include photos and notes in export
```

## Pull Request Process

1. **Update documentation** if needed
2. **Add tests** for new features
3. **Ensure all tests pass**
4. **Update CHANGELOG.md** with your changes
5. **Create a Pull Request** with a clear description:
   - What changes were made
   - Why these changes are needed
   - Any breaking changes
   - Screenshots (if UI changes)

## Code Review

- Be respectful and constructive
- Address all review comments
- Keep PRs focused and reasonably sized
- Be patient - reviews take time

## Reporting Issues

When reporting bugs, please include:

- Android version
- Device model
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots or logs (if applicable)

## Feature Requests

For feature requests, please describe:

- The problem you're trying to solve
- Proposed solution
- Alternative solutions considered
- Additional context

## Questions?

Feel free to open an issue with the `question` label if you need help.

Thank you for contributing! 🎉
