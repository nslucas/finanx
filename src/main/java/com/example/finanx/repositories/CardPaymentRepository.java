package com.example.finanx.repositories;

import com.example.finanx.Entities.CardPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CardPaymentRepository extends JpaRepository<CardPayment, Integer> {
    List<CardPayment> findByUserIdAndCardIdAndMonthAndYearOrderByPaymentDateAscIdAsc(Integer userId, Integer cardId,
                                                                                      Integer month, Integer year);

    @Query("SELECT SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId AND p.cardId = :cardId " +
            "AND p.month = :month AND p.year = :year")
    BigDecimal sumByUserIdCardIdAndMonthYear(@Param("userId") Integer userId, @Param("cardId") Integer cardId,
                                             @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId AND p.month = :month AND p.year = :year")
    BigDecimal sumByUserIdAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month,
                                       @Param("year") Integer year);
}
