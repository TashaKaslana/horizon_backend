# GitHub Copilot Instructions

## Code Style

- Write clean, maintainable code following best practices
- Add comments only for complex logic or ambiguous sections
- Keep documentation concise and focused on the "why" rather than the "what"
- Follow consistent naming conventions

## Feature Development Process

1. Implement features sequentially (not in parallel)
2. For each feature, follow this order:
   - Create/modify models/entities first
   - Implement repositories/data access layers
   - Develop services with business logic
   - Build controllers/APIs
   - Add necessary tests

## Error Handling

- Validate all implementations with appropriate error checking
- Fix identified errors before moving to the next feature
- Suggest proper exception handling where appropriate

## External Resources

- Use Context7 to access the latest documentation for libraries, frameworks, and languages
- Reference GitHub repositories when examples would be helpful
- Recommend appropriate dependencies when needed

## Additional Guidelines

- Favor Spring Boot best practices for backend development
- Use Flyway migrations appropriately for database changes
- Consider existing project structure when integrating new features
- Provide reasoning for architectural decisions
