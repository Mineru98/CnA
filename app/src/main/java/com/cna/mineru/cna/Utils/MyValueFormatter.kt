package com.cna.mineru.cna.Utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter

import java.text.DecimalFormat

class MyValueFormatter(private val suffix: String) : ValueFormatter() {

    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,###,##0")
    }

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble()) + suffix
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return if (axis is XAxis) {
            mFormat.format(value.toDouble())
        } else if (value > 0) {
            mFormat.format(value.toDouble()) + suffix
        } else {
            mFormat.format(value.toDouble())
        }
    }
}