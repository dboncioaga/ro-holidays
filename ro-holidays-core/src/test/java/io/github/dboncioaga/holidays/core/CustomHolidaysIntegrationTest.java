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
package io.github.dboncioaga.holidays.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom holidays functionality via builder.
 */
class CustomHolidaysIntegrationTest {

    @Test
    void testBuilderWithCustomHolidays() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .addCustomHoliday(LocalDate.of(2026, 3, 15), "Company Anniversary")
                .addCustomHoliday(LocalDate.of(2026, 12, 24), "Christmas Eve")
                .build();

        assertTrue(calendar.isHoliday(LocalDate.of(2026, 3, 15)));
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 12, 24)));
        
        // Standard holidays should still work
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 1, 1)));
    }

    @Test
    void testBuilderWithCustomHolidaysMap() {
        Map<LocalDate, String> customs = new HashMap<>();
        customs.put(LocalDate.of(2026, 3, 15), "Company Anniversary");
        customs.put(LocalDate.of(2026, 12, 24), "Christmas Eve");

        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .addCustomHolidays(customs)
                .build();

        assertTrue(calendar.isHoliday(LocalDate.of(2026, 3, 15)));
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 12, 24)));
    }

    @Test
    void testBuilderWithFloatingAndCustomHolidays() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addCustomHoliday(LocalDate.of(2026, 3, 17), "Company Anniversary") // Tuesday
                .build();

        // Custom holiday
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 3, 17)));
        
        // Floated holiday (Jan 24, 2026 is Saturday)
        // Both Friday (floated) AND Saturday (original) are holidays
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 1, 23)), "Friday should be holiday (floated)");
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 1, 24)), "Saturday should still be holiday");
    }

    @Test
    void testCustomHolidaysAppearInGetHolidays() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .addCustomHoliday(LocalDate.of(2026, 3, 15), "Company Anniversary")
                .build();

        Set<Holiday> holidays2026 = calendar.getHolidays(2026);
        
        assertTrue(holidays2026.stream().anyMatch(h -> 
            h.getDate().equals(LocalDate.of(2026, 3, 15)) &&
            h.getName().equals("Company Anniversary") &&
            h.getType() == HolidayType.OPTIONAL));
    }

    @Test
    void testBuilderChaining() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addCustomHoliday(LocalDate.of(2026, 3, 17), "Day 1") // Tuesday
                .addCustomHoliday(LocalDate.of(2026, 6, 19), "Day 2") // Friday
                .addCustomHoliday(LocalDate.of(2026, 9, 10), "Day 3") // Thursday
                .build();

        assertTrue(calendar.isHoliday(LocalDate.of(2026, 3, 17)));
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 6, 19)));
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 9, 10)));
    }

    @Test
    void testCustomHolidayOnBusinessDay() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .addCustomHoliday(LocalDate.of(2026, 3, 17), "Company Free Day")
                .build();

        LocalDate customHoliday = LocalDate.of(2026, 3, 17);
        
        assertTrue(calendar.isHoliday(customHoliday));
        assertFalse(calendar.isBusinessDay(customHoliday));
    }

    @Test
    void testBusinessDaysWithCustomHolidays() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .addCustomHoliday(LocalDate.of(2026, 3, 17), "Company Free Day")
                .build();

        BusinessDays businessDays = new BusinessDays(calendar);
        
        // March 16, 2026 is Monday
        LocalDate monday = LocalDate.of(2026, 3, 16);
        
        // Next business day should skip the custom holiday on March 17
        LocalDate next = businessDays.nextBusinessDay(monday);
        assertEquals(LocalDate.of(2026, 3, 18), next);
    }

    @Test
    void testBuilderNullDateThrows() {
        assertThrows(NullPointerException.class, () -> 
            RomanianHolidayCalendar.builder()
                .addCustomHoliday(null, "Test")
                .build());
    }

    @Test
    void testBuilderNullNameThrows() {
        assertThrows(NullPointerException.class, () -> 
            RomanianHolidayCalendar.builder()
                .addCustomHoliday(LocalDate.of(2026, 1, 1), null)
                .build());
    }

    @Test
    void testBuilderNullMapThrows() {
        assertThrows(NullPointerException.class, () -> 
            RomanianHolidayCalendar.builder()
                .addCustomHolidays(null)
                .build());
    }

    @Test
    void testEmptyBuilder() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder().build();
        
        // Should work like default calendar
        assertTrue(calendar.isHoliday(LocalDate.of(2026, 1, 1)));
        assertFalse(calendar.isHoliday(LocalDate.of(2026, 3, 15)));
    }
}
