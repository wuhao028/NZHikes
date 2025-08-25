# Testing Guide for NZHikes

This document provides information about the unit tests implemented in the NZHikes project.

## Overview

The project includes comprehensive unit tests for all major components:

- **Data Models**: Testing data validation, formatting, and business logic
- **Repositories**: Testing data access layer and business logic
- **ViewModels**: Testing UI state management and user interactions
- **Network Layer**: Testing API service configuration and data transformation
- **Navigation**: Testing navigation components and routing
- **Theme Management**: Testing theme switching and persistence

## Test Structure

### Core Modules

#### `core:ui`
- **ThemeManagerTest**: Tests theme switching, persistence, and DataStore integration
- **CommonComponentsTest**: Tests reusable UI components

#### `core:data`
- **LocalTrackTest**: Tests data model validation and utility methods
- **HikeRepositoryTest**: Tests repository operations and data flow
- **NetworkModuleTest**: Tests network configuration and dependency injection
- **CampsiteRepositoryTest**: Tests campsite data operations
- **HutRepositoryTest**: Tests hut data operations

### Feature Modules

#### `feature:explore`
- **HomeViewModelTest**: Tests home screen state management and data loading
- **SearchViewModelTest**: Tests search functionality and result filtering
- **CampsiteDetailViewModelTest**: Tests campsite detail screen logic
- **SearchResultTest**: Tests search result data models

#### `feature:trips`
- **TripsViewModelTest**: Tests trips management functionality

#### `feature:me`
- **MeViewModelTest**: Tests user profile and settings

### App Module

#### `app`
- **BottomNavItemTest**: Tests navigation configuration
- **MainActivityTest**: Tests main activity lifecycle

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests for Specific Module
```bash
# Core UI tests
./gradlew :core:ui:test

# Core Data tests
./gradlew :core:data:test

# Feature Explore tests
./gradlew :feature:explore:test

# Feature Trips tests
./gradlew :feature:trips:test

# Feature Me tests
./gradlew :feature:me:test

# App tests
./gradlew :app:test
```

### Run Tests with Coverage
```bash
# Generate coverage reports
./gradlew testDebugUnitTestCoverage

# View coverage reports
open app/build/reports/coverage/debug/index.html
```

### Using Test Scripts
```bash
# Run all tests
./run_tests.sh

# Generate coverage reports
./test_coverage.sh
```

## Test Dependencies

The project uses the following testing libraries:

- **JUnit 4**: Core testing framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Testing library for Kotlin Flows
- **Robolectric**: Android framework testing
- **Coroutines Test**: Testing coroutines and async code
- **Room Testing**: Database testing utilities

## Test Patterns

### Given-When-Then Structure
All tests follow the Given-When-Then pattern for clarity:

```kotlin
@Test
fun `test description`() = runTest {
    // Given - Setup test data and mocks
    val testData = createTestData()
    coEvery { mockRepository.getData() } returns testData

    // When - Execute the method under test
    viewModel.loadData()

    // Then - Verify the expected behavior
    assertEquals(expectedValue, actualValue)
}
```

### Mocking Strategy
- Use MockK for mocking dependencies
- Mock external dependencies (API, database, preferences)
- Test business logic with real data models
- Use relaxed mocks for complex objects when appropriate

### Coroutines Testing
- Use `runTest` for testing suspend functions
- Use `StandardTestDispatcher` for controlling coroutine execution
- Test Flow emissions with Turbine

### Error Handling
- Test both success and failure scenarios
- Verify error messages and exception types
- Test graceful degradation when external services fail

## Coverage Goals

Target coverage percentages:
- **Data Models**: 95%+
- **Repositories**: 90%+
- **ViewModels**: 85%+
- **Network Layer**: 80%+
- **Overall Project**: 85%+

## Best Practices

1. **Test Naming**: Use descriptive test names that explain the scenario
2. **Test Isolation**: Each test should be independent and not affect others
3. **Mock External Dependencies**: Don't test external libraries, mock them
4. **Test Edge Cases**: Include tests for null values, empty collections, and error conditions
5. **Keep Tests Simple**: Each test should verify one specific behavior
6. **Use Test Data Builders**: Create helper functions for generating test data
7. **Test Public API**: Focus on testing public methods and observable behavior

## Continuous Integration

Tests are automatically run on:
- Pull request creation
- Code push to main branch
- Release builds

## Troubleshooting

### Common Issues

1. **Test Timeout**: Increase timeout for long-running tests
2. **Mock Verification**: Ensure mocks are properly configured
3. **Coroutine Testing**: Use appropriate test dispatchers
4. **Database Tests**: Use in-memory database for testing

### Debugging Tests

```bash
# Run tests with debug output
./gradlew test --info

# Run specific test class
./gradlew test --tests "com.hao.explore.HomeViewModelTest"

# Run specific test method
./gradlew test --tests "com.hao.explore.HomeViewModelTest.loadData should update state"
```

## Contributing

When adding new features:
1. Write tests first (TDD approach)
2. Ensure all tests pass
3. Maintain or improve coverage
4. Update this documentation if needed

## Resources

- [JUnit 4 Documentation](https://junit.org/junit4/)
- [MockK Documentation](https://mockk.io/)
- [Turbine Documentation](https://github.com/cashapp/turbine)
- [Android Testing Guide](https://developer.android.com/training/testing)
