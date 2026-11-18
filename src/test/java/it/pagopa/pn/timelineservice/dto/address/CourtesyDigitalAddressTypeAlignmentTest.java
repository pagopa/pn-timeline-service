package it.pagopa.pn.timelineservice.dto.address;

import it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity.DigitalAddressEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ogni canale di cortesia deve poter essere persistito nella timeline: un valore mancante in
 * {@link DigitalAddressEntity.TypeEnum} farebbe fallire il salvataggio di SEND_COURTESY_MESSAGE.
 */
class CourtesyDigitalAddressTypeAlignmentTest {

    @Test
    void everyCourtesyTypeIsPersistable() {
        for (CourtesyDigitalAddressInt.COURTESY_DIGITAL_ADDRESS_TYPE_INT type : CourtesyDigitalAddressInt.COURTESY_DIGITAL_ADDRESS_TYPE_INT.values()) {
            assertDoesNotThrow(() -> DigitalAddressEntity.TypeEnum.valueOf(type.name()), "Tipo non persistibile: " + type);
        }
    }

    @Test
    void whatsappIsSupported() {
        assertEquals("WHATSAPP", CourtesyDigitalAddressInt.COURTESY_DIGITAL_ADDRESS_TYPE_INT.WHATSAPP.getValue());
        assertEquals("WHATSAPP", DigitalAddressEntity.TypeEnum.WHATSAPP.getValue());
    }
}
