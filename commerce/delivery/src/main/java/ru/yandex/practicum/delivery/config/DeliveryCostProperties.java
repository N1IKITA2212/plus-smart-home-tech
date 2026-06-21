package ru.yandex.practicum.delivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "delivery.cost")
@Getter
@Setter
public class DeliveryCostProperties {

    private double baseRate = 5.0;
    private double address1Multiplier = 1.0;
    private double address2Multiplier = 2.0;
    private double fragileMultiplier = 0.2;
    private double weightMultiplier = 0.3;
    private double volumeMultiplier = 0.2;
    private double addressMultiplier = 0.2;
}
