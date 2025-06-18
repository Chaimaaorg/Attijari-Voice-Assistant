package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.repositories.ClientRepository;
import com.attijari.bankingservices.services.implementations.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetClientById_Success() {
        // Mock data
        Long clientId = 1L;
        Client expectedClient = new Client();
        expectedClient.setIdClient(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(expectedClient));

        // Call the method
        Client result = clientService.getClientById(clientId);

        // Verify the result
        assertEquals(expectedClient, result);
    }

    @Test
    void testGetClientById_ClientNotFound() {
        // Mock data
        Long clientId = 1L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Call the method
        Client result = clientService.getClientById(clientId);

        // Verify the result
        assertEquals(null, result);
    }
}
