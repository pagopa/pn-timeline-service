package it.pagopa.pn.timelineservice.dto.timeline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimelineEventIdBuilderTest {

    @Test
    void buildWithOnlyCategory() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from(null);
        builder.withCategory("CATEGORY");
        String result = builder.build();
        assertEquals("CATEGORY", result);
    }

    @Test
    void buildWithCategoryAndIun() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from(null);
        builder.withCategory("CATEGORY");
        builder.withIun("12345");
        String result = builder.build();
        assertEquals("CATEGORY.IUN_12345", result);
    }

    @Test
    void buildWithAllFields() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from(null);
        builder.withCategory("CATEGORY");
        builder.withIun("12345");
        builder.withRecIndex(1);
        builder.withSentAttemptMade(2);
        builder.withReworkIdx(3);
        String result = builder.build();
        assertEquals("CATEGORY.IUN_12345.RECINDEX_1.ATTEMPT_2.REWORK_3", result);
    }

    @Test
    void buildWithExistingId() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from("EXISTING_ID");
        builder.withIun("12345");
        String result = builder.build();
        assertEquals("EXISTING_ID.IUN_12345", result);
    }

    @Test
    void buildWithNullValues() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from(null);
        builder.withCategory("CATEGORY");
        builder.withIun(null);
        builder.withRecIndex(null);
        builder.withSentAttemptMade(null);
        builder.withReworkIdx(null);
        String result = builder.build();
        assertEquals("CATEGORY", result);
    }

    @Test
    void buildWithNegativeValues() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from(null);
        builder.withCategory("CATEGORY");
        builder.withSentAttemptMade(-1);
        builder.withReworkIdx(-1);
        String result = builder.build();
        assertEquals("CATEGORY", result);
    }

    @Test
    void buildWithEmptyExistingId() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from("");
        builder.withCategory("CATEGORY");
        String result = builder.build();
        assertEquals("CATEGORY", result);
    }

    @Test
    void buildWithBlankExistingId() {
        TimelineEventIdBuilder builder = TimelineEventIdBuilder.from("   ");
        builder.withCategory("CATEGORY");
        String result = builder.build();
        assertEquals("CATEGORY", result);
    }
}