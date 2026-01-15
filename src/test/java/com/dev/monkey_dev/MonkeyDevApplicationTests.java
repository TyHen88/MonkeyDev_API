package com.dev.monkey_dev;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration Test for Spring Boot Application
 * 
 * This test class demonstrates INTEGRATION TESTING vs UNIT TESTING:
 * 
 * INTEGRATION TEST:
 * - @SpringBootTest: Loads the full Spring application context
 * - Tests that all beans, configurations, and dependencies are wired correctly
 * - Verifies that the application can start successfully
 * - Slower than unit tests (loads entire Spring context)
 * - Tests the integration of multiple components together
 * 
 * UNIT TEST (see UserServiceImplTest):
 * - Tests individual classes in isolation
 * - Uses mocks to replace dependencies
 * - Faster execution
 * - Tests single unit of functionality
 * 
 * The contextLoads() test is a simple smoke test that verifies:
 * 1. Spring can load the application context
 * 2. All @Configuration classes are valid
 * 3. All @Component, @Service, @Repository beans can be created
 * 4. Database connections and other infrastructure are configured correctly
 * 
 * If this test fails, it means there's a configuration problem preventing
 * the application from starting, which is caught early in the development
 * cycle.
 */
@SpringBootTest
class MonkeyDevApplicationTests {

	/**
	 * Context Loads Test - Integration Test
	 * 
	 * This test verifies that the Spring Boot application context loads
	 * successfully.
	 * 
	 * What happens when this test runs:
	 * 1. Spring Boot starts the application context
	 * 2. All @Configuration classes are processed
	 * 3. All @Component, @Service, @Repository beans are created and wired
	 * 4. Database connections are established (if configured)
	 * 5. Security configuration is loaded
	 * 6. If any of these fail, the test fails
	 * 
	 * The test itself doesn't need to do anything - if the context loads,
	 * the test passes. If there's a configuration error, Spring will throw
	 * an exception and the test will fail.
	 * 
	 * This is different from unit tests which test individual methods in isolation.
	 */
	@Test
	void contextLoads() {
		// If we reach this point, the Spring context loaded successfully!
		// No assertion needed - if context loading fails, Spring throws an exception
		// and the test fails automatically.
	}

}
