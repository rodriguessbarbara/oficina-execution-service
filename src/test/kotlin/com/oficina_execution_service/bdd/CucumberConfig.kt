package com.oficina_execution_service.bdd

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

@CucumberContextConfiguration
@ContextConfiguration(classes = [CucumberTestConfiguration::class])
class CucumberConfig

@Configuration
class CucumberTestConfiguration
