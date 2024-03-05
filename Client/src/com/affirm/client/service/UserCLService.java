package com.affirm.client.service;

import com.affirm.client.model.form.UserRegisterForm;
import com.affirm.common.model.transactional.User;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface UserCLService {

    User registerUserFacebookMessenger(UserRegisterForm userForm, String messengerId) throws Exception;

    String getLoggedUserFirstName() throws Exception;

    Object getLoggedUser();
}
