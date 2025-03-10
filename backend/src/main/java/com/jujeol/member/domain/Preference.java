package com.jujeol.member.domain;

import com.jujeol.commons.domain.BaseEntity;
import com.jujeol.drink.domain.Drink;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Preference extends BaseEntity {

    private static final int ANONYMOUS_USER_RATE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id")
    private Drink drink;

    private double rate;

    private Preference(Member member, Drink drink, double rate) {
        this.member = member;
        this.drink = drink;
        this.rate = rate;
    }

    public static Preference from(Drink drink, double rate) {
        return new Preference(null, drink, rate);
    }

    public static Preference from(Member member, Drink drink, double rate) {
        return new Preference(member, drink, rate);
    }

    public static Preference anonymousPreference(Drink drink) {
        return new Preference(null, drink, ANONYMOUS_USER_RATE);
    }

    public void updateRate(double rate) {
        this.rate = rate;
    }
}
