/**
 *
 */
package com.affirm.client.dao;

import com.affirm.client.model.LoggedUserClient;

import java.util.Date;

/**
 * @author jrodriguez
 */
public interface UserCLDAO {
    LoggedUserClient registerSessionLogin(int userId, String ip, String browserMetadata, Date signinDate, Integer sysUserId) throws Exception;
}
