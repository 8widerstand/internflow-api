package com.internflow.api.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "prod"})
public class UserSeederProfileIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void demoUserSeederShouldNotBeLoadedInProduction() {
        assertThat(applicationContext.getBeansOfType(UserSeeder.class)).isEmpty();
    }
}
