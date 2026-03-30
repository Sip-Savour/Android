package com.sipandsavour.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstantsTest {

    @Test
    public void baseUrl_shouldNotBeNull() {
        assertNotNull(Constants.BASE_URL);
        assertFalse(Constants.BASE_URL.isEmpty());
    }

    @Test
    public void timeouts_shouldBePositive() {
        assertTrue(Constants.TIMEOUT_CONNECT > 0);
        assertTrue(Constants.TIMEOUT_READ > 0);
        assertTrue(Constants.TIMEOUT_WRITE > 0);
    }

    @Test
    public void cache_shouldHaveReasonableSize() {
        assertTrue(Constants.CACHE_SIZE > 0);
        assertTrue(Constants.CACHE_MAX_AGE > 0);
        assertTrue(Constants.CACHE_MAX_STALE > 0);
    }

    @Test
    public void endpoints_shouldNotBeEmpty() {
        assertNotNull(Constants.EP_LOGIN);
        assertNotNull(Constants.EP_REGISTER);
        assertNotNull(Constants.EP_PREDICT);
        assertNotNull(Constants.EP_WEEKLY);
        assertNotNull(Constants.EP_FAVORITES);

        assertFalse(Constants.EP_LOGIN.isEmpty());
        assertFalse(Constants.EP_REGISTER.isEmpty());
    }

    @Test
    public void preferenceKeys_shouldBeUnique() {
        assertNotEquals(Constants.KEY_TOKEN, Constants.KEY_USERNAME);
        assertNotEquals(Constants.KEY_EMAIL, Constants.KEY_USER_ID);
        assertNotEquals(Constants.KEY_PREF_COLOR, Constants.KEY_PREF_FEATURES);
    }

    @Test
    public void notificationIds_shouldBePositive() {
        assertTrue(Constants.NOTIFICATION_DAILY_ID > 0);
    }
}
