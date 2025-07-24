package com.bobocode.dao;

import com.bobocode.model.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountDao {
    List<Account> findAll();

    Account findById(long id);

    Account save(Account account);

    void remove(Account account);
}
