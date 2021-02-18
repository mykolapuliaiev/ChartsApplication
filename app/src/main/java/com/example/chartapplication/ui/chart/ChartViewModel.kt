package com.example.chartapplication.ui.chart

import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.ViewModel
import com.example.chartapplication.models.QuotePerformance
import com.example.chartapplication.models.QuoteSymbol
import com.example.chartapplication.models.Quotes
import com.example.chartapplication.ui.models.ChartType
import com.example.chartapplication.utils.exceptions.ChartDataException
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class ChartViewModel : ViewModel() {

    private var weekChartData: Quotes? = null
        get() = field ?: throw ChartDataException.NotSet("Week chart data not set")

    private var monthChartData: Quotes? = null
        get() = field ?: throw ChartDataException.NotSet("Month chart data not set")

    var chartType: ChartType? = null
        get() = field ?: throw ChartDataException.ChartTypeNotSet("Chart type is not set")

    private val chartColors = intArrayOf(Color.GREEN, Color.RED, Color.BLUE)

    var weekChartLineData: LineData? = null
    var weekChartCandleData: MutableList<CandleData>? = null
    var monthChartLineData: LineData? = null
    var monthChartCandleData: MutableList<CandleData>? = null

    @Throws(ChartDataException.ParseFailed::class)
    private fun parseChartData(bareChartData: String): Quotes {
        val moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Quotes> = moshi.adapter(Quotes::class.java)
        return adapter.fromJson(bareChartData)
            ?: throw ChartDataException.ParseFailed("Failed to parse chart data")
    }

    @Throws(ChartDataException.NullableChartType::class)
    fun setWeekChartData(chartData: String) {
        val parsedChartData = parseChartData(chartData)
        weekChartData = parsedChartData
        when (chartType) {
            ChartType.LINE -> weekChartLineData = extractLineChartValues(parsedChartData)
            ChartType.CANDLESTICK -> weekChartCandleData = extractCandleChartValues(parsedChartData)
            null -> throw ChartDataException.NullableChartType("Chart type value is null")
        }
    }

    fun setMonthChartData(chartData: String) {
        val parsedChartData = parseChartData(chartData)
        monthChartData = parsedChartData
        when (chartType) {
            ChartType.LINE -> monthChartLineData = extractLineChartValues(parsedChartData)
            ChartType.CANDLESTICK -> monthChartCandleData =
                extractCandleChartValues(parsedChartData)
            null -> throw ChartDataException.NullableChartType("Chart type value is null")
        }
    }

    private fun extractLineChartValues(data: Quotes): LineData {
        var entryIndex = 0
        val dataSets: MutableList<ILineDataSet>? = data.content?.quoteSymbols?.map { quoteSymbol ->
            val performance = calculatePerformance(quoteSymbol)
            val entries =
                performance.map { Entry(it.timeStamp.toFloat(), it.performance.toFloat()) }
            LineDataSet(entries, quoteSymbol.symbol).apply {
                color = chartColors[entryIndex % chartColors.size]
                lineWidth = 2f
                circleRadius = 5f
                setCircleColor(color)
            }.also {
                entryIndex++
            }
        }?.toMutableList()

        return LineData(dataSets)
    }

    private fun extractCandleChartValues(data: Quotes): MutableList<CandleData>? {
        return data.content?.quoteSymbols?.map { quoteSymbol ->
            val entries = ArrayList<CandleEntry>()
            quoteSymbol.timestamps?.indices?.map { index ->
                entries.add(formCandleEntry(quoteSymbol, index))
            }
            val candleDataSet = CandleDataSet(entries, quoteSymbol.symbol).apply {
                setDrawIcons(false)
                axisDependency = YAxis.AxisDependency.LEFT
                decreasingColor = Color.rgb(190, 60, 0)
                decreasingPaintStyle = Paint.Style.FILL
                increasingColor = Color.rgb(112, 171, 73)
                increasingPaintStyle = Paint.Style.FILL
                neutralColor = Color.BLUE
                shadowColor = Color.DKGRAY
                shadowWidth = 0.5f
            }

            CandleData(candleDataSet)
        }?.toMutableList()
    }

    private fun formCandleEntry(symbol: QuoteSymbol, i: Int): CandleEntry {
        val closures = symbol.closures[i]
        val highs = symbol.highs[i]
        val opens = symbol.opens[i]
        val lows = symbol.lows[i]

        return CandleEntry(
            i.toFloat(),
            highs.toFloat(),
            lows.toFloat(),
            opens.toFloat(),
            closures.toFloat()
        )
    }

    @Throws(ChartDataException.PerformanceCalculationFailed::class)
    private fun calculatePerformance(symbol: QuoteSymbol): MutableList<QuotePerformance> {
        if (!symbol.opens.isNullOrEmpty()) {
            val price = symbol.opens[0]
            return symbol.timestamps?.zip(symbol.opens)?.map { opensData ->
                val stockPerformanceValue = calculatePerformanceValue(opensData.second, price)
                return@map QuotePerformance(stockPerformanceValue, opensData.first)
            }?.toMutableList()
                ?: throw ChartDataException.PerformanceCalculationFailed("Performance calculation failed.")
        } else {
            return mutableListOf()
        }
    }

    private fun calculatePerformanceValue(openValue: Double, quotePrice: Double): Double {
        return 100 * openValue / quotePrice - 100
    }
}