package com.example.lab4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ContactRepository {
    private final File file;
    private final Map<String, ContactModel> contacts;

    public ContactRepository(@Value("${repository}") String repository) throws IOException {
        file = Paths.get(repository).toFile();
        contacts = file.exists() ?
                Arrays.stream(new ObjectMapper().readValue(file, ContactModel[].class))
                        .collect(Collectors.toMap(ContactModel::getId, contact -> contact))
                : new HashMap<>();
    }

    public Collection<ContactModel> findAll() {
        return contacts.values();
    }

    public ContactModel findById(String id) throws IllegalArgumentException {
        if (!contacts.containsKey(id)) {
            throw new IllegalArgumentException("Invalid contact id " + id);
        }

        return contacts.get(id);
    }

    public void save(ContactModel contactModel) throws IOException {
        contacts.put(contactModel.getId(), contactModel);
        writeToFile();
    }

    public void deleteById(String id) throws IOException {
        if (!contacts.containsKey(id)) {
            throw new IllegalArgumentException("Invalid contact id " + id);
        }

        contacts.remove(id);
        writeToFile();
    }

    private void writeToFile() throws IOException {
        new ObjectMapper().writeValue(file, findAll());
    }
}
