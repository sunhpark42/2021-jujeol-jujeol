package com.jujeol.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jujeol.drink.domain.Category;
import com.jujeol.drink.domain.Drink;
import com.jujeol.drink.domain.Review;
import com.jujeol.drink.domain.repository.CategoryRepository;
import com.jujeol.drink.domain.repository.DrinkRepository;
import com.jujeol.drink.domain.repository.ReviewRepository;
import com.jujeol.member.domain.nickname.Nickname;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MemberInfoRepositoryTest {

    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private DrinkRepository drinkRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Member savedMember;
    private Category savedCategory;

    @BeforeEach
    void setUp() {
        Member member = Member.create(Provider.of("1234", ProviderName.TEST)
                , Nickname.create("물욕녀_1234")
                , Biography.create("물건 욕심이 많은 물욕녀"));

        savedMember = memberRepository.save(member);

        Category beer = Category.create("맥주", "BEER");
        savedCategory = categoryRepository.save(beer);
    }

    @DisplayName("내가 남긴 리뷰 모아보기")
    @Test
    public void findReviewsByMemberId() {
        //given
        Drink stella = Drink.create(
                "스텔라", "stella", 5.5, "KakaoTalk_Image_2021-07-08-19-58-09_001.png", 0.0,
                savedCategory);
        Drink drink = drinkRepository.save(stella);

        Review review1 = Review.create("리뷰 1", drink, savedMember);
        Review review2 = Review.create("리뷰 2", drink, savedMember);
        Review review3 = Review.create("리븊 3", drink, savedMember);
        Review review4 = Review.create("리뷰 4", drink, savedMember);
        Review review5 = Review.create("리뷰 5", drink, savedMember);
        List<Review> reviews = List.of(review1, review2, review3, review4, review5)
                .stream()
                .map(review -> reviewRepository.save(review))
                .collect(Collectors.toList());

        //when
        Page<Review> actualReviews = reviewRepository
                .findReviewsOfMine(savedMember.getId(), Pageable.ofSize(10));

        //then
        List<Long> expectedIds = reviews.stream()
                .map(Review::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        List<Long> actualIds = actualReviews.map(Review::getId).toList();

        assertThat(actualIds).isEqualTo(expectedIds);
    }

    @DisplayName("선호도로 내가 마신 술 모아보기")
    @Test
    public void findDrinkUsingPreference() {
        //given
        Drink kgb = Drink.create(
                "KGB", "", 3.5, "KakaoTalk_Image_2021-07-08-19-58-09_002.png", 0.0,
                savedCategory);
        Drink estp = Drink.create(
                "ESTP", "", 7.5, "KakaoTalk_Image_2021-07-08-19-58-11_003.png", 0.0,
                savedCategory);
        Drink tiger_rad = Drink.create(
                "타이거 라들러 자몽", "Tiger_Rad", 9.5, "KakaoTalk_Image_2021-07-08-19-58-15_004.png", 0.0,
                savedCategory);

        List<Drink> drinks = drinkRepository.saveAll(List.of(kgb, estp, tiger_rad));

        Preference preference1 = Preference.from(savedMember, kgb, 3.0);
        Preference preference2 = Preference.from(savedMember, estp, 5.0);
        Preference preference3 = Preference.from(savedMember, tiger_rad, 2.5);

        List.of(preference1, preference2, preference3)
                .stream()
                .map(preference -> preferenceRepository.save(preference))
                .collect(Collectors.toList());

        //when
        Page<Drink> drinkResponses = preferenceRepository
                .findDrinksOfMineWithPreference(savedMember.getId(), Pageable.ofSize(10));

        //then
        List<Long> drinkIds = drinks.stream()
                .map(Drink::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        List<Long> actualIds = drinkResponses.stream().map(Drink::getId)
                .collect(Collectors.toList());

        assertThat(drinkResponses).hasSize(drinks.size());
        assertThat(actualIds).isEqualTo(drinkIds);
    }
}
