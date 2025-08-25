#!/bin/bash

echo "Generating test coverage reports for NZHikes project..."

# Enable test coverage
export GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx2048m"

# Generate coverage for core:ui module
echo "Generating coverage for core:ui module..."
./gradlew :core:ui:testDebugUnitTestCoverage

# Generate coverage for core:data module
echo "Generating coverage for core:data module..."
./gradlew :core:data:testDebugUnitTestCoverage

# Generate coverage for feature:explore module
echo "Generating coverage for feature:explore module..."
./gradlew :feature:explore:testDebugUnitTestCoverage

# Generate coverage for feature:trips module
echo "Generating coverage for feature:trips module..."
./gradlew :feature:trips:testDebugUnitTestCoverage

# Generate coverage for feature:me module
echo "Generating coverage for feature:me module..."
./gradlew :feature:me:testDebugUnitTestCoverage

# Generate coverage for app module
echo "Generating coverage for app module..."
./gradlew :app:testDebugUnitTestCoverage

# Generate overall coverage report
echo "Generating overall coverage report..."
./gradlew testDebugUnitTestCoverage

echo "Coverage reports generated!"
echo "Reports can be found in:"
echo "- core/ui/build/reports/coverage/debug/index.html"
echo "- core/data/build/reports/coverage/debug/index.html"
echo "- feature/explore/build/reports/coverage/debug/index.html"
echo "- feature/trips/build/reports/coverage/debug/index.html"
echo "- feature/me/build/reports/coverage/debug/index.html"
echo "- app/build/reports/coverage/debug/index.html"
