package com.affirm.bpeoplerest.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;

public interface BPeopleApiRestService {

    void bPeopleCrearUsuario(LoanApplication loanApplication, Person person, User user) throws Exception;
}
