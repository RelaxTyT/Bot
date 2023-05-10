package io.ErrorSpringBot1.ErrorBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//Геттеры и сеттеры для заданны полей
@Getter
@Setter
        //Если честно, не особо понимаю что за фигня. Но знаю что она связана с БД P/S Исправлюсь -_-
@Entity(name = "adsTable")
public class Ads {
    // вот тут попроще! Тут у нас первичный ключик
    @Id
            //Что то делается само... Тоже не особо освоил... Подсмотрел!
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ad;
}
