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

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RomanianHolidayCalendar}.
 */
class RomanianHolidayCalendarTest {

    private RomanianHolidayCalendar calendar;

    @BeforeEach
    void setUp() {
        calendar = RomanianHolidayCalendar.loadDefault();
    }

    @Test
    void testLoadDefault() {
        assertNotNull(calendar);
        assertTrue(calendar.getRuleCount() > 0);
    }

    @Test
    void testNewYearIsHoliday() {
        LocalDate newYear2025 = LocalDate.of(2025, 1, 1);
        assertTrue(calendar.isHoliday(newYear2025));
    }

    @Test
    void testNewYearDayTwo() {
        LocalDate newYear2 = LocalDate.of(2025, 1, 2);
        assertTrue(calendar.isHoliday(newYear2));
    }

    @Test
    void testUnificationDay() {
        // January 24 is a holiday from 2017 onwards
        LocalDate unification2016 = LocalDate.of(2016, 1, 24);
        assertFalse(calendar.isHoliday(unification2016));

        LocalDate unification2017 = LocalDate.of(2017, 1, 24);
        assertTrue(calendar.isHoliday(unification2017));

        LocalDate unification2025 = LocalDate.of(2025, 1, 24);
        assertTrue(calendar.isHoliday(unification2025));
    }

    @Test
    void testLabourDay() {
        LocalDate labourDay = LocalDate.of(2025, 5, 1);
        assertTrue(calendar.isHoliday(labourDay));
    }

    @Test
    void testNationalDay() {
        LocalDate nationalDay = LocalDate.of(2025, 12, 1);
        assertTrue(calendar.isHoliday(nationalDay));
    }

    @Test
    void testChristmas() {
        LocalDate christmas = LocalDate.of(2025, 12, 25);
        assertTrue(calendar.isHoliday(christmas));

        LocalDate christmasDay2 = LocalDate.of(2025, 12, 26);
        assertTrue(calendar.isHoliday(christmasDay2));
    }

    @Test
    void testOrthodoxEaster2025() {
        // Easter 2025 is April 20
        LocalDate easter = LocalDate.of(2025, 4, 20);
        assertTrue(calendar.isHoliday(easter));

        // Good Friday (Easter - 2)
        LocalDate goodFriday = LocalDate.of(2025, 4, 18);
        assertTrue(calendar.isHoliday(goodFriday));

        // Easter Monday (Easter + 1)
        LocalDate easterMonday = LocalDate.of(2025, 4, 21);
        assertTrue(calendar.isHoliday(easterMonday));
    }

    @Test
    void testPentecost2025() {
        // Pentecost 2025: Easter + 49 = June 8
        LocalDate pentecost = LocalDate.of(2025, 6, 8);
        assertTrue(calendar.isHoliday(pentecost));

        // Pentecost Monday
        LocalDate pentecostMonday = LocalDate.of(2025, 6, 9);
        assertTrue(calendar.isHoliday(pentecostMonday));
    }

    @Test
    void testRegularWeekdayIsNotHoliday() {
        LocalDate regularDay = LocalDate.of(2025, 3, 15); // Saturday
        assertFalse(calendar.isHoliday(regularDay));
    }

    @Test
    void testGetHolidaysFor2025() {
        Set<Holiday> holidays = calendar.getHolidays(2025);

        assertNotNull(holidays);
        assertFalse(holidays.isEmpty());

        // Should have at least: New Year (2 days) + Unification + Labour Day +
        // National Day + Christmas (2 days) + Easter (3 days) + Pentecost (2 days)
        // = at least 13 LEGAL holidays
        long legalHolidays = holidays.stream()
                .filter(h -> h.getType() == HolidayType.LEGAL)
                .count();
        assertTrue(legalHolidays >= 13, "Expected at least 13 legal holidays, got " + legalHolidays);
    }

    @Test
    void testGetHolidaysReturnsImmutableSet() {
        Set<Holiday> holidays = calendar.getHolidays(2025);

        assertThrows(UnsupportedOperationException.class, () ->
                holidays.add(new Holiday(LocalDate.of(2025, 1, 1), "Test", HolidayType.LEGAL))
        );
    }

    @Test
    void testCaching() {
        Set<Holiday> holidays1 = calendar.getHolidays(2025);
        Set<Holiday> holidays2 = calendar.getHolidays(2025);

        // Should return the same cached instance
        assertSame(holidays1, holidays2);
    }

    @Test
    void testCachingDifferentYears() {
        Set<Holiday> holidays2025 = calendar.getHolidays(2025);
        Set<Holiday> holidays2026 = calendar.getHolidays(2026);

        // Different years should have different sets
        assertNotSame(holidays2025, holidays2026);

        // The actual holidays should be different (Easter dates differ)
        assertNotEquals(holidays2025, holidays2026);
    }

    @Test
    void testClearCache() {
        Set<Holiday> holidays1 = calendar.getHolidays(2025);
        calendar.clearCache();
        Set<Holiday> holidays2 = calendar.getHolidays(2025);

        // After clearing cache, should compute again (different instance)
        assertNotSame(holidays1, holidays2);

        // But content should be the same
        assertEquals(holidays1, holidays2);
    }

    @Test
    void testIsBusinessDay() {
        // Monday, not a holiday (January 6 is Boboteaza, so use January 13)
        LocalDate regularMonday = LocalDate.of(2025, 1, 13);
        assertTrue(calendar.isBusinessDay(regularMonday));

        // Saturday
        LocalDate saturday = LocalDate.of(2025, 1, 4);
        assertFalse(calendar.isBusinessDay(saturday));

        // Sunday
        LocalDate sunday = LocalDate.of(2025, 1, 5);
        assertFalse(calendar.isBusinessDay(sunday));

        // Holiday on weekday
        LocalDate newYear = LocalDate.of(2025, 1, 1);
        assertFalse(calendar.isBusinessDay(newYear));
    }

    @Test
    void testIsWeekend() {
        LocalDate saturday = LocalDate.of(2025, 1, 4);
        assertTrue(calendar.isWeekend(saturday));

        LocalDate sunday = LocalDate.of(2025, 1, 5);
        assertTrue(calendar.isWeekend(sunday));

        LocalDate monday = LocalDate.of(2025, 1, 6);
        assertFalse(calendar.isWeekend(monday));
    }

    @Test
    void testNullDateThrowsException() {
        assertThrows(NullPointerException.class, () ->
                calendar.isHoliday(null)
        );
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        // Access different years concurrently
                        int year = 2020 + (j % 10);
                        Set<Holiday> holidays = calendar.getHolidays(year);
                        assertNotNull(holidays);
                        assertFalse(holidays.isEmpty());

                        // Check specific dates
                        LocalDate newYear = LocalDate.of(year, 1, 1);
                        assertTrue(calendar.isHoliday(newYear));
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Threads did not complete in time");
        executor.shutdown();
        assertEquals(0, errorCount.get(), "Thread safety errors occurred");
    }

    @Test
    void testDeterministicResults() {
        // Multiple calls should return identical results
        for (int i = 0; i < 5; i++) {
            Set<Holiday> holidays = calendar.getHolidays(2025);
            assertEquals(holidays, calendar.getHolidays(2025));
        }
    }

    @Test
    void testYearValidation() {
        // Should accept years within range
        assertDoesNotThrow(() -> calendar.getHolidays(1900));
        assertDoesNotThrow(() -> calendar.getHolidays(2099));

        // Should reject years outside range
        assertThrows(IllegalArgumentException.class, () -> calendar.getHolidays(1899));
        assertThrows(IllegalArgumentException.class, () -> calendar.getHolidays(2100));
    }
}
