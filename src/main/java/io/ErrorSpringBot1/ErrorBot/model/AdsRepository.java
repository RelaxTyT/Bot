package io.ErrorSpringBot1.ErrorBot.model;

import lombok.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;



public interface AdsRepository extends CrudRepository<Ads, Long> {
    
}
