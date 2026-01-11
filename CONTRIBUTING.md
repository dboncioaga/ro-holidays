# Contributing to ro-holidays

Thank you for your interest in contributing to the Romanian Holidays library!

## Code of Conduct

Please be respectful and professional in all interactions.

## How to Contribute

### Reporting Bugs

- Check if the issue already exists in [GitHub Issues](https://github.com/dboncioaga/ro-holidays/issues)
- Provide a clear description and reproduction steps
- Include Java version, library version, and relevant code snippets

### Suggesting Features

- Open an issue describing the feature and its use case
- Explain how it aligns with the library's goals
- Be open to discussion and feedback

### Submitting Changes

1. **Fork the repository**

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes following these guidelines:**
   - Follow existing code style and conventions
   - Write clear, descriptive JavaDoc for public APIs
   - Add comprehensive unit tests
   - Ensure all tests pass: `mvn test`
   - Update documentation if needed

4. **Commit your changes**
   ```bash
   git commit -m "Add feature: description"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Open a Pull Request**
   - Provide a clear description of the changes
   - Reference any related issues
   - Ensure CI checks pass

## Development Setup

### Prerequisites

- Java 25 or higher
- Maven 3.8+
- Git

### Building

```bash
git clone https://github.com/dboncioaga/ro-holidays.git
cd ro-holidays
mvn clean verify
```

### Running Tests

```bash
mvn test
```

### Code Coverage

```bash
mvn verify
# Reports are in target/site/jacoco/index.html
```

## Coding Standards

### General

- Use Java 25 features appropriately
- Follow immutability principles where possible
- Ensure thread safety after initialization
- Write defensive code with proper validation
- No Lombok - use explicit constructors and getters

### Documentation

- All public APIs must have JavaDoc
- Include parameter descriptions and return values
- Document exceptions and edge cases
- Provide usage examples for complex features

### Testing

- Use JUnit 5
- Test happy paths and edge cases
- Test error handling and validation
- Aim for high code coverage
- Use descriptive test method names

### Holiday Data

- All holiday information must be sourced from official Romanian legal documents
- Include references in code comments or documentation
- Do not invent or guess holiday dates
- Verify movable holidays against official calendars

## Project Structure

```
ro-holidays/
├── ro-holidays-core/       # Core library (no Spring dependencies)
│   ├── src/main/java/
│   ├── src/main/resources/ # Holiday data (YAML)
│   └── src/test/java/
└── ro-holidays-spring/     # Spring Boot integration
    ├── src/main/java/
    └── src/test/java/
```

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.

## Questions?

Open an issue or start a discussion on GitHub.
