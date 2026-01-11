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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HolidayCalendar} default methods.
 */
class HolidayCalendarTest {

    @Test
    void testIsWeekendForSaturday() {
        HolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate saturday = LocalDate.of(2025, 1, 4); // Saturday

        assertTrue(calendar.isWeekend(saturday));
        assertEquals(DayOfWeek.SATURDAY, saturday.getDayOfWeek());
    }

    @Test
    void testIsWeekendForSunday() {
        HolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate sunday = LocalDate.of(2025, 1, 5); // Sunday

        assertTrue(calendar.isWeekend(sunday));
        assertEquals(DayOfWeek.SUNDAY, sunday.getDayOfWeek());
    }

    @Test
    void testIsWeekendForWeekday() {
        HolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate monday = LocalDate.of(2025, 1, 6); // Monday
        LocalDate tuesday = LocalDate.of(2025, 1, 7); // Tuesday
        LocalDate wednesday = LocalDate.of(2025, 1, 8); // Wednesday
        LocalDate thursday = LocalDate.of(2025, 1, 9); // Thursday
        LocalDate friday = LocalDate.of(2025, 1, 10); // Friday

        assertFalse(calendar.isWeekend(monday));
        assertFalse(calendar.isWeekend(tuesday));
        assertFalse(calendar.isWeekend(wednesday));
        assertFalse(calendar.isWeekend(thursday));
        assertFalse(calendar.isWeekend(friday));
    }

    @Test
    void testIsBusinessDayForRegularWeekday() {
        TestHolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate monday = LocalDate.of(2025, 1, 6); // Monday, not a holiday

        assertTrue(calendar.isBusinessDay(monday));
    }

    @Test
    void testIsBusinessDayForWeekend() {
        TestHolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate saturday = LocalDate.of(2025, 1, 4); // Saturday

        assertFalse(calendar.isBusinessDay(saturday));
    }

    @Test
    void testIsBusinessDayForHoliday() {
        TestHolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate holiday = LocalDate.of(2025, 1, 1); // New Year (holiday)
        calendar.addHoliday(holiday);

        assertFalse(calendar.isBusinessDay(holiday));
    }

    @Test
    void testIsBusinessDayForHolidayOnWeekend() {
        TestHolidayCalendar calendar = new TestHolidayCalendar();
        LocalDate holidayOnSaturday = LocalDate.of(2025, 1, 4); // Saturday + holiday
        calendar.addHoliday(holidayOnSaturday);

        assertFalse(calendar.isBusinessDay(holidayOnSaturday));
        assertTrue(calendar.isHoliday(holidayOnSaturday));
        assertTrue(calendar.isWeekend(holidayOnSaturday));
    }

    /**
     * Simple test implementation of HolidayCalendar for testing default methods.
     */
    private static class TestHolidayCalendar implements HolidayCalendar {
        private final Set<LocalDate> holidays = new HashSet<>();

        void addHoliday(LocalDate date) {
            holidays.add(date);
        }

        @Override
        public boolean isHoliday(LocalDate date) {
            return holidays.contains(date);
        }

        @Override
        public Set<Holiday> getHolidays(int year) {
            Set<Holiday> result = new HashSet<>();
            for (LocalDate date : holidays) {
                if (date.getYear() == year) {
                    result.add(new Holiday(date, "Test Holiday", HolidayType.LEGAL));
                }
            }
            return result;
        }
    }
}
