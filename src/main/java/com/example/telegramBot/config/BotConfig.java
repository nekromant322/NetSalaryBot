package com.example.telegramBot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application.properties")
public class BotConfig {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.key}")
    private String botKey;
}
