package com.affirm.tests.onesignal

import com.affirm.common.model.catalog.CountryParam
import com.affirm.onesignal.service.OneSignalService
import com.affirm.onesignal.service.impl.OneSignalServiceImpl
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@CompileStatic
class OneSignalServiceTest extends BaseConfig {

    @Autowired
    private OneSignalService oneSignalService;

    private static final String TEST_USER_DESKTOP_PLAYER_ID = "2d235b1b-b635-4d4f-b4e6-1989c8d44d43"// firefox de daniel
    private static final String TEST_USER_MOBILE_PLAYER_ID = "f2653409-aa08-49d8-aa8e-3dee7be5845e"// movil de daniel

    private static final Integer countryId = CountryParam.COUNTRY_PERU
    
    List<String> playerIds = null

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        playerIds = Arrays.asList(TEST_USER_DESKTOP_PLAYER_ID, TEST_USER_MOBILE_PLAYER_ID)
    }

    @Test
    void shouldSendPlainNotificationWithTemplate() {
        String[] notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_ACCEPTED_OFFER)

        String notification = oneSignalService.sendNotification(null, countryId, notificationMessage, playerIds, null, null)
        Assert.assertTrue(notification != null)
    }

    @Test
    void shouldSendNotificationWithFilters() {
        String[] notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.CALL_INTERACTION_NO_RESPONSE)

        oneSignalService.applyNotificationsScheduleDelivery(null, null, 1)

        Map<String, String> tags = new HashMap<>()
        tags.put("current_question", "50");

        JSONObject filters = oneSignalService.applyNotificationsUserBasedFilters(2, OneSignalServiceImpl.RelationFilter.LESS_THAN, null, tags)

        String notification = oneSignalService.sendNotification(null, countryId, notificationMessage, playerIds, null, filters)
        Assert.assertTrue(notification != null)
    }

    @Test
    void shouldSendScheduledNotification() {
        String[] notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_OFFER)

        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 2)

        String timeToSendNotification = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0))
        System.out.println(timeToSendNotification)

        JSONObject filters = oneSignalService.applyNotificationsScheduleDelivery(null, timeToSendNotification, null)

        String notification = oneSignalService.sendNotification(null, countryId, notificationMessage, playerIds, null, filters)
        Assert.assertTrue(notification != null)
    }

    @Test
    void shouldCancelScheduledNotification() {
        String[] notificationMessage = Configuration.getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate.WITH_OFFER)

        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 2)

        String timeToSendNotification = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0))
        System.out.println(timeToSendNotification)

        JSONObject filters = oneSignalService.applyNotificationsScheduleDelivery(null, timeToSendNotification, null)

        String notification = oneSignalService.sendNotification(null, countryId, notificationMessage, playerIds, null, filters);

        Thread.sleep(1 * 1000);

        boolean successful = oneSignalService.cancelScheduledNotification(null, countryId, notification);
        Assert.assertTrue(successful);
    }

    @Test
    void shouldFailBecauseNotificationIdDoesNotExists() {
        boolean successful = oneSignalService.cancelScheduledNotification(null, countryId, "I_AM_A_BAD_NOTIFICATION_ID")
        Assert.assertFalse(successful)
    }

}
