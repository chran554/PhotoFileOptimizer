package se.cha;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;

import static org.junit.Assert.assertTrue;

public class DateParseTest {

    @Test
    public void testParse() {
        final String dateText = "2021-01-30T09:44:00Z";
        final SimpleDateFormat dateFormat = new SimpleDateFormat();

        final OffsetDateTime dateTime = OffsetDateTime.parse(dateText);
        System.out.println("date = " + dateTime);

        assertTrue(true);
    }

}
