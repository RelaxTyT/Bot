package io.ErrorSpringBot1.ErrorBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
//Тут настраивается подключение и передача
//Для пометки как класс который относиться к Конфигурационному
@Configuration
        //Обычный планировщик задач.
@EnableScheduling
        //Великий и могучий. Генерирует за вас стандартные методы.
@Data
        //Указывает расположение кфг.
@PropertySource("application.properties")
public class BotConfig {
    //cюда мы передаём бот.имя
    @Value("${bot.name}")
    String botName;
    //сюда мы передаём бот.токен(ключик)
    @Value("${bot.token}")
    String token;

    @Value("${bot.owner}")
    Long ownerId;
}
