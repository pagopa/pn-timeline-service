package it.pagopa.pn.timelineservice.strategy;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimelineStrategyResolverTest {
    @Test
    void resolvesCorrectBundleForGivenCommunicationType() {
        TimelineStrategyBundle bundle = Mockito.mock(TimelineStrategyBundle.class);
        Mockito.when(bundle.supportedType()).thenReturn(CommunicationType.LEGAL);

        TimelineStrategyResolver resolver = new TimelineStrategyResolver(List.of(bundle));

        assertEquals(bundle, resolver.resolve(CommunicationType.LEGAL));
    }

    @Test
    void throwsExceptionWhenCommunicationTypeNotSupported() {
        TimelineStrategyResolver resolver = new TimelineStrategyResolver(List.of());

        assertThrows(PnInternalException.class, () -> resolver.resolve(CommunicationType.LEGAL));
    }

    @Test
    void throwsExceptionWhenDuplicateBundleForSameCommunicationType() {
        TimelineStrategyBundle bundle1 = Mockito.mock(TimelineStrategyBundle.class);
        TimelineStrategyBundle bundle2 = Mockito.mock(TimelineStrategyBundle.class);
        Mockito.when(bundle1.supportedType()).thenReturn(CommunicationType.LEGAL);
        Mockito.when(bundle2.supportedType()).thenReturn(CommunicationType.LEGAL);

        assertThrows(PnInternalException.class, () -> new TimelineStrategyResolver(List.of(bundle1, bundle2)));
    }

    @Test
    void resolvesCorrectBundleWhenMultipleBundlesRegistered() {
        TimelineStrategyBundle legalBundle = Mockito.mock(TimelineStrategyBundle.class);
        TimelineStrategyBundle informalBundle = Mockito.mock(TimelineStrategyBundle.class);
        Mockito.when(legalBundle.supportedType()).thenReturn(CommunicationType.LEGAL);
        Mockito.when(informalBundle.supportedType()).thenReturn(CommunicationType.INFORMAL);

        TimelineStrategyResolver resolver = new TimelineStrategyResolver(List.of(legalBundle, informalBundle));

        assertEquals(legalBundle, resolver.resolve(CommunicationType.LEGAL));
        assertEquals(informalBundle, resolver.resolve(CommunicationType.INFORMAL));
    }
}