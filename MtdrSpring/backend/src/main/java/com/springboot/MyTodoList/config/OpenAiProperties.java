package com.springboot.MyTodoList.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.util.StringUtils;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:openai.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${OPENAI_CONFIG_PATH:config/openai.properties}", ignoreResourceNotFound = true)
})
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProperties.class);

    /**
     * API key used to authenticate against OpenAI.
     * Expected to arrive via config/openai.properties (gitignored) or env vars.
     */
    private String apiKey;

    /** Chat completion model id (defaults to gpt-4o-mini). */
    private String model = "gpt-4o-mini";

    /** Base URL in case a compatible proxy/service is used. */
    private String baseUrl = "https://api.openai.com/v1";

    private double temperature = 0.2;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @PostConstruct
    public void hydrateFromEnv() {
        if (!StringUtils.hasText(apiKey)) {
            String envKey = System.getenv("OPENAI_API_KEY");
            if (!StringUtils.hasText(envKey)) {
                envKey = System.getProperty("OPENAI_API_KEY");
            }
            if (StringUtils.hasText(envKey)) {
                this.apiKey = envKey;
                log.debug("OpenAI API key inyectada desde variable de entorno.");
            }
        } else {
            log.debug("OpenAI API key cargada desde archivo externo.");
        }
    }

    public boolean isConfigured() {
        return StringUtils.hasText(apiKey);
    }
}
