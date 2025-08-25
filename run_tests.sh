#!/bin/bash

echo "Running all unit tests for NZHikes project..."

# Run tests for core:ui module
echo "Running tests for core:ui module..."
./gradlew :core:ui:test

# Run tests for core:data module
echo "Running tests for core:data module..."
./gradlew :core:data:test

# Run tests for feature:explore module
echo "Running tests for feature:explore module..."
./gradlew :feature:explore:test

# Run tests for feature:trips module
echo "Running tests for feature:trips module..."
./gradlew :feature:trips:test

# Run tests for feature:me module
echo "Running tests for feature:me module..."
./gradlew :feature:me:test

# Run tests for app module
echo "Running tests for app module..."
./gradlew :app:test

# Run all tests together
echo "Running all tests together..."
./gradlew test

echo "All tests completed!"
