package org.windowing.windowingproject.pst;

import org.junit.jupiter.api.Test;
import org.windowing.windowingproject.model.PstEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PrioritySearchTreeTest {

    /** Five distinct points spread across x=[1,5], y=[1,5]. */
    private List<PstEntry> samplePoints() {
        return Arrays.asList(
                new PstEntry(1, 2, 0, 0),
                new PstEntry(3, 4, 1, 0),
                new PstEntry(5, 1, 2, 0),
                new PstEntry(2, 5, 3, 0),
                new PstEntry(4, 3, 4, 0)
        );
    }

    private Set<Integer> segIndices(List<PstEntry> entries) {
        return entries.stream().map(PstEntry::getSegmentIndex).collect(Collectors.toSet());
    }

    // ---- queryOpenLeft (forward tree) ----

    @Test
    void queryOpenLeft_allInRange() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        assertEquals(5, pst.queryOpenLeft(10, 0, 10).size());
    }

    @Test
    void queryOpenLeft_xUpperBoundExcludes() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        Set<Integer> result = segIndices(pst.queryOpenLeft(3, 0, 10));
        assertTrue(result.contains(0));  // x=1 ≤ 3
        assertTrue(result.contains(1));  // x=3 ≤ 3
        assertFalse(result.contains(2)); // x=5 > 3
        assertFalse(result.contains(4)); // x=4 > 3
    }

    @Test
    void queryOpenLeft_yRangeFilters() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        Set<Integer> result = segIndices(pst.queryOpenLeft(10, 3, 5));
        assertTrue(result.contains(1));  // y=4
        assertTrue(result.contains(3));  // y=5
        assertTrue(result.contains(4));  // y=3
        assertFalse(result.contains(0)); // y=2 < 3
        assertFalse(result.contains(2)); // y=1 < 3
    }

    @Test
    void queryOpenLeft_exactBoundaryIncluded() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        List<PstEntry> result = pst.queryOpenLeft(1, 2, 2);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getSegmentIndex());
    }

    @Test
    void queryOpenLeft_noMatch_returnsEmpty() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        assertTrue(pst.queryOpenLeft(0, 0, 10).isEmpty());
    }

    // ---- queryOpenLeft (negated tree — x ≥ xMin) ----

    @Test
    void negatedTree_findsPointsAboveXMin() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), true);
        Set<Integer> result = segIndices(pst.queryOpenLeft(-3, 0, 10)); // x ≥ 3
        assertTrue(result.contains(1));  // x=3
        assertTrue(result.contains(2));  // x=5
        assertTrue(result.contains(4));  // x=4
        assertFalse(result.contains(0)); // x=1 < 3
        assertFalse(result.contains(3)); // x=2 < 3
    }

    // ---- queryYStrip ----

    @Test
    void queryYStrip_returnsCorrectRange() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        Set<Integer> result = segIndices(pst.queryYStrip(2, 4));
        assertTrue(result.contains(0));  // y=2
        assertTrue(result.contains(1));  // y=4
        assertTrue(result.contains(4));  // y=3
        assertFalse(result.contains(2)); // y=1
        assertFalse(result.contains(3)); // y=5
    }

    @Test
    void queryYStrip_emptyRange() {
        PrioritySearchTree pst = new PrioritySearchTree(samplePoints(), false);
        assertTrue(pst.queryYStrip(10, 20).isEmpty());
    }

    // ---- duplicate-coordinate correctness (Section 5.5) ----

    @Test
    void duplicateY_bothPointsFound() {
        List<PstEntry> pts = Arrays.asList(
                new PstEntry(1, 5, 0, 0),
                new PstEntry(3, 5, 1, 0)
        );
        PrioritySearchTree pst = new PrioritySearchTree(pts, false);
        assertEquals(2, pst.queryOpenLeft(10, 5, 5).size());
    }

    @Test
    void duplicateX_bothPointsFound() {
        List<PstEntry> pts = Arrays.asList(
                new PstEntry(3, 1, 0, 0),
                new PstEntry(3, 4, 1, 0)
        );
        PrioritySearchTree pst = new PrioritySearchTree(pts, false);
        assertEquals(2, pst.queryOpenLeft(3, 0, 10).size());
    }

    // ---- edge cases ----

    @Test
    void emptyTree_returnsEmpty() {
        PrioritySearchTree pst = new PrioritySearchTree(List.of(), false);
        assertTrue(pst.queryOpenLeft(100, 0, 100).isEmpty());
        assertTrue(pst.queryYStrip(0, 100).isEmpty());
    }

    @Test
    void singlePoint_found() {
        PrioritySearchTree pst = new PrioritySearchTree(
                List.of(new PstEntry(5, 5, 0, 0)), false);
        assertEquals(1, pst.queryOpenLeft(5, 5, 5).size());
    }

    @Test
    void singlePoint_notFound() {
        PrioritySearchTree pst = new PrioritySearchTree(
                List.of(new PstEntry(5, 5, 0, 0)), false);
        assertTrue(pst.queryOpenLeft(4, 0, 10).isEmpty()); // x=5 > 4
    }
}
