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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for floatable weekend holidays functionality.
 */
class FloatableHolidaysTest {

    @Test
    void testFloatingDisabledByDefault() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();
        
        // Jan 24, 2026 is Saturday
        LocalDate saturday = LocalDate.of(2026, 1, 24);
        assertTrue(calendar.isHoliday(saturday), "Saturday holiday should exist without floating");
        
        // Friday before should NOT be a holiday
        LocalDate friday = LocalDate.of(2026, 1, 23);
        assertFalse(calendar.isHoliday(friday), "Friday should not be holiday without floating");
    }

    @Test
    void testSaturdayHolidayFloatsToFriday() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(true);
        
        // Jan 24, 2026 is Saturday (Ziua Unirii)
        LocalDate friday = LocalDate.of(2026, 1, 23);
        assertTrue(calendar.isHoliday(friday), "Friday should be holiday when floating enabled");
        
        // Original Saturday is ALSO kept as a holiday
        LocalDate saturday = LocalDate.of(2026, 1, 24);
        assertTrue(calendar.isHoliday(saturday), "Saturday should still be holiday (both days are holidays)");
    }

    @Test
    void testSundayHolidayFloatsToMonday() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(true);
        
        // Apr 12, 2026 is Sunday (Easter)
        LocalDate sunday = LocalDate.of(2026, 4, 12);
        assertTrue(calendar.isHoliday(sunday), "Sunday should still be holiday");
        
        LocalDate monday = LocalDate.of(2026, 4, 13);
        assertTrue(calendar.isHoliday(monday), "Monday should be holiday (already Easter Monday)");
        
        // May 31, 2026 is Sunday (Pentecost)
        LocalDate pentecostSunday = LocalDate.of(2026, 5, 31);
        assertTrue(calendar.isHoliday(pentecostSunday), "Pentecost Sunday should still be holiday");
        
        LocalDate pentecostMonday = LocalDate.of(2026, 6, 1);
        assertTrue(calendar.isHoliday(pentecostMonday), "Monday after Sunday holiday should be holiday");
    }

    @Test
    void testFloatedHolidayHasObservedLabel() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(true);
        Set<Holiday> holidays = calendar.getHolidays(2026);
        
        // Check if floated holidays have "FD" suffix
        boolean hasFloatedLabel = holidays.stream()
                .anyMatch(h -> h.getName().contains(" FD"));
        
        assertTrue(hasFloatedLabel, "Floated holidays should have 'FD' suffix");
    }

    @Test
    void testWeekdayHolidaysNotFloated() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(true);
        
        // May 1, 2026 is Friday (Labour Day) - should not be floated
        LocalDate friday = LocalDate.of(2026, 5, 1);
        assertTrue(calendar.isHoliday(friday), "Weekday holiday should remain on same day");
        
        // Thursday and Saturday should NOT be holidays (unless they are holidays themselves)
        LocalDate thursday = LocalDate.of(2026, 4, 30);
        assertFalse(calendar.isHoliday(thursday), "Day before weekday holiday should not be holiday");
    }

    @Test
    void testMultipleWeekendHolidaysIn2026() {
        RomanianHolidayCalendar calendarNoFloat = RomanianHolidayCalendar.loadDefault(false);
        RomanianHolidayCalendar calendarWithFloat = RomanianHolidayCalendar.loadDefault(true);
        
        Set<Holiday> holidaysNoFloat = calendarNoFloat.getHolidays(2026);
        Set<Holiday> holidaysWithFloat = calendarWithFloat.getHolidays(2026);
        
        // With floating, we get additional holidays (original weekend days + floated weekdays)
        assertTrue(holidaysWithFloat.size() > holidaysNoFloat.size(), 
                "Floating calendar should have more holidays (includes both weekend and floated days)");
    }

    @Test
    void testBusinessDayCalculationsWithFloating() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(true);
        BusinessDays businessDays = new BusinessDays(calendar);
        
        // Jan 23, 2026 (Friday) is now a holiday (floated from Saturday)
        LocalDate jan23 = LocalDate.of(2026, 1, 23);
        assertFalse(calendar.isBusinessDay(jan23), "Floated Friday should not be business day");
        
        // Jan 22, 2026 (Thursday) should be last business day before weekend
        LocalDate jan22 = LocalDate.of(2026, 1, 22);
        assertTrue(calendar.isBusinessDay(jan22), "Thursday should be business day");
        
        LocalDate nextBD = businessDays.nextBusinessDay(jan22);
        // Should skip Friday (floated holiday), Saturday, Sunday, and go to Monday Jan 26
        assertEquals(LocalDate.of(2026, 1, 26), nextBD, 
                "Next business day should skip floated Friday and weekend");
    }

    @Test
    void testConstructorWithFloatingParameter() {
        RomanianHolidayCalendar calendarTrue = RomanianHolidayCalendar.loadDefault(true);
        RomanianHolidayCalendar calendarFalse = RomanianHolidayCalendar.loadDefault(false);
        
        // Jan 24, 2026 is Saturday
        LocalDate saturday = LocalDate.of(2026, 1, 24);
        LocalDate friday = LocalDate.of(2026, 1, 23);
        
        // With floating - both Saturday AND Friday are holidays
        assertTrue(calendarTrue.isHoliday(saturday), "Saturday should still be holiday");
        assertTrue(calendarTrue.isHoliday(friday), "Friday should also be holiday (floated)");
        
        // Without floating - only Saturday is holiday
        assertTrue(calendarFalse.isHoliday(saturday), "Saturday should be holiday");
        assertFalse(calendarFalse.isHoliday(friday), "Friday should not be holiday");
    }

    @Test
    void testNonFloatableHolidays() {
        // Create a calendar with some holidays excluded from floating
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addNonFloatableHoliday("Ziua Unirii Principatelor Române")
                .build();

        // Jan 24, 2026 is Saturday (Ziua Unirii)
        LocalDate saturday = LocalDate.of(2026, 1, 24);
        LocalDate friday = LocalDate.of(2026, 1, 23);
        
        // Saturday should be a holiday
        assertTrue(calendar.isHoliday(saturday), "Saturday should be holiday");
        
        // Friday should NOT be a holiday (floating disabled for this holiday)
        assertFalse(calendar.isHoliday(friday), "Friday should not be floated holiday");
    }

    @Test
    void testMultipleNonFloatableHolidays() {
        Set<String> nonFloatable = Set.of(
                "Ziua Unirii Principatelor Române",
                "Crăciunul"
        );
        
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addNonFloatableHolidays(nonFloatable)
                .build();

        // Jan 24, 2026 is Saturday (Ziua Unirii) - should not float
        LocalDate jan24 = LocalDate.of(2026, 1, 24);
        LocalDate jan23 = LocalDate.of(2026, 1, 23);
        assertTrue(calendar.isHoliday(jan24), "Saturday should be holiday");
        assertFalse(calendar.isHoliday(jan23), "Friday should not be floated");
        
        // Dec 25, 2026 is Friday (Crăciunul) - weekday, no floating needed
        LocalDate dec25 = LocalDate.of(2026, 12, 25);
        assertTrue(calendar.isHoliday(dec25), "Friday Christmas should be holiday");
    }

    @Test
    void testFloatingSkipsConsecutiveHolidays() {
        // Create a scenario where Friday is also a holiday
        // For example: Good Friday (Apr 10, 2026) and a Saturday holiday (Apr 11, 2026)
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addCustomHoliday(LocalDate.of(2026, 4, 10), "Good Friday")
                .addCustomHoliday(LocalDate.of(2026, 4, 11), "Test Saturday Holiday")
                .build();

        // Apr 11, 2026 is Saturday
        LocalDate saturday = LocalDate.of(2026, 4, 11);
        LocalDate friday = LocalDate.of(2026, 4, 10);
        LocalDate monday = LocalDate.of(2026, 4, 14);
        
        assertTrue(calendar.isHoliday(saturday), "Saturday should be holiday");
        assertTrue(calendar.isHoliday(friday), "Friday should be holiday (Good Friday)");
        
        // The floating day should skip Friday (already a holiday) and go forward to Monday
        // (Sunday Apr 12 is Easter, Monday Apr 13 is Easter Monday, so it goes to Tuesday Apr 14)
        assertTrue(calendar.isHoliday(monday), "Tuesday should be floated holiday");
    }

    @Test
    void testFloatingForwardSkipsConsecutiveHolidays() {
        // Create a scenario where Monday is also a holiday
        // For example: Sunday holiday followed by Easter Monday
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.builder()
                .floatWeekendHolidays(true)
                .addCustomHoliday(LocalDate.of(2026, 4, 12), "Test Sunday Holiday")
                .addCustomHoliday(LocalDate.of(2026, 4, 13), "Easter Monday")
                .build();

        // Apr 12, 2026 is Sunday
        LocalDate sunday = LocalDate.of(2026, 4, 12);
        LocalDate monday = LocalDate.of(2026, 4, 13);
        LocalDate tuesday = LocalDate.of(2026, 4, 14);
        
        assertTrue(calendar.isHoliday(sunday), "Sunday should be holiday");
        assertTrue(calendar.isHoliday(monday), "Monday should be holiday (Easter Monday)");
        
        // The floating day should skip Monday (already a holiday) and go to Tuesday
        assertTrue(calendar.isHoliday(tuesday), "Tuesday should be floated holiday");
    }
}
