package com.affirm.client.service.impl;

import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.model.form.UserRegisterForm;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service("userClService")
public class UserCLServiceImpl implements UserCLService {

    private static Logger logger = Logger.getLogger(UserCLServiceImpl.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public User registerUserFacebookMessenger(UserRegisterForm userForm, String messengerId) throws Exception {
        if (userDao.getUserByFacebookMessengerId(messengerId) != null) {
            logger.debug("This messenger id already has an user and doesn't match.");
            return null;
        }
        // Register the user
        User user = userDao.registerUser(userForm.getName(), userForm.getFirstSurname(), userForm.getLastSurname(), userForm.getDocType(), userForm.getDocNumber(), null);
        // Asociate the user with the messenger id
        userDao.registerFacebookMessengerId(user.getId(), messengerId);
        return user;
    }

    @Override
    public String getLoggedUserFirstName() throws Exception{
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            user.setPerson(personDao.getPerson(catalogService, Configuration.getDefaultLocale(), ((User) principal).getPersonId()   , false));
            return user.getPerson().getFirstName();
        } else if (principal instanceof LoggedUserEmployer) {
            return ((LoggedUserEmployer) principal).getName();
        } else {
            return "";
        }
    }

    @Override
    public Object getLoggedUser() {
        return SecurityUtils.getSubject().getPrincipal();
    }

}
