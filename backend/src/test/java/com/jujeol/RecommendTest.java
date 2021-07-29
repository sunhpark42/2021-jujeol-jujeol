package com.jujeol;

import static com.jujeol.drink.DrinkTestContainer.*;

import com.jujeol.admin.acceptance.AdminAcceptanceTool;
import com.jujeol.drink.DrinkTestContainer;
import com.jujeol.drink.acceptance.DrinkAcceptanceTool;
import com.jujeol.drink.domain.repository.DrinkRepository;
import com.jujeol.member.acceptance.MemberAcceptanceTool;
import com.jujeol.member.domain.Biography;
import com.jujeol.member.domain.Member;
import com.jujeol.member.domain.MemberRepository;
import com.jujeol.member.domain.PreferenceRepository;
import com.jujeol.member.domain.Provider;
import com.jujeol.member.domain.ProviderName;
import com.jujeol.member.domain.nickname.Nickname;
import com.jujeol.member.fixture.TestMember;
import java.io.File;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.SQL92JDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

public class RecommendTest extends AcceptanceTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    MemberAcceptanceTool memberAcceptanceTool;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AdminAcceptanceTool adminAcceptanceTool;

    @Autowired
    DrinkAcceptanceTool drinkAcceptanceTool;

    @Test
    @DisplayName("hi")
    public void recommend() throws Exception{
        final Long obId = 주류_등록(OB);
        final Long stellaId = 주류_등록(STELLA);
        final Long kgbId = 주류_등록(KGB);
        final Long tigerId = 주류_등록(TIGER_LEMON);
        memberAcceptanceTool.선호도_등록(obId, 20.0, TestMember.PIKA);
        memberAcceptanceTool.선호도_등록(obId, 20.0, TestMember.SOLONG);
        memberAcceptanceTool.선호도_등록(stellaId, 20.0, TestMember.SOLONG);
        memberAcceptanceTool.선호도_등록(stellaId, 1.0, TestMember.WEDGE);
        memberAcceptanceTool.선호도_등록(obId, 20.5, TestMember.CROFFLE);
        memberAcceptanceTool.선호도_등록(kgbId, 20.5, TestMember.CROFFLE);
        memberAcceptanceTool.선호도_등록(tigerId, 20.5, TestMember.WEDGE);
        for (Member member : memberRepository.findAll()) {
            System.out.println("@@@@@@@@@@@@@@@@@@");
            System.out.println(member.getId());
            System.out.println(member.getNickname());
            System.out.println("@@@@@@@@@@@@@@@@@@");
        }
        ClassLoader classLoader = TestApp.class.getClassLoader();
        // 데이터를 어디서 가져올래?
        // infra layer => profile
        // member or recommend
        DataModel dataModel = new SQL92JDBCDataModel(dataSource, "preference", "member_id", "drink_id", "rate", null);
//        DataModel dataModel = new FileDataModel(new File(classLoader.getResource("dataset_grade.csv").getFile()));

        // 상품쪽으로 가는 거잖아. 그러면 이거는 솔직히 추천이라고 해봤자 카테고리 가중치만 둬서 카테고리 추천정도?
        // 맛 기반 추천 (상품 유사성으로 추천해주기)

        // 도메인 => 멤버 아이디,

        // 협업필터링 유저 기반
        // 협업필터링 자체는 무조건 유저를 비교하기 떄문에 아이템 기반이든 유저 선호도 기반이든 다 똑같이 에러가 생기고
        final UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        // A - B - C      A-C => 가중치,
        UserNeighborhood userNeighborhood = new ThresholdUserNeighborhood(0.5, similarity, dataModel);

        UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        for (RecommendedItem recommendedItem : userBasedRecommender.recommend(1, 1)) {
            System.out.println("#########################");
            System.out.println(recommendedItem.getItemID());
            System.out.println(recommendedItem.getValue());
            System.out.println("#########################");
        }
        // 인덱싱 도입
    }

    private Long 주류_등록(DrinkTestContainer drinkTestContainer) {
        adminAcceptanceTool.어드민_주류_데이터_등록(drinkTestContainer);
        return drinkAcceptanceTool.주류_아이디_조회(drinkTestContainer.getName());
    }
}
