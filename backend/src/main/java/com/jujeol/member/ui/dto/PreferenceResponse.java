package com.jujeol.member.ui.dto;

import com.jujeol.member.application.dto.PreferenceDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PreferenceResponse {

    private double preferenceRate;

    public static PreferenceResponse from(PreferenceDto preferenceDto) {
        return new PreferenceResponse(preferenceDto.getPreferenceRate());
    }
}
