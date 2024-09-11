package com.ignaciocassi.customer;

import com.ignaciocassi.utils.PhonenumberValidator;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    private final PhonenumberValidator phonenumberValidator;

    public CustomerRegistrationService(CustomerRepository customerRepository,
                                       PhonenumberValidator phonenumberValidator) {
        this.customerRepository = customerRepository;
        this.phonenumberValidator = phonenumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (!phonenumberValidator.test(phoneNumber)) {
            throw new IllegalStateException("Phonenumber is not valid");
        }

        Optional<Customer> customer = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        // Check if a customer exists with the given phonenumber
        if (customer.isPresent()) {
            if (customer.get().getName().equals(request.getCustomer().getName()))
                return;
            throw new IllegalStateException(String.format("A Customer already exists for the phone number: [%s]", phoneNumber));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }

        customerRepository.save(request.getCustomer());
    }

}
