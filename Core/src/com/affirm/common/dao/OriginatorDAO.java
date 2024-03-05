package com.affirm.common.dao;

public interface OriginatorDAO {
    void registerAffiliator(String name, String ruc, String email, String phoneNumber, String ubigeoId, Integer bankId, String accountNumber, String generatedPassword, String encryptedPassword) throws Exception ;
}
