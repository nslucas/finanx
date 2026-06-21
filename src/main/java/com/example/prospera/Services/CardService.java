package com.example.prospera.Services;

import com.example.prospera.DTO.CardRecord;
import com.example.prospera.Entities.Card;
import com.example.prospera.Entities.User;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public CardService(CardRepository cardRepository, AuthenticatedUserService authenticatedUserService) {
        this.cardRepository = cardRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<Card> findAllActiveForAuthenticatedUser() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(user.getId());
    }

    public Card findAuthenticatedUserCard(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return findUserCard(user.getId(), id);
    }

    public Card findUserCard(Integer userId, Integer cardId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Card not found with id: " + cardId));
    }

    public Card create(CardRecord record) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record);

        Card card = fromRecord(record);
        card.setId(null);
        card.setUserId(user.getId());
        card.setActive(true);
        return cardRepository.save(card);
    }

    public Card update(Integer id, CardRecord record) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record);

        Card card = findUserCard(user.getId(), id);
        card.setBankName(record.bankName());
        card.setName(record.name());
        card.setNetwork(record.network());
        card.setLastFourDigits(record.lastFourDigits());
        card.setCreditLimit(record.creditLimit());
        card.setClosingDay(record.closingDay());
        card.setDueDay(record.dueDay());
        return cardRepository.save(card);
    }

    public void deactivate(Integer id) {
        Card card = findAuthenticatedUserCard(id);
        card.setActive(false);
        cardRepository.save(card);
    }

    public Card fromRecord(CardRecord record) {
        return new Card(record.id(), record.bankName(), record.name(), record.network(), record.lastFourDigits(),
                record.creditLimit(), record.closingDay(), record.dueDay(), record.active(), null);
    }

    private void validate(CardRecord record) {
        if (record.bankName() == null || record.bankName().isBlank()) {
            throw new IllegalArgumentException("Bank name is required");
        }
        if (record.name() == null || record.name().isBlank()) {
            throw new IllegalArgumentException("Card name is required");
        }
        if (record.creditLimit() == null || record.creditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Credit limit must be zero or greater");
        }
        validateDay(record.closingDay(), "Closing day");
        validateDay(record.dueDay(), "Due day");
        if (record.lastFourDigits() != null && !record.lastFourDigits().matches("\\d{4}")) {
            throw new IllegalArgumentException("Last four digits must contain exactly four numbers");
        }
    }

    private void validateDay(Integer day, String fieldName) {
        if (day == null || day < 1 || day > 31) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 31");
        }
    }

}
