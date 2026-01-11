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

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import io.github.dboncioaga.holidays.core.BusinessDays;
import io.github.dboncioaga.holidays.core.HolidayCalendar;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RomanianHolidaysAutoConfiguration}.
 */
class RomanianHolidaysAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RomanianHolidaysAutoConfiguration.class));

    @Test
    void testAutoConfigurationLoads() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(HolidayCalendar.class);
            assertThat(context).hasSingleBean(BusinessDays.class);
        });
    }

    @Test
    void testHolidayCalendarBean() {
        contextRunner.run(context -> {
            HolidayCalendar calendar = context.getBean(HolidayCalendar.class);
            assertThat(calendar).isNotNull();

            // Verify it recognizes Romanian holidays
            LocalDate newYear = LocalDate.of(2025, 1, 1);
            assertThat(calendar.isHoliday(newYear)).isTrue();
        });
    }

    @Test
    void testBusinessDaysBean() {
        contextRunner.run(context -> {
            BusinessDays businessDays = context.getBean(BusinessDays.class);
            assertThat(businessDays).isNotNull();
            assertThat(businessDays.getCalendar()).isNotNull();
        });
    }

    @Test
    void testExcludeHolidaysConfiguration() {
        contextRunner
                .withPropertyValues("ro.holidays.exclude[0]=2025-05-01")
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    // Labour Day should be excluded
                    LocalDate labourDay = LocalDate.of(2025, 5, 1);
                    assertThat(calendar.isHoliday(labourDay)).isFalse();

                    // But other holidays should still work
                    LocalDate newYear = LocalDate.of(2025, 1, 1);
                    assertThat(calendar.isHoliday(newYear)).isTrue();
                });
    }

    @Test
    void testCustomHolidaysAndExcludeBothWork() {
        contextRunner
                .withPropertyValues(
                        "ro.holidays.custom-holidays.2026-03-15=Company Anniversary",
                        "ro.holidays.exclude[0]=2025-05-01"
                )
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    LocalDate customHoliday = LocalDate.of(2026, 3, 15);
                    assertThat(calendar.isHoliday(customHoliday)).isTrue();

                    LocalDate excludedHoliday = LocalDate.of(2025, 5, 1);
                    assertThat(calendar.isHoliday(excludedHoliday)).isFalse();
                });
    }

    @Test
    void testFloatWeekendHolidaysDisabledByDefault() {
        contextRunner.run(context -> {
            HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

            // Jan 24, 2026 is Saturday (Ziua Unirii)
            LocalDate saturday = LocalDate.of(2026, 1, 24);
            assertThat(calendar.isHoliday(saturday)).isTrue();

            // Friday should not be a holiday when floating disabled
            LocalDate friday = LocalDate.of(2026, 1, 23);
            assertThat(calendar.isHoliday(friday)).isFalse();
        });
    }

    @Test
    void testFloatWeekendHolidaysEnabled() {
        contextRunner
                .withPropertyValues("ro.holidays.float-weekend-holidays=true")
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    // Jan 24, 2026 is Saturday (Ziua Unirii)
                    // With floating enabled, both Friday AND Saturday are holidays
                    LocalDate friday = LocalDate.of(2026, 1, 23);
                    assertThat(calendar.isHoliday(friday)).isTrue();

                    // Original Saturday is also still a holiday
                    LocalDate saturday = LocalDate.of(2026, 1, 24);
                    assertThat(calendar.isHoliday(saturday)).isTrue();
                });
    }

    @Test
    void testFloatWeekendHolidaysWithBusinessDays() {
        contextRunner
                .withPropertyValues("ro.holidays.float-weekend-holidays=true")
                .run(context -> {
                    BusinessDays businessDays = context.getBean(BusinessDays.class);
                    HolidayCalendar calendar = businessDays.getCalendar();

                    // Jan 23, 2026 (Friday) is now a holiday (floated from Saturday)
                    LocalDate jan23 = LocalDate.of(2026, 1, 23);
                    assertThat(calendar.isBusinessDay(jan23)).isFalse();

                    // Jan 22, 2026 (Thursday) should be a business day
                    LocalDate jan22 = LocalDate.of(2026, 1, 22);
                    assertThat(calendar.isBusinessDay(jan22)).isTrue();

                    // Next business day from Jan 22 should skip floated Friday, Sat, Sun
                    LocalDate nextBD = businessDays.nextBusinessDay(jan22);
                    assertThat(nextBD).isEqualTo(LocalDate.of(2026, 1, 26));
                });
    }

    @Test
    void testCustomHolidays() {
        contextRunner
                .withPropertyValues(
                        "ro.holidays.custom-holidays.2026-03-15=Company Anniversary",
                        "ro.holidays.custom-holidays.2026-12-24=Christmas Eve"
                )
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    LocalDate anniversary = LocalDate.of(2026, 3, 15);
                    assertThat(calendar.isHoliday(anniversary)).isTrue();

                    LocalDate christmasEve = LocalDate.of(2026, 12, 24);
                    assertThat(calendar.isHoliday(christmasEve)).isTrue();

                    // Standard holidays should still work
                    LocalDate newYear = LocalDate.of(2026, 1, 1);
                    assertThat(calendar.isHoliday(newYear)).isTrue();
                });
    }

    @Test
    void testCustomHolidaysWithFloating() {
        contextRunner
                .withPropertyValues(
                        "ro.holidays.float-weekend-holidays=true",
                        "ro.holidays.custom-holidays.2026-03-17=Company Free Day"
                )
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    // Custom holiday on Tuesday
                    LocalDate customDay = LocalDate.of(2026, 3, 17);
                    assertThat(calendar.isHoliday(customDay)).isTrue();

                    // Floated holiday
                    LocalDate floatedFriday = LocalDate.of(2026, 1, 23);
                    assertThat(calendar.isHoliday(floatedFriday)).isTrue();
                });
    }

    @Test
    void testNonFloatableHolidaysConfiguration() {
        contextRunner
                .withPropertyValues(
                        "ro.holidays.float-weekend-holidays=true",
                        "ro.holidays.non-floatable-holidays[0]=Ziua Unirii Principatelor Române"
                )
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    // Jan 24, 2026 is Saturday (Ziua Unirii)
                    LocalDate saturday = LocalDate.of(2026, 1, 24);
                    assertThat(calendar.isHoliday(saturday)).isTrue();

                    // Friday should NOT be a holiday (floating disabled for this holiday)
                    LocalDate friday = LocalDate.of(2026, 1, 23);
                    assertThat(calendar.isHoliday(friday)).isFalse();
                });
    }

    @Test
    void testNonFloatableHolidaysMultiple() {
        contextRunner
                .withPropertyValues(
                        "ro.holidays.float-weekend-holidays=true",
                        "ro.holidays.non-floatable-holidays[0]=Ziua Unirii Principatelor Române",
                        "ro.holidays.non-floatable-holidays[1]=Crăciunul (ziua a doua)"
                )
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);

                    // Jan 24, 2026 is Saturday - should not float
                    LocalDate jan24 = LocalDate.of(2026, 1, 24);
                    LocalDate jan23 = LocalDate.of(2026, 1, 23);
                    assertThat(calendar.isHoliday(jan24)).isTrue();
                    assertThat(calendar.isHoliday(jan23)).isFalse();

                    // Dec 26, 2026 is Saturday - should not float
                    LocalDate dec26 = LocalDate.of(2026, 12, 26);
                    LocalDate dec25 = LocalDate.of(2026, 12, 25);
                    assertThat(calendar.isHoliday(dec26)).isTrue();
                    // Dec 25 is already a holiday (Crăciunul), but it's Friday so no floating needed

                    // Aug 15, 2026 is Saturday - this SHOULD float (not in non-floatable list)
                    LocalDate aug15 = LocalDate.of(2026, 8, 15);
                    LocalDate aug14 = LocalDate.of(2026, 8, 14);
                    assertThat(calendar.isHoliday(aug15)).isTrue();
                    assertThat(calendar.isHoliday(aug14)).isTrue(); // Should be floated
                });
    }

    @Test
    void testDecember2026FloatingScenario() {
        contextRunner
                .withPropertyValues("ro.holidays.float-weekend-holidays=true")
                .run(context -> {
                    HolidayCalendar calendar = context.getBean(HolidayCalendar.class);
                    
                    // Dec 25, 2026 is Friday (Crăciunul)
                    LocalDate dec25 = LocalDate.of(2026, 12, 25);
                    assertThat(calendar.isHoliday(dec25)).isTrue();
                    
                    // Dec 26, 2026 is Saturday (Crăciunul ziua a doua)
                    LocalDate dec26 = LocalDate.of(2026, 12, 26);
                    assertThat(calendar.isHoliday(dec26)).isTrue();
                    
                    // Dec 27 is Sunday - not a holiday
                    LocalDate dec27 = LocalDate.of(2026, 12, 27);
                    assertThat(calendar.isHoliday(dec27)).isFalse();
                    
                    // Dec 28 is Monday - should be floating day for Dec 26 
                    // (since Friday Dec 25 is already a holiday)
                    LocalDate dec28 = LocalDate.of(2026, 12, 28);
                    assertThat(calendar.isHoliday(dec28)).isTrue();
                });
    }
}
