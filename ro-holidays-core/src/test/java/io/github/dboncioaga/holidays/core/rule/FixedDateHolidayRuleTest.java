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
 * Unit tests for {@link FixedDateHolidayRule}.
 */
class FixedDateHolidayRuleTest {

    @Test
    void testBasicFixedHoliday() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                1, 1, "Anul Nou", HolidayType.LEGAL, null, null
        );

        Set<Holiday> holidays2025 = rule.holidaysFor(2025);
        assertEquals(1, holidays2025.size());

        Holiday holiday = holidays2025.iterator().next();
        assertEquals(LocalDate.of(2025, 1, 1), holiday.getDate());
        assertEquals("Anul Nou", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testFixedHolidayWithFromYear() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                1, 24, "Ziua Unirii", HolidayType.LEGAL, 2017, null
        );

        // Before fromYear
        assertTrue(rule.holidaysFor(2016).isEmpty());

        // At fromYear
        assertEquals(1, rule.holidaysFor(2017).size());

        // After fromYear
        assertEquals(1, rule.holidaysFor(2025).size());
    }

    @Test
    void testFixedHolidayWithToYear() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                5, 1, "Ziua Muncii", HolidayType.LEGAL, null, 2030
        );

        // Before toYear
        assertEquals(1, rule.holidaysFor(2025).size());

        // At toYear
        assertEquals(1, rule.holidaysFor(2030).size());

        // After toYear
        assertTrue(rule.holidaysFor(2031).isEmpty());
    }

    @Test
    void testFixedHolidayWithYearRange() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                12, 25, "Crăciunul", HolidayType.LEGAL, 2000, 2050
        );

        assertTrue(rule.holidaysFor(1999).isEmpty());
        assertEquals(1, rule.holidaysFor(2000).size());
        assertEquals(1, rule.holidaysFor(2025).size());
        assertEquals(1, rule.holidaysFor(2050).size());
        assertTrue(rule.holidaysFor(2051).isEmpty());
    }

    @Test
    void testLeapYearDate() {
        // February 29 is valid only in leap years
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                2, 29, "Leap Day", HolidayType.OPTIONAL, null, null
        );

        // 2024 is a leap year
        assertEquals(1, rule.holidaysFor(2024).size());

        // 2025 is not a leap year
        assertTrue(rule.holidaysFor(2025).isEmpty());

        // 2028 is a leap year
        assertEquals(1, rule.holidaysFor(2028).size());
    }

    @Test
    void testInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(0, 1, "Invalid", HolidayType.LEGAL, null, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(13, 1, "Invalid", HolidayType.LEGAL, null, null)
        );
    }

    @Test
    void testInvalidDay() {
        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(1, 0, "Invalid", HolidayType.LEGAL, null, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(1, 32, "Invalid", HolidayType.LEGAL, null, null)
        );
    }

    @Test
    void testInvalidDate() {
        // February 30 is never valid
        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(2, 30, "Invalid", HolidayType.LEGAL, null, null)
        );
    }

    @Test
    void testNullName() {
        assertThrows(NullPointerException.class, () ->
                new FixedDateHolidayRule(1, 1, null, HolidayType.LEGAL, null, null)
        );
    }

    @Test
    void testBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(1, 1, "   ", HolidayType.LEGAL, null, null)
        );
    }

    @Test
    void testNullType() {
        assertThrows(NullPointerException.class, () ->
                new FixedDateHolidayRule(1, 1, "Name", null, null, null)
        );
    }

    @Test
    void testInvalidYearRange() {
        assertThrows(IllegalArgumentException.class, () ->
                new FixedDateHolidayRule(1, 1, "Name", HolidayType.LEGAL, 2030, 2020)
        );
    }

    @Test
    void testGetters() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                12, 25, "Crăciunul", HolidayType.LEGAL, 2000, 2050
        );

        assertEquals(12, rule.getMonth());
        assertEquals(25, rule.getDay());
        assertEquals("Crăciunul", rule.getName());
        assertEquals(HolidayType.LEGAL, rule.getType());
        assertEquals(2000, rule.getFromYear());
        assertEquals(2050, rule.getToYear());
    }

    @Test
    void testToString() {
        FixedDateHolidayRule rule = new FixedDateHolidayRule(
                1, 1, "Anul Nou", HolidayType.LEGAL, 2000, 2050
        );

        String result = rule.toString();
        assertTrue(result.contains("month=1"));
        assertTrue(result.contains("day=1"));
        assertTrue(result.contains("Anul Nou"));
        assertTrue(result.contains("LEGAL"));
        assertTrue(result.contains("fromYear=2000"));
        assertTrue(result.contains("toYear=2050"));
    }
}
