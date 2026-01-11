# ro-holidays

[![Build](https://github.com/dboncioaga/ro-holidays/actions/workflows/build.yml/badge.svg)](https://github.com/dboncioaga/ro-holidays/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A production-ready Java library for Romanian public holidays and business-day calculations.

## Quick Start

```java
// Get the default Romanian holiday calendar
HolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();

// Check if a date is a holiday
LocalDate date = LocalDate.of(2025, 1, 1);
boolean isHoliday = calendar.isHoliday(date); // true - New Year

// Check if it's a business day
boolean isBusinessDay = calendar.isBusinessDay(date); // false

// Get all holidays for a year
Set<Holiday> holidays2025 = calendar.getHolidays(2025);
```

For Spring Boot applications, just add the dependency and the beans are auto-configured.

## Features

- ✅ **Complete Romanian holiday calendar** with all legal public holidays
- ✅ **Orthodox Easter calculation** using the Meeus/Jones/Butcher algorithm
- ✅ **Business day operations** (next, previous, add/subtract business days)
- ✅ **Floatable weekend holidays** - optional feature to move weekend holidays to nearest working day
- ✅ **Custom holidays support** - add company-specific or special event holidays
- ✅ **Non-floatable holidays** - exclude specific holidays from floating
- ✅ **YAML-driven configuration** for easy maintenance
- ✅ **Spring Boot integration** with auto-configuration
- ✅ **Immutable and thread-safe** after initialization
- ✅ **JSpecify annotations** for enhanced null safety
- ✅ **Comprehensive test coverage** (155 tests)
- ✅ **Static code analysis** with Checkstyle and PMD
- ✅ **Apache 2.0 licensed** for commercial and open-source use

## Requirements

- Java 25 or higher
- Maven 3.8+ (for building from source)

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.dboncioaga</groupId>
    <artifactId>ro-holidays-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

For Spring Boot integration:

```xml
<dependency>
    <groupId>io.github.dboncioaga</groupId>
    <artifactId>ro-holidays-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

### Plain Java

```java
import io.github.dboncioaga.holidays.core.RomanianHolidayCalendar;
import io.github.dboncioaga.holidays.core.HolidayCalendar;
import io.github.dboncioaga.holidays.core.Holiday;
import java.time.LocalDate;
import java.util.Set;

// Create a calendar instance
HolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();

// Create a calendar with weekend holiday floating enabled
// (holidays falling on weekends move to nearest working day)
HolidayCalendar floatingCalendar = RomanianHolidayCalendar.loadDefault(true);

// Create a calendar with custom holidays using builder
HolidayCalendar customCalendar = RomanianHolidayCalendar.builder()
    .addCustomHoliday(LocalDate.of(2026, 3, 15), "Company Anniversary")
    .addCustomHoliday(LocalDate.of(2026, 12, 24), "Christmas Eve")
    .floatWeekendHolidays(true)
    .build();

// Check if a date is a holiday
LocalDate newYear = LocalDate.of(2025, 1, 1);
boolean isHoliday = calendar.isHoliday(newYear); // true

// Check if a date is a business day
LocalDate regularDay = LocalDate.of(2025, 1, 8);
boolean isBusinessDay = calendar.isBusinessDay(regularDay); // true

// Get all holidays for a year
Set<Holiday> holidays2025 = calendar.getHolidays(2025);
holidays2025.forEach(holiday -> 
    System.out.println(holiday.getDate() + ": " + holiday.getName())
);
```

### Business Day Operations

```java
import io.github.dboncioaga.holidays.core.BusinessDays;
import java.time.LocalDate;

HolidayCalendar calendar = RomanianHolidayCalendar.loadDefault();
BusinessDays businessDays = new BusinessDays(calendar);

LocalDate today = LocalDate.now();

// Get the next business day
LocalDate next = businessDays.nextBusinessDay(today);

// Get the previous business day
LocalDate previous = businessDays.previousBusinessDay(today);

// Add 5 business days
LocalDate future = businessDays.addBusinessDays(today, 5);

// Subtract 3 business days
LocalDate past = businessDays.addBusinessDays(today, -3);
```

### Spring Boot Integration

Add the Spring dependency and the beans will be auto-configured:

```java
import org.springframework.stereotype.Service;
import io.github.dboncioaga.holidays.core.HolidayCalendar;
import io.github.dboncioaga.holidays.core.BusinessDays;

@Service
public class MyService {
    
    private final HolidayCalendar calendar;
    private final BusinessDays businessDays;
    
    // Beans are automatically injected
    public MyService(HolidayCalendar calendar, BusinessDays businessDays) {
        this.calendar = calendar;
        this.businessDays = businessDays;
    }
    
    public boolean isWorkingDay(LocalDate date) {
        return calendar.isBusinessDay(date);
    }
}
```

### Spring Configuration

Customize the holiday calendar in `application.yml`:

```yaml
ro:
  holidays:
    # Enable floating of weekend holidays to nearest working day
    # Saturday holidays move to Friday, Sunday holidays move to Monday
    float-weekend-holidays: true
    # Custom holidays with names (recommended)
    custom-holidays:
      2026-03-15: "Company Anniversary"
      2026-12-24: "Christmas Eve"
      2026-07-15: "Summer Break"
    # Exclude specific standard holidays
    exclude:
      - 2025-05-02  # Exclude a specific date from holidays
```

Or in `application.properties`:

```properties
# Enable weekend holiday floating
ro.holidays.float-weekend-holidays=true
# Custom holidays with names (recommended)
ro.holidays.custom-holidays.2026-03-15=Company Anniversary
ro.holidays.custom-holidays.2026-12-24=Christmas Eve
# Exclude specific holidays
ro.holidays.exclude[0]=2025-05-02
```

## Custom Holidays

The library supports adding custom holidays beyond the standard Romanian public holidays. This is useful for:
- Company-specific free days
- Special events
- Industry-specific holidays
- Any additional days you want to treat as holidays

### Plain Java

Use the builder pattern to add custom holidays:

```java
HolidayCalendar calendar = RomanianHolidayCalendar.builder()
    .addCustomHoliday(LocalDate.of(2026, 3, 15), "Company Anniversary")
    .addCustomHoliday(LocalDate.of(2026, 12, 24), "Christmas Eve")
    .build();

// Check custom holidays
calendar.isHoliday(LocalDate.of(2026, 3, 15)); // true
calendar.isBusinessDay(LocalDate.of(2026, 3, 15)); // false

// Add multiple at once
Map<LocalDate, String> companyHolidays = Map.of(
    LocalDate.of(2026, 3, 15), "Company Anniversary",
    LocalDate.of(2026, 7, 15), "Summer Break",
    LocalDate.of(2026, 12, 24), "Christmas Eve"
);

HolidayCalendar calendar = RomanianHolidayCalendar.builder()
    .addCustomHolidays(companyHolidays)
    .floatWeekendHolidays(true) // Optional: also enable floating
    .build();
```

### Spring Boot

Configure custom holidays in your `application.yml`:

```yaml
ro:
  holidays:
    custom-holidays:
      2026-03-15: "Company Anniversary"
      2026-12-24: "Christmas Eve"
      2026-07-15: "Summer Break"
```

Or in `application.properties`:

```properties
ro.holidays.custom-holidays.2026-03-15=Company Anniversary
ro.holidays.custom-holidays.2026-12-24=Christmas Eve
ro.holidays.custom-holidays.2026-07-15=Summer Break
```

Custom holidays are marked as `OPTIONAL` type by default and appear in the holiday calendar alongside standard Romanian holidays.

## Floatable Weekend Holidays

The library supports an optional feature to automatically move holidays that fall on weekends to the nearest working day. This is useful for organizations that observe this practice.

**How it works:**
- **Saturday holidays** → Friday is added as a floating day (if it's a working day), otherwise Monday or the next business day
- **Sunday holidays** → Monday is added as a floating day (or the next business day if Monday is also a holiday)
- The original weekend holiday remains a holiday
- Floated holidays are marked with "FD" suffix (Floating Day)
- Result: More holidays when floating is enabled (both weekend and weekday dates)

**Example:**

```java
// Without floating (default)
HolidayCalendar calendar = RomanianHolidayCalendar.loadDefault(false);
LocalDate jan24_2026 = LocalDate.of(2026, 1, 24); // Saturday - Union Day
calendar.isHoliday(jan24_2026); // true (holiday on Saturday)
calendar.isHoliday(jan24_2026.minusDays(1)); // false (Friday Jan 23 is working day)

// With floating enabled
HolidayCalendar floatingCalendar = RomanianHolidayCalendar.loadDefault(true);
floatingCalendar.isHoliday(jan24_2026); // true (Saturday still holiday)
floatingCalendar.isHoliday(jan24_2026.minusDays(1)); // true (Friday Jan 23 is now floating day)
// Result: 3-day weekend (Fri Jan 23, Sat Jan 24, Sun Jan 25)
```

**Spring Boot configuration:**

```yaml
ro:
  holidays:
    float-weekend-holidays: true  # Enable floating
    # Optional: specify holidays that should NOT be floated
    non-floatable-holidays:
      - "Crăciunul"
      - "Ziua Națională a României"
```

**Non-floatable holidays:**

You can exclude specific holidays from floating, even when floating is enabled. This is useful for holidays of special significance that must be observed on their actual date:

```java
// Using builder
HolidayCalendar calendar = RomanianHolidayCalendar.builder()
    .floatWeekendHolidays(true)
    .addNonFloatableHoliday("Crăciunul")
    .addNonFloatableHoliday("Ziua Națională a României")
    .build();

// Or add multiple at once
Set<String> nonFloatable = Set.of(
    "Ziua Unirii Principatelor Române",
    "Crăciunul",
    "Ziua Națională a României"
);

HolidayCalendar calendar = RomanianHolidayCalendar.builder()
    .floatWeekendHolidays(true)
    .addNonFloatableHolidays(nonFloatable)
    .build();
```

**Available holiday names for non-floatable configuration:**
- `Anul Nou`
- `Anul Nou (ziua a doua)`
- `Boboteaza`
- `Soborul Sfântului Ioan Botezătorul`
- `Ziua Unirii Principatelor Române`
- `Vinerea Mare`
- `Paștele`
- `A doua zi de Paște`
- `Ziua Muncii`
- `Ziua Copilului`
- `Rusalii`
- `A doua zi de Rusalii`
- `Adormirea Maicii Domnului`
- `Sfântul Andrei`
- `Ziua Națională a României`
- `Crăciunul`
- `Crăciunul (ziua a doua)`

**Note:** This feature is **disabled by default** to maintain strict compliance with Romanian labor law, where holidays are observed on their actual dates. Enable it only if your organization follows this practice. When enabled, you get **additional** holidays (original weekend dates are kept, plus the floated weekday dates).

## Romanian Holidays Included

### Fixed Holidays

- **January 1-2**: Anul Nou (New Year)
- **January 6**: Boboteaza (Epiphany)
- **January 7**: Soborul Sfântului Ioan Botezătorul (St. John the Baptist)
- **January 24**: Ziua Unirii Principatelor Române (Union Day, from 2017)
- **May 1**: Ziua Muncii (Labour Day)
- **June 1**: Ziua Copilului (Children's Day)
- **August 15**: Adormirea Maicii Domnului (Assumption of Mary)
- **November 30**: Sfântul Andrei (St. Andrew's Day)
- **December 1**: Ziua Națională a României (National Day)
- **December 25-26**: Crăciunul (Christmas)

### Movable Holidays (Based on Orthodox Easter)

- **Good Friday** (Easter - 2 days): Vinerea Mare
- **Easter Sunday**: Paștele
- **Easter Monday** (Easter + 1 day): A doua zi de Paște
- **Pentecost** (Easter + 49 days): Rusalii
- **Pentecost Monday** (Easter + 50 days): A doua zi de Rusalii

## Data Sources

Holiday information is based on Romanian labor law:
- Codul muncii
- Official Romanian government announcements

Orthodox Easter calculation uses the Meeus/Jones/Butcher algorithm adapted for the Julian calendar, validated against official dates from the Romanian Patriarchate.

## Architecture

### Modules

- **ro-holidays-core**: Core library with no external dependencies (except YAML parsing)
- **ro-holidays-spring**: Spring Boot auto-configuration and integration

### Design Principles

- **Immutable value objects** for thread safety
- **Data-driven** configuration via YAML
- **Strategy pattern** for holiday rules
- **Decorator pattern** for Spring configuration overrides
- **Caching** for performance (per-year holiday computation)
- **Null safety** with JSpecify annotations (`@NullMarked` packages)

## Building from Source

```bash
git clone https://github.com/dboncioaga/ro-holidays.git
cd ro-holidays
mvn clean verify
```

## Code Quality

The project uses static code analysis tools to maintain high code quality:

### Checkstyle
```bash
# Runs automatically during build
mvn compile

# Or run explicitly
mvn checkstyle:check
```

### PMD
```bash
# Run PMD analysis
mvn pmd:check
```

Configuration files:
- `checkstyle.xml` - Checkstyle rules (Google Java Style based)
- `pmd-ruleset.xml` - PMD rules (best practices, design, error-prone patterns)

## Contributing

Contributions are welcome! Please ensure:

1. All tests pass: `mvn test`
2. Code follows existing style
3. New features include tests and JavaDoc
4. Holiday data is accurate and sourced from official references

## Versioning

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking API changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

## License

Copyright 2026 dboncioaga

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Support

- **Issues**: [GitHub Issues](https://github.com/dboncioaga/ro-holidays/issues)
- **Documentation**: [JavaDoc](https://github.com/dboncioaga/ro-holidays)
- **Releases**: [GitHub Releases](https://github.com/dboncioaga/ro-holidays/releases)
