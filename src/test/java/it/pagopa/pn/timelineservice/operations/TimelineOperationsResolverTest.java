package it.pagopa.pn.timelineservice.operations;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.timelineservice.dto.timeline.CommunicationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimelineOperationsResolverTest {
    @Test
    void resolvesCorrectBundleForGivenCommunicationType() {
        TimelineOperations bundle = Mockito.mock(TimelineOperations.class);
        Mockito.when(bundle.supportedType()).thenReturn(CommunicationType.LEGAL);

        TimelineOperationsResolver resolver = new TimelineOperationsResolver(List.of(bundle));

        assertEquals(bundle, resolver.resolve(CommunicationType.LEGAL));
    }

    @Test
    void throwsExceptionWhenCommunicationTypeNotSupported() {
        TimelineOperationsResolver resolver = new TimelineOperationsResolver(List.of());

        assertThrows(PnInternalException.class, () -> resolver.resolve(CommunicationType.LEGAL));
    }

    @Test
    void throwsExceptionWhenDuplicateBundleForSameCommunicationType() {
        TimelineOperations bundle1 = Mockito.mock(TimelineOperations.class);
        TimelineOperations bundle2 = Mockito.mock(TimelineOperations.class);
        Mockito.when(bundle1.supportedType()).thenReturn(CommunicationType.LEGAL);
        Mockito.when(bundle2.supportedType()).thenReturn(CommunicationType.LEGAL);

        assertThrows(PnInternalException.class, () -> new TimelineOperationsResolver(List.of(bundle1, bundle2)));
    }

    @Test
    void resolvesCorrectBundleWhenMultipleBundlesRegistered() {
        TimelineOperations legalBundle = Mockito.mock(TimelineOperations.class);
        TimelineOperations informalBundle = Mockito.mock(TimelineOperations.class);
        Mockito.when(legalBundle.supportedType()).thenReturn(CommunicationType.LEGAL);
        Mockito.when(informalBundle.supportedType()).thenReturn(CommunicationType.INFORMAL);

        TimelineOperationsResolver resolver = new TimelineOperationsResolver(List.of(legalBundle, informalBundle));

        assertEquals(legalBundle, resolver.resolve(CommunicationType.LEGAL));
        assertEquals(informalBundle, resolver.resolve(CommunicationType.INFORMAL));
    }
}