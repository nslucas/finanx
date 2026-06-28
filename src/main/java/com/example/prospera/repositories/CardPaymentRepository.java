package com.example.prospera.repositories;

import com.example.prospera.Entities.CardPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardPaymentRepository extends JpaRepository<CardPayment, Integer> {
    Optional<CardPayment> findByIdAndUserIdAndCardId(Integer id, Integer userId, Integer cardId);

    List<CardPayment> findByUserIdAndCardIdAndMonthAndYearOrderByPaymentDateAscIdAsc(Integer userId, Integer cardId,
                                                                                      Integer month, Integer year);

    @Query("SELECT SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId AND p.cardId = :cardId " +
            "AND p.month = :month AND p.year = :year")
    BigDecimal sumByUserIdCardIdAndMonthYear(@Param("userId") Integer userId, @Param("cardId") Integer cardId,
                                             @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId AND p.cardId = :cardId " +
            "AND ((p.year > :fromYear) OR (p.year = :fromYear AND p.month >= :fromMonth))")
    BigDecimal sumByUserIdAndCardIdFromMonthYear(@Param("userId") Integer userId, @Param("cardId") Integer cardId,
                                                 @Param("fromMonth") Integer fromMonth,
                                                 @Param("fromYear") Integer fromYear);

    @Query("SELECT SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId AND p.month = :month AND p.year = :year")
    BigDecimal sumByUserIdAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month,
                                       @Param("year") Integer year);

    @Query("SELECT p.cardId, SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId " +
            "AND p.month = :month AND p.year = :year GROUP BY p.cardId")
    List<Object[]> sumByCardForUserIdAndMonthYear(@Param("userId") Integer userId,
                                                  @Param("month") Integer month,
                                                  @Param("year") Integer year);

    @Query("SELECT p.cardId, p.year, p.month, SUM(p.amount) FROM CardPayment p WHERE p.userId = :userId " +
            "AND ((p.year > :fromYear) OR (p.year = :fromYear AND p.month >= :fromMonth)) " +
            "AND ((p.year < :toYear) OR (p.year = :toYear AND p.month <= :toMonth)) " +
            "GROUP BY p.cardId, p.year, p.month")
    List<Object[]> sumByCardAndMonthYearInRange(@Param("userId") Integer userId,
                                                @Param("fromMonth") Integer fromMonth,
                                                @Param("fromYear") Integer fromYear,
                                                @Param("toMonth") Integer toMonth,
                                                @Param("toYear") Integer toYear);
}
