package com.jujeol.drink.acceptance;

import com.jujeol.RequestBuilder;
import com.jujeol.admin.acceptance.AdminAcceptanceApi;
import com.jujeol.admin.ui.dto.AdminDrinkResponse;
import com.jujeol.commons.exception.JujeolExceptionDto;
import com.jujeol.drink.domain.Drink;
import com.jujeol.drink.domain.repository.DrinkRepository;
import com.jujeol.drink.exception.NotFoundDrinkException;
import com.jujeol.drink.ui.dto.DrinkDetailResponse;
import com.jujeol.drink.ui.dto.DrinkSimpleResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class DrinkAcceptanceApi {

    @Autowired
    private RequestBuilder requestBuilder;
    @Autowired
    private AdminAcceptanceApi adminAcceptanceApi;
    @Autowired
    private DrinkRepository drinkRepository;

    public DrinkDetailResponse 단일_상품_조회(Long id) {
        return requestBuilder.builder().get("/drinks/" + id).withoutLog().build().convertBody(DrinkDetailResponse.class);
    }

    public JujeolExceptionDto 단일_상품_조회_실패(Long id) {
        return requestBuilder.builder().get("/drinks/" + id).withoutLog().build().errorResponse();
    }

    public Long 주류_아이디_조회(String drinkName) {
        return drinkRepository.findByName(drinkName).orElseThrow(NotFoundDrinkException::new).getId();
    }

    public List<Drink> 전체_상품_목록_조회() {
        return drinkRepository.findAll();
    }
}
