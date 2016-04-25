package com.ateolan.jumbcount;

import android.provider.BaseColumns;

/**
 * Created by cmetaxas on 25/04/2016.
 * Class for jumb entry table
 */
public final class JumbCounterStatistic {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public JumbCounterStatistic() {}

    /* Inner class that defines the table contents */
    public static abstract class JumbEntry implements BaseColumns {
        public static final String TABLE_NAME = "jumbentry";
        public static final String COLUMN_NAME_INCIDENT_DATE = "incidentdate";
        public static final String COLUMN_NAME_INCIDENT_COUNT = "incidentcount";
    }
}
