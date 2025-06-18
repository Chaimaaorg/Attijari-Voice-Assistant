package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.models.Plafond;
import com.attijari.bankingservices.repositories.CarteRepository;
import com.attijari.bankingservices.repositories.PlafondRepository;
import com.attijari.bankingservices.services.implementations.PlafondServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlafondServiceImplTest {

    @Mock
    private PlafondRepository plafondRepository;

    @Mock
    private CarteRepository carteRepository;

    @InjectMocks
    private PlafondServiceImpl plafondService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateCardLimitByCardNum_CardNotFound() {
        // Mock data
        Long cardNum = 1234567890L;
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";

        when(carteRepository.findByNumeroCarte(cardNum)).thenReturn(Optional.empty());

        // Call the method
        plafondService.updateCardLimitByCardNum(cardNum, limit, typeLimit, duration, status);

        // Verify that nothing is saved
        verifyNoMoreInteractions(plafondRepository);
    }

    @Test
    void testUpdateCardLimitByCardNum_CardFound_NoMatchingType() {
        // Mock data
        Long cardNum = 1234567890L;
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";
        Carte carte = new Carte();
        carte.setIdCarte(1L);

        when(carteRepository.findByNumeroCarte(cardNum)).thenReturn(Optional.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(new ArrayList<>());

        // Call the method
        plafondService.updateCardLimitByCardNum(cardNum, limit, typeLimit, duration, status);

        // Verify that a new plafond is saved
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    void testUpdateCardLimitByCardNum_CardFound_MatchingType_AddLimit() {
        // Mock data
        Long cardNum = 1234567890L;
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";
        Carte carte = new Carte();
        carte.setIdCarte(1L);
        Plafond plafond = new Plafond();
        plafond.setIdPlafond(1L);
        plafond.setTypePlafond(typeLimit);
        plafond.setMontantPlafond(new BigDecimal("500.00"));

        List<Plafond> existingPlafonds = new ArrayList<>();
        existingPlafonds.add(plafond);

        when(carteRepository.findByNumeroCarte(cardNum)).thenReturn(Optional.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(existingPlafonds);

        // Call the method
        plafondService.updateCardLimitByCardNum(cardNum, limit, typeLimit, duration, status);

        // Verify that the existing plafond is updated
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    void testUpdateCardLimitByCardNum_CardFound_MatchingType_RemoveLimit() {
        // Mock data
        Long cardNum = 1234567890L;
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "remove";
        Carte carte = new Carte();
        carte.setIdCarte(1L);
        Plafond plafond = new Plafond();
        plafond.setIdPlafond(1L);
        plafond.setTypePlafond(typeLimit);
        plafond.setMontantPlafond(new BigDecimal("1500.00"));

        List<Plafond> existingPlafonds = new ArrayList<>();
        existingPlafonds.add(plafond);

        when(carteRepository.findByNumeroCarte(cardNum)).thenReturn(Optional.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(existingPlafonds);

        // Call the method
        plafondService.updateCardLimitByCardNum(cardNum, limit, typeLimit, duration, status);

        // Verify that the existing plafond is updated
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    void testUpdateCardLimitByCardType_CardTypeNotFound() {
        // Mock data
        Long userId = 1L;
        String cardType = "Credit";
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";

        when(carteRepository.findByTypeCarte(cardType)).thenReturn(new ArrayList<>());

        // Call the method
        plafondService.updateCardLimitByCardType(userId, cardType, limit, typeLimit, duration, status);

        // Verify that nothing is saved
        verifyNoMoreInteractions(plafondRepository);
    }

    @Test
    void testUpdateCardLimitByCardType_CardTypeFound_NoMatchingType() {
        // Mock data
        Long userId = 1L;
        String cardType = "Debit";
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Weekly";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";
        Carte carte = new Carte();
        carte.setIdCarte(1L);

        when(carteRepository.findByTypeCarte(cardType)).thenReturn(List.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(new ArrayList<>());

        // Call the method
        plafondService.updateCardLimitByCardType(userId, cardType, limit, typeLimit, duration, status);

        // Verify that a new plafond is saved
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    void testUpdateCardLimitByCardType_CardTypeFound_MatchingType_AddLimit() {
        // Mock data
        Long userId = 1L;
        String cardType = "Credit";
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "add";
        Carte carte = new Carte();
        carte.setIdCarte(1L);
        Plafond plafond = new Plafond();
        plafond.setIdPlafond(1L);
        plafond.setTypePlafond(typeLimit);
        plafond.setMontantPlafond(new BigDecimal("500.00"));

        List<Plafond> existingPlafonds = new ArrayList<>();
        existingPlafonds.add(plafond);

        when(carteRepository.findByTypeCarte(cardType)).thenReturn(List.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(existingPlafonds);

        // Call the method
        plafondService.updateCardLimitByCardType(userId, cardType, limit, typeLimit, duration, status);

        // Verify that the existing plafond is updated
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    void testUpdateCardLimitByCardType_CardTypeFound_MatchingType_RemoveLimit() {
        // Mock data
        Long userId = 1L;
        String cardType = "Credit";
        BigDecimal limit = new BigDecimal("1000.00");
        String typeLimit = "Daily";
        Optional<Timestamp> duration = Optional.of(new Timestamp(System.currentTimeMillis()));
        String status = "remove";
        Carte carte = new Carte();
        carte.setIdCarte(1L);
        Plafond plafond = new Plafond();
        plafond.setIdPlafond(1L);
        plafond.setTypePlafond(typeLimit);
        plafond.setMontantPlafond(new BigDecimal("1500.00"));

        List<Plafond> existingPlafonds = new ArrayList<>();
        existingPlafonds.add(plafond);

        when(carteRepository.findByTypeCarte(cardType)).thenReturn(List.of(carte));
        when(plafondRepository.findByCarte(carte.getIdCarte())).thenReturn(existingPlafonds);

        // Call the method
        plafondService.updateCardLimitByCardType(userId, cardType, limit, typeLimit, duration, status);

        // Verify that the existing plafond is updated
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }
}
