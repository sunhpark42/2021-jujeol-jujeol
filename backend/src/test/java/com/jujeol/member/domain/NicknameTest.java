package com.jujeol.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jujeol.member.domain.nickname.Nickname;
import com.jujeol.member.exception.InvalidUserNicknameCharacterException;
import com.jujeol.member.exception.InvalidUserNicknameLengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NicknameTest {

    @DisplayName("닉네임 생성 - 실패(10글자 초과 혹은 0자)")
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"   ", "열글자가되는이름짜라란"})
    void createNickname_fail_InvalidNicknameLength(String nickname) {
        //given
        //when
        //then
        assertThatThrownBy(() -> Nickname.create(nickname))
                .isInstanceOf(InvalidUserNicknameLengthException.class);
    }

    @DisplayName("닉네임 생성 - 실패(잘 못 된 형식)")
    @ParameterizedTest
    @ValueSource(strings = {"띄어쓰 기 포함", "자음은ㅇㅈ이지", "특수문자의왕!!"})
    void createNickname_fail_InvalidNicknameCharacter(String nickname) {
        //given
        //when
        //then
        assertThatThrownBy(() -> Nickname.create(nickname))
                .isInstanceOf(InvalidUserNicknameCharacterException.class);
    }

    @DisplayName("닉네임 생성 - 성공")
    @ParameterizedTest
    @ValueSource(strings = {"적절한닉네임", "열글자가되는이름짜잔","아우디사우디_123"})
    void createNickname(String nickname) {
        //given
        //when
        //then
        assertThat(Nickname.create(nickname)).isNotNull();
    }
}