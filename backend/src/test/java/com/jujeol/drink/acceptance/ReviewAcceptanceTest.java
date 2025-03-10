package com.jujeol.drink.acceptance;

import static com.jujeol.commons.exception.ExceptionCodeAndDetails.CREATE_REVIEW_LIMIT;
import static com.jujeol.commons.exception.ExceptionCodeAndDetails.INVALID_CONTENT_LENGTH;
import static com.jujeol.commons.exception.ExceptionCodeAndDetails.NOT_EXIST_REVIEW_IN_DRINK;
import static com.jujeol.commons.exception.ExceptionCodeAndDetails.NOT_FOUND_DRINK;
import static com.jujeol.commons.exception.ExceptionCodeAndDetails.NOT_FOUND_REVIEW;
import static com.jujeol.commons.exception.ExceptionCodeAndDetails.UNAUTHORIZED_USER;
import static com.jujeol.drink.DrinkTestContainer.OB;
import static com.jujeol.drink.DrinkTestContainer.STELLA;
import static com.jujeol.member.fixture.TestMember.PIKA;
import static com.jujeol.member.fixture.TestMember.RANDOM_MEMBER;
import static com.jujeol.member.fixture.TestMember.SOLONG;
import static org.assertj.core.api.Assertions.assertThat;

import com.jujeol.AcceptanceTest;
import com.jujeol.RequestBuilder.HttpResponse;
import com.jujeol.admin.acceptance.AdminAcceptanceTool;
import com.jujeol.commons.exception.JujeolExceptionDto;
import com.jujeol.drink.DrinkTestContainer;
import com.jujeol.drink.application.dto.ReviewRequest;
import com.jujeol.drink.application.dto.ReviewResponse;
import com.jujeol.member.fixture.TestMember;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class ReviewAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminAcceptanceTool adminAcceptanceTool;
    @Autowired
    private DrinkAcceptanceTool drinkAcceptanceTool;
    @Autowired
    private ReviewAcceptanceTool reviewAcceptanceTool;

    @DisplayName("리뷰 생성 - 성공")
    @Test
    void createReviewTest() {
        //given
        Long obId = 주류_등록(OB);
        String content = "너무 맛있어요 - 소롱";
        ReviewRequest reviewRequest = new ReviewRequest(content);

        //when
        final HttpStatus httpStatus =
                request().post("/drinks/{id}/reviews", reviewRequest, obId)
                        .withUser(SOLONG)
                        .withDocument("reviews/create")
                        .build().statusCode();

        //then
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        List<ReviewResponse> reviewResponses = reviewAcceptanceTool.리뷰_조회(obId);
        assertThat(reviewResponses).extracting("content").contains(content);
    }

    @DisplayName("리뷰 생성 - 성공(300자)")
    @Test
    void createReviewTest_300() {
        //given
        Long obId = 주류_등록(OB);
        String content = "이 내용은 정확히 300자입니다. 300자 까지 뭐라고 쓸지 모르겠네요.. 자소서 쓰는 기분... 저희팀 소개를 하자면 일단 저 웨지 나봄 크로플 피카 소롱 서니 티케 이렇게 7명으로 이루어져있구요! 모두 한명 한명 맡은바 임무를 잘 수행하고 있습니다. 저는 여기 팀에 들어오게 되어서 얼마나 좋은지 몰라요 다들 너무 잘 챙겨주시고 잘 이끌어주셔서 감사합니다. 코로나 또한 빨리 종식되었으면 좋겠어요 다들 못보니까 너무 아쉽고 힘드네요 ㅠㅠ  다들 건강 챙기시구 코로나 조심하세요 그럼 이만 줄이겠습니다. 이부분은300자를맞추기위한몸부림";
        //when
        final HttpStatus httpStatus = request()
                .post("/drinks/{id}/reviews", new ReviewRequest(content), obId)
                .withUser()
                .withDocument("reviews/create-succeed300")
                .build().statusCode();
        //then
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        List<ReviewResponse> reviewResponses = reviewAcceptanceTool.리뷰_조회(obId);
        assertThat(reviewResponses).extracting("content").contains(content);
    }

    @DisplayName("리뷰 생성 - 실패(존재하지 않는 주류 id)")
    @Test
    void createReviewTest_fail() {
        //given
        String content = "너무 맛있어요 - 피카";

        //when
        final JujeolExceptionDto errorResponse = request()
                .post("/drinks/{id}/reviews", new ReviewRequest(content), Long.MAX_VALUE)
                .withUser(PIKA)
                .withDocument("reviews/create-fail")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, NOT_FOUND_DRINK);
    }

    @DisplayName("리뷰 생성 - 실패(하루 최대 생성 제한)")
    @Test
    void createReviewTest_fail_createReviewLimit() {
        //given
        String content = "너무 맛있어요 - 피카";
        Long obId = 주류_등록(OB);
        리뷰_등록(PIKA, content, obId);

        String content2 = "너무 맛있어요2 - 피카";
        //when
        final JujeolExceptionDto errorResponse = request()
                .post("/drinks/{id}/reviews", new ReviewRequest(content2), obId)
                .withUser(PIKA)
                .withDocument("reviews/create-fail-limit")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, CREATE_REVIEW_LIMIT);
    }

    @DisplayName("리뷰 생성 - 실패(리뷰 내용이 비어 있을 경우)")
    @Test
    void createReviewTest_fail_reviewEmptyContent() {
        //given
        Long obId = 주류_등록(OB);
        String content = "";

        //when
        final JujeolExceptionDto errorResponse = request()
                .post("/drinks/{id}/reviews", new ReviewRequest(content), obId)
                .withUser()
                .withDocument("reviews/create-fail-emptyContent")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, INVALID_CONTENT_LENGTH);
    }

    @DisplayName("리뷰 생성 - 실패(리뷰 내용이 300자가 넘을 경우)")
    @Test
    void createReviewTest_fail_reviewContentOver300() {
        //given
        Long obId = 주류_등록(OB);
        String content = "이 내용은 300자가 넘는 내용입니다. 300자 까지 뭐라고 쓸지 모르겠네요.. 자소서 쓰는 기분... 저희팀 소개를 하자면 일단 저 웨지 나봄 크로플 피카 소롱 서니 티케 이렇게 7명으로 이루어져있구요! 모두 한명 한명 맡은바 임무를 잘 수행하고 있습니다. 저는 여기 팀에 들어오게 되어서 얼마나 좋은지 몰라요 다들 너무 잘 챙겨주시고 잘 이끌어주셔서 감사합니다. 그리구 코로나 또한 빨리 종식되었으면 좋겠어요 다들 못보니까 너무 아쉽고 힘드네요 ㅠㅠ 팀프로젝트가 성공리에 마무리되어 끝났으면 좋겠고 서로 싸우지말고 잘 진행햇으면 좋겠습니다. 다들 건강 챙기시구 코로나 조심하세요 그럼 이만 줄이겠습니다.";

        //when
        final JujeolExceptionDto errorResponse = request()
                .post("/drinks/{id}/reviews", new ReviewRequest(content), obId)
                .withUser()
                .withDocument("reviews/create-fail-contentOver300")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, INVALID_CONTENT_LENGTH);
    }

    @DisplayName("리뷰 조회 - 성공")
    @Test
    void showReviews() {
        //given
        Long obId = 주류_등록(OB);

        int totalSize = 30;
        for (int i = 0; i < totalSize; i++) {
            reviewAcceptanceTool.리뷰_등록(RANDOM_MEMBER, "리뷰" + i, obId);
        }

        //when
        final HttpResponse httpResponsePageOne = request()
                .get("/drinks/{id}/reviews?page=1", obId)
                .withDocument("reviews/show")
                .build();

        final HttpResponse httpResponsePageTwo = request()
                .get("/drinks/{id}/reviews?page=2", obId)
                .build();

        //then
        페이징_검증(httpResponsePageOne.pageInfo(), 1, 3, 10, totalSize);
        페이징_검증(httpResponsePageTwo.pageInfo(), 2, 3, 10, totalSize);
    }

    @DisplayName("리뷰 수정 - 성공")
    @Test
    public void updateReview() {
        //given
        final String originalContent = "바보 윤지우";
        String content = "천재 윤지우";

        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(PIKA, originalContent, obId);

        //when
        final HttpStatus httpStatus = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(content), obId, reviewId)
                .withUser(PIKA)
                .withDocument("reviews/update")
                .build().statusCode();

        //then
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        final List<ReviewResponse> reviews = reviewAcceptanceTool.리뷰_조회(obId);
        assertThat(reviews).extracting("content").contains(content);
        assertThat(reviews).extracting("content").doesNotContain(originalContent);
    }

    @DisplayName("리뷰 수정 - 실패(잘못된 Drink id)")
    @Test
    public void updateReview_fail_notFoundDrink() {
        //given
        final String originalContent = "바보 윤지우";
        String content = "천재 윤지우";

        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(PIKA, originalContent, obId);

        //when
        final JujeolExceptionDto errorResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(content), Long.MAX_VALUE,
                        reviewId)
                .withUser(PIKA)
                .withDocument("reviews/update-fail-drink")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, NOT_FOUND_DRINK);
    }

    @DisplayName("리뷰 수정 - 실패(잘못된 Review id)")
    @Test
    public void updateReview_fail_notFoundReview() {
        //given
        final String originalContent = "바보 윤지우";
        String content = "천재 윤지우";

        Long obId = 주류_등록(OB);
        리뷰_등록(PIKA, originalContent, obId);

        //when
        final JujeolExceptionDto errorResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(content), obId,
                        Long.MAX_VALUE)
                .withUser(PIKA)
                .withDocument("reviews/update-fail-review")
                .build().errorResponse();

        //then
        예외_검증(errorResponse, NOT_FOUND_REVIEW);
    }

    @DisplayName("리뷰 수정 - 실패(리뷰와 주류 불일치)")
    @Test
    public void updateReviewTest_fail_notExistReviewInDrink() {
        //given
        String content = "천재 윤지우";

        Long obId = 주류_등록(OB);
        Long stellaId = 주류_등록(STELLA);
        Long reviewId = 리뷰_등록(PIKA, content, obId);

        //when
        final HttpResponse httpResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(content), stellaId,
                        reviewId)
                .withUser(PIKA)
                .withDocument("reviews/update-fail-match")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), NOT_EXIST_REVIEW_IN_DRINK);
    }

    @DisplayName("리뷰 수정 - 실패(다른 사용자가 수정할 경우)")
    @Test
    void updateReviewTest_fail_anotherUser() {
        //given
        String content = "천재 윤지우";

        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(PIKA, content, obId);

        //when
        final HttpResponse httpResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(content), obId, reviewId)
                .withUser(SOLONG)
                .withDocument("reviews/update-fail-author")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), UNAUTHORIZED_USER);
    }

    @DisplayName("리뷰 수정 - 실패(리뷰 내용이 비어 있을 경우)")
    @Test
    public void updateReviewTest_fail_reviewEmptyContent() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(PIKA, content, obId);

        //when
        final HttpResponse httpResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(""), obId,
                        reviewId)
                .withUser(PIKA)
                .withDocument("reviews/update-fail-emptyContent")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), INVALID_CONTENT_LENGTH);
    }

    @DisplayName("리뷰 수정 - 실패(리뷰 내용이 300자가 넘을 경우)")
    @Test
    public void updateReviewTest_fail_reviewContentOver300() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(PIKA, content, obId);

        String newContent = "이 내용은 300자가 넘는 내용입니다. 300자 까지 뭐라고 쓸지 모르겠네요.. 자소서 쓰는 기분... 저희팀 소개를 하자면 일단 저 웨지 나봄 크로플 피카 소롱 서니 티케 이렇게 7명으로 이루어져있구요! 모두 한명 한명 맡은바 임무를 잘 수행하고 있습니다. 저는 여기 팀에 들어오게 되어서 얼마나 좋은지 몰라요 다들 너무 잘 챙겨주시고 잘 이끌어주셔서 감사합니다. 그리구 코로나 또한 빨리 종식되었으면 좋겠어요 다들 못보니까 너무 아쉽고 힘드네요 ㅠㅠ 팀프로젝트가 성공리에 마무리되어 끝났으면 좋겠고 서로 싸우지말고 잘 진행햇으면 좋겠습니다. 다들 건강 챙기시구 코로나 조심하세요 그럼 이만 줄이겠습니다.";

        //when
        final HttpResponse httpResponse = request()
                .put("/drinks/{drinkId}/reviews/{reviewId}", new ReviewRequest(newContent), obId,
                        reviewId)
                .withUser(PIKA)
                .withDocument("reviews/update-fail-contentOver300")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), INVALID_CONTENT_LENGTH);
    }

    @DisplayName("리뷰 삭제 - 성공")
    @Test
    public void deleteReviewTest() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(SOLONG, content, obId);
        //when
        final HttpResponse httpResponse = request()
                .delete("/drinks/{drinkId}/reviews/{reviewId}", obId, reviewId)
                .withUser(SOLONG)
                .withDocument("reviews/delete")
                .build();
        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reviewAcceptanceTool.리뷰_조회(obId)).isEmpty();
    }

    @DisplayName("리뷰 삭제 - 실패(잘못된 Drink id)")
    @Test
    public void deleteReviewTest_fail_notFoundDrink() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(SOLONG, content, obId);
        //when
        final HttpResponse httpResponse = request()
                .delete("/drinks/{drinkId}/reviews/{reviewId}", Long.MAX_VALUE, reviewId)
                .withUser(SOLONG)
                .withDocument("reviews/delete-fail-drink")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), NOT_FOUND_DRINK);
    }

    @DisplayName("리뷰 삭제 - 실패(잘못된 Review id)")
    @Test
    public void deleteReviewTest_fail_notFoundReview() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        리뷰_등록(SOLONG, content, obId);
        //when
        final HttpResponse httpResponse = request()
                .delete("/drinks/{drinkId}/reviews/{reviewId}", obId, Long.MAX_VALUE)
                .withUser(SOLONG)
                .withDocument("reviews/delete-fail-review")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), NOT_FOUND_REVIEW);
    }

    @DisplayName("리뷰 삭제 - 실패(리뷰와 주류 불일치)")
    @Test
    public void deleteReviewTest_fail_notExistReviewInDrink() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        final Long stellaId = 주류_등록(STELLA);
        final Long reviewId = 리뷰_등록(SOLONG, content, obId);

        //when
        final HttpResponse httpResponse = request()
                .delete("/drinks/{drinkId}/reviews/{reviewId}", stellaId, reviewId)
                .withUser(SOLONG)
                .withDocument("reviews/delete-fail-match")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), NOT_EXIST_REVIEW_IN_DRINK);
    }

    @DisplayName("리뷰 삭제 - 실패(다른 사용자가 삭제할 경우)")
    @Test
    void deleteReviewTest_fail_anotherUser() {
        //given
        String content = "hello";
        Long obId = 주류_등록(OB);
        Long reviewId = 리뷰_등록(SOLONG, content, obId);

        //when
        final HttpResponse httpResponse = request()
                .delete("/drinks/{drinkId}/reviews/{reviewId}", obId, reviewId)
                .withUser(PIKA)
                .withDocument("reviews/delete-fail-author")
                .build();

        //then
        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        예외_검증(httpResponse.errorResponse(), UNAUTHORIZED_USER);
    }

    private Long 주류_등록(DrinkTestContainer drinkTestContainer) {
        adminAcceptanceTool.어드민_주류_데이터_등록(drinkTestContainer);
        return drinkAcceptanceTool.주류_아이디_조회(drinkTestContainer.getName());
    }

    private Long 리뷰_등록(TestMember testMember, String content, Long drinkId) {
        reviewAcceptanceTool.리뷰_등록(testMember, content, drinkId);
        return reviewAcceptanceTool.리뷰_조회(drinkId, content).getId();
    }
}
