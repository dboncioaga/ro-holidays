/*
 * Copyright 2026 dboncioaga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dboncioaga.holidays.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import io.github.dboncioaga.holidays.core.BusinessDays;
import io.github.dboncioaga.holidays.core.HolidayCalendar;
import io.github.dboncioaga.holidays.core.RomanianHolidayCalendar;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Spring Boot auto-configuration for Romanian holidays.
 * <p>
 * This configuration automatically provides:
 * </p>
 * <ul>
 *   <li>A {@link HolidayCalendar} bean configured for Romanian holidays</li>
 *   <li>A {@link BusinessDays} bean for business day calculations</li>
 * </ul>
 * <p>
 * Configuration can be customized using the {@code ro.holidays} properties:
 * </p>
 * <pre>
 * ro:
 *   holidays:
 *     float-weekend-holidays: true
 *     custom-holidays:
 *       2026-03-15: "Company Anniversary"
 *       2026-12-24: "Christmas Eve"
 *     non-floatable-holidays:
 *       - "Crăciunul"
 *       - "Ziua Națională a României"
 *     exclude:
 *       - 2025-05-02
 * </pre>
 */
@AutoConfiguration
@EnableConfigurationProperties(RomanianHolidaysProperties.class)
public class RomanianHolidaysAutoConfiguration {

    /**
     * Provides a holiday calendar bean.
     * <p>
     * The calendar is configured with Romanian holidays and can be
     * customized via application properties.
     * </p>
     *
     * @param properties the configuration properties
     * @return a holiday calendar instance, never null
     */
    @Bean
    @ConditionalOnMissingBean
    public HolidayCalendar holidayCalendar(RomanianHolidaysProperties properties) {
        Map<LocalDate, String> customHolidays = properties.getCustomHolidays();
        Set<String> nonFloatableHolidays = new HashSet<>(properties.getNonFloatableHolidays());

        RomanianHolidayCalendar baseCalendar;
        if (customHolidays.isEmpty() && nonFloatableHolidays.isEmpty()) {
            baseCalendar = RomanianHolidayCalendar.loadDefault(properties.isFloatWeekendHolidays());
        } else {
            RomanianHolidayCalendar.Builder builder = RomanianHolidayCalendar.builder()
                    .floatWeekendHolidays(properties.isFloatWeekendHolidays());
            
            if (!customHolidays.isEmpty()) {
                builder.addCustomHolidays(customHolidays);
            }
            
            if (!nonFloatableHolidays.isEmpty()) {
                builder.addNonFloatableHolidays(nonFloatableHolidays);
            }
            
            baseCalendar = builder.build();
        }

        Set<LocalDate> excludedDates = new HashSet<>(properties.getExclude());

        if (excludedDates.isEmpty()) {
            return baseCalendar;
        }

        return new ConfigurableHolidayCalendar(baseCalendar, Set.of(), excludedDates);
    }

    /**
     * Provides a business days calculator bean.
     *
     * @param calendar the holiday calendar to use
     * @return a business days instance, never null
     */
    @Bean
    @ConditionalOnMissingBean
    public BusinessDays businessDays(HolidayCalendar calendar) {
        return new BusinessDays(calendar);
    }
}
