package com.owen1212055.biomevisuals.parsers.booleans.impl;

import com.google.gson.*;
import com.owen1212055.biomevisuals.parsers.booleans.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

public class DateBooleanProvider implements BooleanProvider {

    @Override
    public String getKey() {
        return "date_range";
    }

    @Override
    public boolean parse(JsonObject object) {
        LocalDate from = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(object.get("min_date").getAsString()));
        LocalDate to = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(object.get("max_date").getAsString()));

        LocalDate now = LocalDate.now();
        boolean ignoreYear = object.get("ignore_year").getAsBoolean();
        if (ignoreYear) {
            from = from.with(ChronoField.YEAR, now.getYear());
            to = to.with(ChronoField.YEAR, now.getYear());
        }

        return now.isAfter(from) && now.isBefore(to);
    }

}
