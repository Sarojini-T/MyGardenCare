package com.sarojini.MyGardenCare;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"application.security.jwt.secret-key=505E625377956A586E3272357538782F413F4428472B4B6250645367566B5970",
		"API_KEY_ID=dummy-api-key",
		"API_KEY_SECRET=dummy-secret"
})
class MyGardenCareApplicationTests {

	@Test
	void contextLoads() {
	}

}
