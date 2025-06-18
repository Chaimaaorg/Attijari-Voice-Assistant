package com.attijari.bankingservices.services.implementations;
import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.repositories.ClientRepository;
import com.attijari.bankingservices.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId).orElse(null);
    }


}
