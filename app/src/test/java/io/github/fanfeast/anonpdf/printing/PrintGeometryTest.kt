package io.github.fanfeast.anonpdf.printing

import org.junit.Assert.assertEquals
import org.junit.Test

class PrintGeometryTest {
    @Test fun eightyPercentA4IsNotEnlargedBackToFullSheet() {
        assertEquals(1f, printScale(476, 674, 595, 842), 0.0001f)
    }
    @Test fun oversizedPageFitsWithoutCropping() {
        assertEquals(0.5f, printScale(1190, 1684, 595, 842), 0.0001f)
    }
    @Test fun marginsCanReduceFullSizePage() {
        assertEquals(575f / 595f, printScale(595, 842, 575, 822), 0.0001f)
    }
}
