package com.jujeol.drink.domain.repository;

import com.jujeol.drink.domain.Drink;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DrinkRepository extends JpaRepository<Drink, Long>, DrinkCustomRepository {

    @Query("select d from Drink d where d.viewCount.viewCount > 0 order by d.viewCount.viewCount desc")
    Page<Drink> findAllOrderByViewCount(Pageable pageable);

    @Query("select d from Drink d where d.preferenceAvg > 0 order by d.preferenceAvg desc")
    Page<Drink> findAllOrderByPreferenceAvg(Pageable pageable);

    @Query("select d from Drink d where d.name.name = :drinkName")
    Optional<Drink> findByName(String drinkName);
}
