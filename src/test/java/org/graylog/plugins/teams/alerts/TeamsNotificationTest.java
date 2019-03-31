package org.graylog.plugins.teams.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamsNotificationTest {

  private TeamsNotification sut;

  @BeforeEach
  void setUp() {
    sut = new TeamsNotification();
  }

  @Test
  void getAttribute() throws AlarmCallbackConfigurationException {
    Map<String, Object> configMap = createValidConfigMap();
    sut.initialize(new Configuration(configMap));

    Map<String, Object> actual = sut.getAttributes();

    assertEquals(configMap, actual);
  }

  @Test
  void getRequestedConfiguration() {
    List<String> expectedConfigFields = Lists.newArrayList(
        TeamsNotificationConfig.WEBHOOK_URL,
        TeamsNotificationConfig.COLOR,
        TeamsNotificationConfig.DETAIL_MESSAGE,
        TeamsNotificationConfig.PROXY
    );

    Map<String, ConfigurationField> actual = sut.getRequestedConfiguration().getFields();

    assertEquals(4, actual.size());
    expectedConfigFields.forEach(
        expected -> assertTrue(actual.containsKey(expected)));
  }

  @Test
  void getName() {
    String expected = "Microsoft Teams Alarm Callback";

    // When
    String actual = sut.getName();

    // Then
    assertEquals(expected, actual);
  }

  @Test
  void checkConfiguration() throws AlarmCallbackConfigurationException {
    sut.initialize(new Configuration(createValidConfigMap()));
    try {
      sut.checkConfiguration();
    } catch (ConfigurationException e) {
      fail("Exception should not be thrown");
    }
  }

  @Test
  void checkConfiguration_Fail_WebhookURLIsEmpty() throws AlarmCallbackConfigurationException {
    Map<String, Object> m = createValidConfigMap();
    m.replace(TeamsNotificationConfig.WEBHOOK_URL, StringUtils.EMPTY);
    sut.initialize(new Configuration(m));

    assertThrows(ConfigurationException.class, () -> sut.checkConfiguration());
  }

  @Test
  void checkConfiguration_Fail_WebhookURLIsInvalid() throws AlarmCallbackConfigurationException {
    Map<String, Object> m = createValidConfigMap();
    m.replace(TeamsNotificationConfig.WEBHOOK_URL, "invalid URL");
    sut.initialize(new Configuration(m));

    ConfigurationException ex = assertThrows(ConfigurationException.class, () -> sut.checkConfiguration());
    assertEquals(TeamsNotificationConfig.WEBHOOK_URL + " is invalid as URI", ex.getMessage());
  }

  @Test
  void checkConfiguration_Fail_ProxyURLIsInvalid() throws AlarmCallbackConfigurationException {
    Map<String, Object> m = createValidConfigMap();
    m.replace(TeamsNotificationConfig.PROXY, "invalid URL");
    sut.initialize(new Configuration(m));

    ConfigurationException ex = assertThrows(ConfigurationException.class, () -> sut.checkConfiguration());
    assertEquals(TeamsNotificationConfig.PROXY + " is invalid as URI", ex.getMessage());
  }

  private Map<String, Object> createValidConfigMap() {
    Map<String, Object> m = new HashMap<>();
    m.put(TeamsNotificationConfig.WEBHOOK_URL, "https://testwebhook.com");
    m.put(TeamsNotificationConfig.COLOR, "000000");
    m.put(TeamsNotificationConfig.DETAIL_MESSAGE, "Detail");
    m.put(TeamsNotificationConfig.PROXY, "http://proxy.com:9999");
    return m;
  }
}