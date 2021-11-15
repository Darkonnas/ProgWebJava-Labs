package com.example.lab4;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService service) {
        this.contactService = service;
    }

    @GetMapping("/")
    public String showListForm(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        return "listForm";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        ContactModel contactModel = "<new>".equals(id) ? new ContactModel() : contactService.getContactById(id);
        model.addAttribute("contact", contactModel);
        return "editForm";
    }

    @GetMapping("/remove/{id}")
    public String removeContact(@PathVariable("id") String id) throws IOException {
        contactService.removeContactById(id);
        return "redirect:/";
    }

    @PostMapping("/save")
    public String saveContact(ContactModel contactModel) throws IOException {
        contactService.saveContact(contactModel);
        return "redirect:/";
    }
}
