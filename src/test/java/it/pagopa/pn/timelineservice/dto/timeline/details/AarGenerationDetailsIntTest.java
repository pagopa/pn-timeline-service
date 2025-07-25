package it.pagopa.pn.timelineservice.dto.timeline.details;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AarGenerationDetailsIntTest {

    private AarGenerationDetailsInt detailsInt;

    @BeforeEach
    void setUp() {
        detailsInt = new AarGenerationDetailsInt();
        detailsInt.setRecIndex(1);
        detailsInt.setGeneratedAarUrl("url");
        detailsInt.setNumberOfPages(2);
    }
    @Test
    void toLog() {
        Assertions.assertEquals("recIndex=1", detailsInt.toLog());
    }
    @Test
    void testEquals() {
        AarGenerationDetailsInt expected = buildAarGenerationDetailsInt();
        Assertions.assertEquals(expected.getGeneratedAarUrl(), detailsInt.getGeneratedAarUrl());
        Assertions.assertEquals(expected.getNumberOfPages(), detailsInt.getNumberOfPages());
        Assertions.assertEquals(expected.getRecIndex(), detailsInt.getRecIndex());
    }
    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, detailsInt.getRecIndex());
    }
    @Test
    void getGeneratedAarUrl() {
        Assertions.assertEquals("url", detailsInt.getGeneratedAarUrl());
    }
    @Test
    void getNumberOfPages() {
        Assertions.assertEquals(2, detailsInt.getNumberOfPages());
    }
    @Test
    void testToString() {
        String expected = "AarGenerationDetailsInt(recIndex=1, generatedAarUrl=url, numberOfPages=2)";
        Assertions.assertEquals(expected, detailsInt.toString());
    }
    private AarGenerationDetailsInt buildAarGenerationDetailsInt(){
        return AarGenerationDetailsInt.builder()
                .recIndex(1)
                .numberOfPages(2)
                .generatedAarUrl("url")
                .build();
    }
}