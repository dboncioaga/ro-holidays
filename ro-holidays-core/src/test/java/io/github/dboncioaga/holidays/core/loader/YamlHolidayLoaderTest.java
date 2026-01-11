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
package io.github.dboncioaga.holidays.core.loader;

import org.junit.jupiter.api.Test;
import io.github.dboncioaga.holidays.core.Holiday;
import io.github.dboncioaga.holidays.core.HolidayRule;
import io.github.dboncioaga.holidays.core.HolidayType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link YamlHolidayLoader}.
 */
class YamlHolidayLoaderTest {

    @Test
    void testLoadDefaultRules() {
        List<HolidayRule> rules = YamlHolidayLoader.loadDefaultRules();

        assertNotNull(rules);
        assertFalse(rules.isEmpty());

        // Verify at least 5 Romanian fixed holidays are included
        assertTrue(rules.size() >= 5, "Expected at least 5 fixed holidays");

        // Check for some known holidays
        boolean hasNewYear = false;
        boolean hasNationalDay = false;
        boolean hasChristmas = false;

        for (HolidayRule rule : rules) {
            Set<Holiday> holidays = rule.holidaysFor(2025);
            for (Holiday holiday : holidays) {
                if (holiday.getDate().equals(LocalDate.of(2025, 1, 1))) {
                    hasNewYear = true;
                }
                if (holiday.getDate().equals(LocalDate.of(2025, 12, 1))) {
                    hasNationalDay = true;
                }
                if (holiday.getDate().equals(LocalDate.of(2025, 12, 25))) {
                    hasChristmas = true;
                }
            }
        }

        assertTrue(hasNewYear, "Expected New Year's Day");
        assertTrue(hasNationalDay, "Expected National Day");
        assertTrue(hasChristmas, "Expected Christmas");
    }

    @Test
    void testLoadSimpleYaml() {
        String yaml = """
                fixed:
                  - month: 1
                    day: 1
                    name: Test Holiday
                    type: LEGAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
        List<HolidayRule> rules = YamlHolidayLoader.loadRules(input);

        assertEquals(1, rules.size());

        Set<Holiday> holidays = rules.get(0).holidaysFor(2025);
        assertEquals(1, holidays.size());

        Holiday holiday = holidays.iterator().next();
        assertEquals(LocalDate.of(2025, 1, 1), holiday.getDate());
        assertEquals("Test Holiday", holiday.getName());
        assertEquals(HolidayType.LEGAL, holiday.getType());
    }

    @Test
    void testLoadYamlWithYearRange() {
        String yaml = """
                fixed:
                  - month: 5
                    day: 1
                    name: Test Holiday
                    type: LEGAL
                    from: 2020
                    to: 2030
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
        List<HolidayRule> rules = YamlHolidayLoader.loadRules(input);

        assertEquals(1, rules.size());

        HolidayRule rule = rules.get(0);
        assertTrue(rule.holidaysFor(2019).isEmpty());
        assertEquals(1, rule.holidaysFor(2020).size());
        assertEquals(1, rule.holidaysFor(2025).size());
        assertEquals(1, rule.holidaysFor(2030).size());
        assertTrue(rule.holidaysFor(2031).isEmpty());
    }

    @Test
    void testLoadYamlWithMultipleHolidays() {
        String yaml = """
                fixed:
                  - month: 1
                    day: 1
                    name: New Year
                    type: LEGAL
                  - month: 12
                    day: 25
                    name: Christmas
                    type: LEGAL
                  - month: 6
                    day: 1
                    name: Children's Day
                    type: OPTIONAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
        List<HolidayRule> rules = YamlHolidayLoader.loadRules(input);

        assertEquals(3, rules.size());

        Set<Holiday> holidays2025 = rules.stream()
                .flatMap(rule -> rule.holidaysFor(2025).stream())
                .collect(java.util.stream.Collectors.toSet());

        assertEquals(3, holidays2025.size());
    }

    @Test
    void testMissingFixedKey() {
        String yaml = """
                other:
                  - month: 1
                    day: 1
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testInvalidYaml() {
        String yaml = "invalid: [unclosed";

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testMissingRequiredField() {
        String yaml = """
                fixed:
                  - month: 1
                    name: Missing Day
                    type: LEGAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testInvalidHolidayType() {
        String yaml = """
                fixed:
                  - month: 1
                    day: 1
                    name: Test
                    type: INVALID_TYPE
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testInvalidMonth() {
        String yaml = """
                fixed:
                  - month: 13
                    day: 1
                    name: Test
                    type: LEGAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testInvalidDay() {
        String yaml = """
                fixed:
                  - month: 1
                    day: 32
                    name: Test
                    type: LEGAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testNonNumericMonth() {
        String yaml = """
                fixed:
                  - month: "January"
                    day: 1
                    name: Test
                    type: LEGAL
                """;

        InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));

        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules(input)
        );
    }

    @Test
    void testResourceNotFound() {
        assertThrows(HolidayLoadException.class, () ->
                YamlHolidayLoader.loadRules("/non-existent.yaml")
        );
    }
}
