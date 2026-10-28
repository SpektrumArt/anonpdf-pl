package io.github.fanfeast.anonpdf.printing

import kotlin.math.min

/** Physical PDF points: never enlarge a PDF that the editor has already shrunk. */
internal fun printScale(width: Int, height: Int, areaWidth: Int, areaHeight: Int): Float {
    require(width > 0 && height > 0 && areaWidth > 0 && areaHeight > 0)
    return min(1f, min(areaWidth.toFloat() / width, areaHeight.toFloat() / height))
}
