package org.windowing.windowingproject.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeNumberTest {

    @Test
    void ordering_byPrimary() {
        assertTrue(new CompositeNumber(1, 99).compareTo(new CompositeNumber(2, 0)) < 0);
        assertTrue(new CompositeNumber(5, 0).compareTo(new CompositeNumber(3, 0)) > 0);
    }

    @Test
    void ordering_bySecondaryWhenPrimaryEqual() {
        assertTrue(new CompositeNumber(5, 1).compareTo(new CompositeNumber(5, 2)) < 0);
        assertTrue(new CompositeNumber(5, 3).compareTo(new CompositeNumber(5, 3)) == 0);
    }

    @Test
    void equality() {
        assertEquals(new CompositeNumber(3, 4), new CompositeNumber(3, 4));
        assertNotEquals(new CompositeNumber(3, 4), new CompositeNumber(3, 5));
    }

    @Test
    void lowerBound_hasNegInfSecondary() {
        CompositeNumber lb = CompositeNumber.lowerBound(5.0);
        assertEquals(5.0, lb.getPrimary());
        assertEquals(Double.NEGATIVE_INFINITY, lb.getSecondary());
    }

    @Test
    void upperBound_hasPosInfSecondary() {
        CompositeNumber ub = CompositeNumber.upperBound(5.0);
        assertEquals(5.0, ub.getPrimary());
        assertEquals(Double.POSITIVE_INFINITY, ub.getSecondary());
    }

    @Test
    void leq() {
        assertTrue(new CompositeNumber(1, 0).leq(new CompositeNumber(2, 0)));
        assertTrue(new CompositeNumber(2, 0).leq(new CompositeNumber(2, 0)));
        assertFalse(new CompositeNumber(3, 0).leq(new CompositeNumber(2, 0)));
    }

    @Test
    void geq() {
        assertTrue(new CompositeNumber(3, 0).geq(new CompositeNumber(2, 0)));
        assertTrue(new CompositeNumber(2, 0).geq(new CompositeNumber(2, 0)));
        assertFalse(new CompositeNumber(1, 0).geq(new CompositeNumber(2, 0)));
    }

    @Test
    void lowerBound_includesBoundaryPoint() {
        // Any point with primary = 5 and finite secondary is >= lowerBound(5)
        assertTrue(new CompositeNumber(5.0, 0).compareTo(CompositeNumber.lowerBound(5.0)) >= 0);
        assertTrue(new CompositeNumber(5.0, -999).compareTo(CompositeNumber.lowerBound(5.0)) >= 0);
        assertTrue(new CompositeNumber(5.0, 999).compareTo(CompositeNumber.lowerBound(5.0)) >= 0);
    }

    @Test
    void upperBound_includesBoundaryPoint() {
        // Any point with primary = 5 and finite secondary is <= upperBound(5)
        assertTrue(new CompositeNumber(5.0, 0).compareTo(CompositeNumber.upperBound(5.0)) <= 0);
        assertTrue(new CompositeNumber(5.0, 999).compareTo(CompositeNumber.upperBound(5.0)) <= 0);
        assertTrue(new CompositeNumber(5.0, -999).compareTo(CompositeNumber.upperBound(5.0)) <= 0);
    }

    @Test
    void compositeX_factory() {
        CompositeNumber cx = CompositeNumber.compositeX(3.0, 7.0);
        assertEquals(3.0, cx.getPrimary());
        assertEquals(7.0, cx.getSecondary());
    }

    @Test
    void compositeY_factory() {
        CompositeNumber cy = CompositeNumber.compositeY(3.0, 7.0);
        assertEquals(7.0, cy.getPrimary());
        assertEquals(3.0, cy.getSecondary());
    }
}
