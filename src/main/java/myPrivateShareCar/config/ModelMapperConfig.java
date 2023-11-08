package myPrivateShareCar.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// REVIEW: использование model mapper - это хорошо. Могут спросить, какие ещё библиотеки знаешь и/или почему выбрал её.
// Как самая известная альтернатива map struct. Можешь почитать, потом обсудим
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
