package it.pagopa.pn.timelineservice.dto.timeline;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimelineEventIdParserTest {

    @Test
    void parseReworkIndexFullFromValidEventId() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("CATEGORY.IUN_12345.REWORK_3");
        assertEquals(Optional.of("REWORK_3"), parser.reworkIndexFull());
    }

    @Test
    void parseReworkIndexFullFromEventIdWithoutRework() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("CATEGORY.IUN_12345");
        assertEquals(Optional.empty(), parser.reworkIndexFull());
    }

    @Test
    void parseReworkIndexFullFromEmptyEventId() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("");
        assertEquals(Optional.empty(), parser.reworkIndexFull());
    }

    @Test
    void parseReworkIndexFullFromNullEventId() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse(null);
        assertEquals(Optional.empty(), parser.reworkIndexFull());
    }

    @Test
    void toComponentsReturnsAllFieldsCorrectly() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("CATEGORY.IUN_12345.RECINDEX_1.ATTEMPT_2.REWORK_3");
        TimelineEventIdParser.TimelineEventIdComponents components = parser.toComponents();
        assertEquals("CATEGORY", components.category());
        assertEquals("12345", components.iun());
        assertEquals(1, components.recIndex());
        assertEquals(2, components.sentAttemptMade());
        assertEquals(3, components.reworkIndex());
        assertTrue(components.hasCategory());
        assertTrue(components.hasIun());
        assertTrue(components.hasRecIndex());
        assertTrue(components.hasSentAttemptMade());
        assertTrue(components.hasReworkIndex());
    }

    @Test
    void toComponentsWithMissingFieldsReturnsNullsAndHasMethodsReturnFalse() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("CATEGORY");
        TimelineEventIdParser.TimelineEventIdComponents components = parser.toComponents();
        assertEquals("CATEGORY", components.category());
        assertNull(components.iun());
        assertNull(components.recIndex());
        assertNull(components.sentAttemptMade());
        assertNull(components.reworkIndex());
        assertTrue(components.hasCategory());
        assertFalse(components.hasIun());
        assertFalse(components.hasRecIndex());
        assertFalse(components.hasSentAttemptMade());
        assertFalse(components.hasReworkIndex());
    }

    @Test
    void toComponentsWithEmptyEventIdReturnsAllNullsAndHasMethodsReturnFalse() {
        TimelineEventIdParser parser = TimelineEventIdParser.parse("");
        TimelineEventIdParser.TimelineEventIdComponents components = parser.toComponents();
        assertNull(components.category());
        assertNull(components.iun());
        assertNull(components.recIndex());
        assertNull(components.sentAttemptMade());
        assertNull(components.reworkIndex());
        assertFalse(components.hasCategory());
        assertFalse(components.hasIun());
        assertFalse(components.hasRecIndex());
        assertFalse(components.hasSentAttemptMade());
        assertFalse(components.hasReworkIndex());
    }
}