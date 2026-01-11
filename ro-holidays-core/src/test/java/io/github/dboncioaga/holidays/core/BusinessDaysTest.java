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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BusinessDays}.
 */
class BusinessDaysTest {

    private BusinessDays businessDays;

    @BeforeEach
    void setUp() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();
        businessDays = new BusinessDays(calendar);
    }

    @Test
    void testNextBusinessDayFromMonday() {
        // Monday Jan 6, 2025 is a holiday (Boboteaza) -> next is Wednesday Jan 8
        LocalDate monday = LocalDate.of(2025, 1, 6);
        LocalDate next = businessDays.nextBusinessDay(monday);

        assertEquals(LocalDate.of(2025, 1, 8), next);
        assertEquals(DayOfWeek.WEDNESDAY, next.getDayOfWeek());
    }

    @Test
    void testNextBusinessDayFromFriday() {
        // Friday Jan 3, 2025 -> Wednesday Jan 8, 2025 (skipping weekend + Jan 6,7 holidays)
        LocalDate friday = LocalDate.of(2025, 1, 3);
        LocalDate next = businessDays.nextBusinessDay(friday);

        assertEquals(LocalDate.of(2025, 1, 8), next);
        assertEquals(DayOfWeek.WEDNESDAY, next.getDayOfWeek());
    }

    @Test
    void testNextBusinessDayFromSaturday() {
        // Saturday Jan 4, 2025 -> Wednesday Jan 8, 2025 (skipping weekend + Jan 6,7 holidays)
        LocalDate saturday = LocalDate.of(2025, 1, 4);
        LocalDate next = businessDays.nextBusinessDay(saturday);

        assertEquals(LocalDate.of(2025, 1, 8), next);
    }

    @Test
    void testNextBusinessDaySkippingHoliday() {
        // Wednesday Dec 31, 2025 -> Monday Jan 5, 2026
        // Jan 1-2 are holidays, Jan 3-4 weekend
        LocalDate dec31 = LocalDate.of(2025, 12, 31);
        LocalDate next = businessDays.nextBusinessDay(dec31);

        assertEquals(LocalDate.of(2026, 1, 5), next);
    }

    @Test
    void testPreviousBusinessDayFromTuesday() {
        // Tuesday Jan 14, 2025 -> Monday Jan 13
        LocalDate tuesday = LocalDate.of(2025, 1, 14);
        LocalDate previous = businessDays.previousBusinessDay(tuesday);

        assertEquals(LocalDate.of(2025, 1, 13), previous);
    }

    @Test
    void testPreviousBusinessDayFromMonday() {
        // Monday Jan 13, 2025 -> Friday Jan 10, 2025
        LocalDate monday = LocalDate.of(2025, 1, 13);
        LocalDate previous = businessDays.previousBusinessDay(monday);

        assertEquals(LocalDate.of(2025, 1, 10), previous);
        assertEquals(DayOfWeek.FRIDAY, previous.getDayOfWeek());
    }

    @Test
    void testPreviousBusinessDayFromSunday() {
        // Sunday Jan 5, 2025 -> Friday Jan 3, 2025 (skipping weekend)
        LocalDate sunday = LocalDate.of(2025, 1, 5);
        LocalDate previous = businessDays.previousBusinessDay(sunday);

        assertEquals(LocalDate.of(2025, 1, 3), previous);
    }

    @Test
    void testPreviousBusinessDaySkippingHoliday() {
        // Wednesday Jan 8, 2025 -> Friday Jan 3, 2025
        // (skipping Jan 6-7 holidays, weekend Jan 4-5)
        LocalDate jan8 = LocalDate.of(2025, 1, 8);
        LocalDate previous = businessDays.previousBusinessDay(jan8);

        assertEquals(LocalDate.of(2025, 1, 3), previous);
    }

    @Test
    void testAddZeroBusinessDays() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        LocalDate result = businessDays.addBusinessDays(date, 0);

        assertEquals(date, result);
    }

    @Test
    void testAddOneBusinessDay() {
        // Monday Jan 13 + 1 business day = Tuesday Jan 14
        LocalDate monday = LocalDate.of(2025, 1, 13);
        LocalDate result = businessDays.addBusinessDays(monday, 1);

        assertEquals(LocalDate.of(2025, 1, 14), result);
    }

    @Test
    void testAddBusinessDaysOverWeekend() {
        // Friday Jan 3 + 1 business day = Wednesday Jan 8 (skipping weekend + Jan 6,7 holidays)
        LocalDate friday = LocalDate.of(2025, 1, 3);
        LocalDate result = businessDays.addBusinessDays(friday, 1);

        assertEquals(LocalDate.of(2025, 1, 8), result);
    }

    @Test
    void testAddMultipleBusinessDays() {
        // Monday Jan 13 + 5 business days = Monday Jan 20
        LocalDate monday = LocalDate.of(2025, 1, 13);
        LocalDate result = businessDays.addBusinessDays(monday, 5);

        assertEquals(LocalDate.of(2025, 1, 20), result);
        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
    }

    @Test
    void testAddBusinessDaysOverHoliday() {
        // Dec 30, 2025 (Tuesday) + 3 business days
        // Dec 31 (Wed) is business day #1
        // Jan 1 (Thu) is holiday
        // Jan 2 (Fri) is holiday
        // Jan 3 (Sat) is weekend
        // Jan 4 (Sun) is weekend
        // Jan 5 (Mon) is business day #2
        // Jan 6 (Tue) is holiday (Boboteaza)
        // Jan 7 (Wed) is holiday (Soborul)
        // Jan 8 (Thu) is business day #3
        LocalDate dec30 = LocalDate.of(2025, 12, 30);
        LocalDate result = businessDays.addBusinessDays(dec30, 3);

        assertEquals(LocalDate.of(2026, 1, 8), result);
    }

    @Test
    void testSubtractOneBusinessDay() {
        // Tuesday Jan 14 - 1 business day = Monday Jan 13
        LocalDate tuesday = LocalDate.of(2025, 1, 14);
        LocalDate result = businessDays.addBusinessDays(tuesday, -1);

        assertEquals(LocalDate.of(2025, 1, 13), result);
    }

    @Test
    void testSubtractBusinessDaysOverWeekend() {
        // Monday Jan 13 - 1 business day = Friday Jan 10
        LocalDate monday = LocalDate.of(2025, 1, 13);
        LocalDate result = businessDays.addBusinessDays(monday, -1);

        assertEquals(LocalDate.of(2025, 1, 10), result);
    }

    @Test
    void testSubtractMultipleBusinessDays() {
        // Monday Jan 20 - 5 business days = Monday Jan 13
        LocalDate jan20 = LocalDate.of(2025, 1, 20);
        LocalDate result = businessDays.addBusinessDays(jan20, -5);

        assertEquals(LocalDate.of(2025, 1, 13), result);
    }

    @Test
    void testAddBusinessDaysAcrossYearBoundary() {
        // Dec 29, 2025 (Monday) + 1 business day
        // Dec 30 (Tue) is business day
        // Dec 31 (Wed) is business day
        // Jan 1-2 are holidays
        LocalDate dec29 = LocalDate.of(2025, 12, 29);
        LocalDate result = businessDays.addBusinessDays(dec29, 1);

        assertEquals(LocalDate.of(2025, 12, 30), result);
    }

    @Test
    void testAddLargeNumberOfBusinessDays() {
        // Test adding 20 business days (about 4 weeks)
        LocalDate start = LocalDate.of(2025, 2, 3); // Monday
        LocalDate result = businessDays.addBusinessDays(start, 20);

        // Verify result is a business day
        assertTrue(businessDays.getCalendar().isBusinessDay(result));
        assertTrue(result.isAfter(start));
    }

    @Test
    void testNullDateThrowsException() {
        assertThrows(NullPointerException.class, () ->
                businessDays.nextBusinessDay(null)
        );

        assertThrows(NullPointerException.class, () ->
                businessDays.previousBusinessDay(null)
        );

        assertThrows(NullPointerException.class, () ->
                businessDays.addBusinessDays(null, 1)
        );
    }

    @Test
    void testNullCalendarThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new BusinessDays(null)
        );
    }

    @Test
    void testGetCalendar() {
        assertNotNull(businessDays.getCalendar());
        assertInstanceOf(HolidayCalendar.class, businessDays.getCalendar());
    }

    @Test
    void testDeterministicBehavior() {
        LocalDate date = LocalDate.of(2025, 3, 15);

        // Multiple calls should return same result
        LocalDate next1 = businessDays.nextBusinessDay(date);
        LocalDate next2 = businessDays.nextBusinessDay(date);
        assertEquals(next1, next2);

        LocalDate prev1 = businessDays.previousBusinessDay(date);
        LocalDate prev2 = businessDays.previousBusinessDay(date);
        assertEquals(prev1, prev2);

        LocalDate add1 = businessDays.addBusinessDays(date, 5);
        LocalDate add2 = businessDays.addBusinessDays(date, 5);
        assertEquals(add1, add2);
    }

    @Test
    void testSymmetry() {
        // Adding then subtracting should return to original position
        // Use a business day for the test
        LocalDate start = LocalDate.of(2025, 3, 17); // Monday

        LocalDate forward = businessDays.addBusinessDays(start, 10);
        LocalDate backToStart = businessDays.addBusinessDays(forward, -10);

        assertEquals(start, backToStart);
    }
}
