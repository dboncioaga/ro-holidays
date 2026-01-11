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
package io.github.dboncioaga.holidays.core.rule;

import org.junit.jupiter.api.Test;
import io.github.dboncioaga.holidays.core.Holiday;
import io.github.dboncioaga.holidays.core.HolidayType;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for movable holiday rules based on Orthodox Easter.
 */
class MovableHolidayRulesTest {

    @Test
    void testEasterSunday2025() {
        OrthodoxEasterSundayRule rule = new OrthodoxEasterSundayRule();
        Set<Holiday> holidays = rule.holidaysFor(2025);

        assertEquals(1, holidays.size());
        Holiday holiday = holidays.iterator().next();
        assertEquals(LocalDate.of(2025, 4, 20), holiday.getDate());
        assertEquals("Paștele", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testGoodFriday2025() {
        GoodFridayRule rule = new GoodFridayRule();
        Set<Holiday> holidays = rule.holidaysFor(2025);

        assertEquals(1, holidays.size());
        Holiday holiday = holidays.iterator().next();
        // Good Friday is Easter - 2 days
        assertEquals(LocalDate.of(2025, 4, 18), holiday.getDate());
        assertEquals("Vinerea Mare", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testEasterMonday2025() {
        EasterMondayRule rule = new EasterMondayRule();
        Set<Holiday> holidays = rule.holidaysFor(2025);

        assertEquals(1, holidays.size());
        Holiday holiday = holidays.iterator().next();
        // Easter Monday is Easter + 1 day
        assertEquals(LocalDate.of(2025, 4, 21), holiday.getDate());
        assertEquals("A doua zi de Paște", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testPentecostSunday2025() {
        PentecostSundayRule rule = new PentecostSundayRule();
        Set<Holiday> holidays = rule.holidaysFor(2025);

        assertEquals(1, holidays.size());
        Holiday holiday = holidays.iterator().next();
        // Pentecost is Easter + 49 days
        assertEquals(LocalDate.of(2025, 6, 8), holiday.getDate());
        assertEquals("Rusalii", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testPentecostMonday2025() {
        PentecostMondayRule rule = new PentecostMondayRule();
        Set<Holiday> holidays = rule.holidaysFor(2025);

        assertEquals(1, holidays.size());
        Holiday holiday = holidays.iterator().next();
        // Pentecost Monday is Easter + 50 days
        assertEquals(LocalDate.of(2025, 6, 9), holiday.getDate());
        assertEquals("A doua zi de Rusalii", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testAllMovableHolidaysFor2024() {
        // Easter 2024 is May 5
        LocalDate easter2024 = LocalDate.of(2024, 5, 5);

        GoodFridayRule goodFridayRule = new GoodFridayRule();
        assertEquals(easter2024.minusDays(2),
                goodFridayRule.holidaysFor(2024).iterator().next().getDate());

        OrthodoxEasterSundayRule easterRule = new OrthodoxEasterSundayRule();
        assertEquals(easter2024,
                easterRule.holidaysFor(2024).iterator().next().getDate());

        EasterMondayRule mondayRule = new EasterMondayRule();
        assertEquals(easter2024.plusDays(1),
                mondayRule.holidaysFor(2024).iterator().next().getDate());

        PentecostSundayRule pentecostRule = new PentecostSundayRule();
        assertEquals(easter2024.plusDays(49),
                pentecostRule.holidaysFor(2024).iterator().next().getDate());

        PentecostMondayRule pentecostMondayRule = new PentecostMondayRule();
        assertEquals(easter2024.plusDays(50),
                pentecostMondayRule.holidaysFor(2024).iterator().next().getDate());
    }

    @Test
    void testConsistencyAcrossYears() {
        // Verify all movable holidays are consistent for multiple years
        for (int year = 2020; year <= 2030; year++) {
            LocalDate easter = OrthodoxEasterCalculator.calculateEaster(year);

            GoodFridayRule goodFridayRule = new GoodFridayRule();
            Holiday goodFriday = goodFridayRule.holidaysFor(year).iterator().next();
            assertEquals(easter.minusDays(2), goodFriday.getDate(),
                    "Good Friday should be Easter - 2 for year " + year);

            EasterMondayRule mondayRule = new EasterMondayRule();
            Holiday easterMonday = mondayRule.holidaysFor(year).iterator().next();
            assertEquals(easter.plusDays(1), easterMonday.getDate(),
                    "Easter Monday should be Easter + 1 for year " + year);

            PentecostSundayRule pentecostRule = new PentecostSundayRule();
            Holiday pentecost = pentecostRule.holidaysFor(year).iterator().next();
            assertEquals(easter.plusDays(49), pentecost.getDate(),
                    "Pentecost should be Easter + 49 for year " + year);

            PentecostMondayRule pentecostMondayRule = new PentecostMondayRule();
            Holiday pentecostMonday = pentecostMondayRule.holidaysFor(year).iterator().next();
            assertEquals(easter.plusDays(50), pentecostMonday.getDate(),
                    "Pentecost Monday should be Easter + 50 for year " + year);
        }
    }
}
