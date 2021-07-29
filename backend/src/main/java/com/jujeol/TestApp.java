package com.jujeol;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.mahout.cf.taste.common.TasteException;
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

public class TestApp {

    public static void main(String[] args) throws IOException, TasteException {
        final long start = System.currentTimeMillis();
        ClassLoader classLoader = TestApp.class.getClassLoader();
        // 데이터를 어디서 가져올래?
        // infra layer => profile
        DataModel dataModel = new FileDataModel(new File(classLoader.getResource("dataset_grade.csv").getFile()));

        // 상품쪽으로 가는 거잖아. 그러면 이거는 솔직히 추천이라고 해봤자 카테고리 가중치만 둬서 카테고리 추천정도?
        // 맛 기반 추천 (상품 유사성으로 추천해주기)

        // 도메인 => 멤버 아이디,

        // 협업필터링 유저 기반
        // 협업필터링 자체는 무조건 유저를 비교하기 떄문에 아이템 기반이든 유저 선호도 기반이든 다 똑같이 에러가 생기고
        final UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        // A - B - C      A-C => 가중치,
        UserNeighborhood userNeighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);

        UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);

        for (RecommendedItem recommendedItem : userBasedRecommender.recommend(1000044383, 100)) {
            System.out.println("--------------------------");
            System.out.println(recommendedItem.getItemID());
            System.out.println(recommendedItem.getValue());
            System.out.println("--------------------------");
        }
        // 인덱싱 도입
        System.out.println(System.currentTimeMillis() - start);
    }
}
