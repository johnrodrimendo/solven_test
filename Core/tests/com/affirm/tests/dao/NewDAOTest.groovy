package com.affirm.tests.dao

import com.affirm.common.dao.NewDAO
import com.affirm.common.model.New
import com.affirm.common.model.catalog.CountryParam
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class NewDAOTest extends BaseConfig {

    @Autowired
    private NewDAO newDAO

    @Test
    void shouldNews() {
        List<New> news = newDAO.getNews();

        Assertions.assertNotNull(news)
    }

    @Test
    void shouldGetNewsByCountryId() {
        List<New> peruNews = newDAO.getNews(CountryParam.COUNTRY_PERU);
        List<New> argentinaNews = newDAO.getNews(CountryParam.COUNTRY_ARGENTINA);

        Assertions.assertNotNull(peruNews)
        Assertions.assertNotNull(argentinaNews)
    }

    @Test
    void shouldRegisterNew() {
        Executable executable = {
            newDAO.registerNew(CountryParam.COUNTRY_PERU, 'Testing new title', 'Testing new description', 'http://google.com.pe', null, new Date(), '', null);
        }

        Assertions.assertDoesNotThrow(executable)
    }

}
