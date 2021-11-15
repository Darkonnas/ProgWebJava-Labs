package com.example.lab4;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository repository) {
        this.contactRepository = repository;
    }

    public Collection<ContactModel> getAllContacts() {
        return contactRepository.findAll();
    }

    public ContactModel getContactById(String id) {
        return contactRepository.findById(id);
    }

    public void saveContact(ContactModel contactModel) throws IOException {
        contactRepository.save(contactModel);
    }

    public void removeContactById(String id) throws IOException {
        contactRepository.deleteById(id);
    }
}
