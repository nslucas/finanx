package com.example.finanx.Services;

import com.example.finanx.DTO.CardPaymentRecord;
import com.example.finanx.Entities.*;
import com.example.finanx.Repositories.CardPaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class CardPaymentService {
    private final CardPaymentRepository cardPaymentRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final CardService cardService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public CardPaymentService(CardPaymentRepository cardPaymentRepository, AuthenticatedUserService authenticatedUserService,
                              CardService cardService, AccountService accountService, TransactionService transactionService) {
        this.cardPaymentRepository = cardPaymentRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.cardService = cardService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public CardPayment create(Integer cardId, CardPaymentRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Payment body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record);
        Card card = cardService.findUserCard(user.getId(), cardId);
        if (!Boolean.TRUE.equals(card.getActive())) {
            throw new IllegalArgumentException("Card is inactive");
        }
        Account account = accountService.findUserAccount(user.getId(), record.accountId());
        accountService.ensureActive(account);

        LocalDate paymentDate = record.paymentDate() == null ? LocalDate.now() : record.paymentDate();
        Transaction transaction = transactionService.createCardPaymentTransaction(user.getId(), account.getId(),
                record.amount(), LocalDateTime.of(paymentDate, LocalTime.NOON), record.description());

        CardPayment payment = new CardPayment(null, card.getId(), account.getId(), user.getId(), record.month(),
                record.year(), record.amount(), paymentDate, record.description(), transaction.getId());
        return cardPaymentRepository.save(payment);
    }

    public List<CardPayment> findPayments(Integer cardId, Integer month, Integer year) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMonthYear(month, year);
        cardService.findUserCard(user.getId(), cardId);
        return cardPaymentRepository.findByUserIdAndCardIdAndMonthAndYearOrderByPaymentDateAscIdAsc(
                user.getId(), cardId, month, year);
    }

    public BigDecimal getPaidAmount(Integer userId, Integer cardId, Integer month, Integer year) {
        BigDecimal total = cardPaymentRepository.sumByUserIdCardIdAndMonthYear(userId, cardId, month, year);
        return total == null ? BigDecimal.ZERO : total;
    }

    public BigDecimal getPaidAmountForUserMonth(Integer userId, Integer month, Integer year) {
        BigDecimal total = cardPaymentRepository.sumByUserIdAndMonthYear(userId, month, year);
        return total == null ? BigDecimal.ZERO : total;
    }

    private void validate(CardPaymentRecord record) {
        if (record.accountId() == null) {
            throw new IllegalArgumentException("Account id is required");
        }
        validateMonthYear(record.month(), record.year());
        if (record.amount() == null || record.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }
}
