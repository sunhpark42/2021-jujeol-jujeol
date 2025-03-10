package com.jujeol.member.ui.dto;

import com.jujeol.member.application.dto.SocialProviderCodeDto;
import com.jujeol.member.domain.ProviderName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SocialProviderCodeRequest {

    private String code;
    private ProviderName providerName;

    public SocialProviderCodeDto toDto() {
        return SocialProviderCodeDto.of(code, providerName);
    }

    public static SocialProviderCodeRequest of(String code, ProviderName providerName) {
        return new SocialProviderCodeRequest(code, providerName);
    }
}
