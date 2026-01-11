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
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for reviewing 2026 Romanian holidays.
 */
class Holidays2026Test {

    @Test
    void testList2026Holidays() {
        RomanianHolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();
        Set<Holiday> holidays = calendar.getHolidays(2026);

        // Sort by date
        List<Holiday> sortedHolidays = holidays.stream()
                .filter(h -> h.getType() == HolidayType.LEGAL)
                .sorted(Comparator.comparing(Holiday::getDate))
                .toList();

        System.out.println("\n=== SĂRBĂTORI LEGALE 2026 ===\n");
        for (Holiday holiday : sortedHolidays) {
            DayOfWeek dayOfWeek = holiday.getDate().getDayOfWeek();
            System.out.printf("%s (%s) - %s - %s%n",
                    holiday.getDate(),
                    dayOfWeek,
                    holiday.getName(),
                    holiday.getType());
        }
        System.out.println("\nTotal: " + sortedHolidays.size() + " sărbători legale\n");

        // Verify key holidays
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 1, 1))));
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 1, 2))));
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 1, 24))));
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 4, 12)))); // Easter 2026
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 5, 1))));
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 12, 1))));
        assertTrue(sortedHolidays.stream()
                .anyMatch(h -> h.getDate().equals(LocalDate.of(2026, 12, 25))));
    }
}
