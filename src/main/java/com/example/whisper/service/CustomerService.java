package com.example.whisper.service;

import com.example.whisper.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    Customer findById(UUID id);

    List<Customer> findAllByIds(List<UUID> ids);

    List<Customer> getDistinctCustomersWhichMembersOfChatsByCustomerId(UUID customerId);

    Customer register(Customer customer);

    Void delete(Customer customer);
}
