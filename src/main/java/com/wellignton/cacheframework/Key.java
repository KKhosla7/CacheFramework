package com.wellignton.cacheframework;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode
public class Key {
    private final String value;
    private ZonedDateTime expire;

    public Key(String value) {
        this.value = value;
        this.expire = ZonedDateTime.now(ZoneId.systemDefault());
    }
}
