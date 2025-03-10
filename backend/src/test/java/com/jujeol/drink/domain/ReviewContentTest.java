package com.jujeol.drink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jujeol.drink.exception.InvalidReviewContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewContentTest {

    @DisplayName("리뷰 생성 - 성공")
    @ParameterizedTest
    @ValueSource(strings = {"와 진짜 맛있다!!", "너무 행복해요!", "이 내용은 정확히 300자입니다. 300자 까지 뭐라고 쓸지 모르겠네요.. 자소서 쓰는 기분... 저희팀 소개를 하자면 일단 저 웨지 나봄 크로플 피카 소롱 서니 티케 이렇게 7명으로 이루어져있구요! 모두 한명 한명 맡은바 임무를 잘 수행하고 있습니다. 저는 여기 팀에 들어오게 되어서 얼마나 좋은지 몰라요 다들 너무 잘 챙겨주시고 잘 이끌어주셔서 감사합니다. 그리구 코로나 또한 빨리 종식되었으면 좋겠어요 다들 못보니까 너무 아쉽고 힘드네요 ㅠㅠ 다들 건강 챙기시구 코로나 조심하세요 그럼 이만 줄이겠습니다. 다들사랑해용8자만더쓰자얍히"})
    void reviewContentTest(String content) {
        //given
        ReviewContent reviewContent = new ReviewContent(content);
        //when
        //then
        assertThat(reviewContent.getContent()).isEqualTo(content);
    }

    @DisplayName("리뷰 생성 - 실패(리뷰 내용이 공백이거나 300자가 넘을 경우)")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "이 내용은 301자입니다. 300자 까지 뭐라고 쓸지 모르겠네요.. 자소서 쓰는 기분... 저희팀 소개를 하자면 일단 저 웨지 나봄 크로플 피카 소롱 서니 티케 이렇게 7명으로 이루어져있구요! 모두 한명 한명 맡은바 임무를 잘 수행하고 있습니다. 저는 여기 팀에 들어오게 되어서 얼마나 좋은지 몰라요 다들 너무 잘 챙겨주시고 잘 이끌어주셔서 감사합니다. 그리구 코로나 또한 빨리 종식되었으면 좋겠어요 다들 못보니까 너무 아쉽고 힘드네요 ㅠㅠ 다들 건강 챙기시구 코로나 조심하세요 그럼 이만 줄이겠습니다.  나머지 내용은 301자를채우기위한"})
    void reviewContentTest_fail(String content) {
        //given
        //when
        //then
        assertThatThrownBy(() -> new ReviewContent(content))
                .isInstanceOf(InvalidReviewContentException.class);
    }
}