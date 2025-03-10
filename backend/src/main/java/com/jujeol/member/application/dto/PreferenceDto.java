package com.jujeol.member.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PreferenceDto {

    private double preferenceRate;

    public static PreferenceDto of(double preferenceRate) {
        return new PreferenceDto(preferenceRate);
    }
}


