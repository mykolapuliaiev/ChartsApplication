package com.example.chartapplication.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chartapplication.R
import com.example.chartapplication.databinding.FragmentChartBinding
import com.example.chartapplication.ui.models.ChartPeriod
import com.example.chartapplication.ui.models.ChartType
import com.example.chartapplication.utils.exceptions.ArgumentsException
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.LineData

class ChartFragment : Fragment() {

    companion object {
        private const val WEEK_CHART_DATA_KEY = "week_chart_data_key"
        private const val MONTH_CHART_DATA_KEY = "month_chart_data_key"
        private const val CHART_TYPE_KEY = "chart_type_key"

        fun newInstance(
            weekChartData: String,
            monthChartData: String,
            chartType: ChartType
        ): ChartFragment {
            val fragment = ChartFragment()
            fragment.arguments = bundleOf(
                WEEK_CHART_DATA_KEY to weekChartData,
                MONTH_CHART_DATA_KEY to monthChartData,
                CHART_TYPE_KEY to chartType
            )
            return fragment
        }
    }

    private lateinit var binding: FragmentChartBinding

    private val viewModel: ChartViewModel by lazy {
        ViewModelProvider(this).get(ChartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        viewModel.chartType = getChartType()
        viewModel.setWeekChartData(getWeekChartData())
        viewModel.setMonthChartData(getMonthChartData())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // 1. Setup radio group clicks
        binding.chartsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.week_radio -> updateChartData(ChartPeriod.WEEK)
                R.id.month_radio -> updateChartData(ChartPeriod.MONTH)
            }
        }

        // 2. Initial chart setup depending on chart type
        when (viewModel.chartType) {
            ChartType.LINE -> {
                binding.lineChart.visibility = View.VISIBLE
                setupChartView(binding.lineChart)
                updateChartData(binding.lineChart, viewModel.weekChartLineData)
            }
            ChartType.CANDLESTICK -> {
                binding.candlestickChartScrollview.visibility = View.VISIBLE
                setupCandleChartViews()
                updateChartData(viewModel.weekChartCandleData)
            }
        }

        binding.chartsRadioGroup.check(R.id.week_radio)
    }

    private fun setupChartView(chart: LineChart) {
        chart.apply {
            setDrawBorders(false)
            setTouchEnabled(true)
            setPinchZoom(false)
            setScaleEnabled(true)

            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.setDrawAxisLine(false)
            xAxis.setDrawAxisLine(false)
            xAxis.labelRotationAngle = 0f

            isDragEnabled = false

            val l: Legend = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            animateX(1000, Easing.EaseInCubic)
        }
    }

    private fun setupCandleChartViews() {
        val charts = listOf(binding.candleChart1, binding.candleChart2, binding.candleChart3)
        charts.forEach { setupChartView(it) }
    }

    private fun setupChartView(chart: CandleStickChart) {
        chart.apply {
            description.isEnabled = false

            setMaxVisibleValueCount(300)
            setScaleEnabled(true)
            setPinchZoom(true)

            isDragEnabled = true

            val leftAxis: YAxis = axisLeft
            leftAxis.setLabelCount(8, false)
            leftAxis.setDrawGridLines(false)
            leftAxis.setDrawAxisLine(false)

            val xAxis: XAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            val rightAxis: YAxis = axisRight
            rightAxis.isEnabled = false

            val l: Legend = legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
        }
    }

    private fun updateChartData(chartPeriod: ChartPeriod) {
        val lineData: LineData?
        val candleData: MutableList<CandleData>?
        when (chartPeriod) {
            ChartPeriod.WEEK -> {
                lineData = viewModel.weekChartLineData
                candleData = viewModel.weekChartCandleData
            }
            ChartPeriod.MONTH -> {
                lineData = viewModel.monthChartLineData
                candleData = viewModel.monthChartCandleData
            }
        }

        when (viewModel.chartType) {
            ChartType.LINE -> updateChartData(binding.lineChart, lineData)
            ChartType.CANDLESTICK -> updateChartData(candleData)
        }
    }

    private fun updateChartData(chart: LineChart, data: LineData?) {
        chart.data = data
        chart.invalidate()
    }

    private fun updateChartData(data: MutableList<CandleData>?) {
        val charts = listOf(binding.candleChart1, binding.candleChart2, binding.candleChart3)
        charts.forEachIndexed { index, candleStickChart ->
            updateChartData(candleStickChart, data?.get(index))
        }
    }

    private fun updateChartData(chart: CandleStickChart, data: CandleData?) {
        chart.data = data
        chart.invalidate()
    }

    /**
     * Retrieving of chart type
     */
    @Throws(ArgumentsException.UnableToReceiveChartType::class)
    private fun getChartType(): ChartType {
        return (arguments?.getSerializable(CHART_TYPE_KEY) as? ChartType)
            ?: throw ArgumentsException.UnableToReceiveChartType(
                getString(R.string.exception_unable_to_receive_chart_type)
            )
    }

    /**
     * Retrieving of month chart data
     */
    @Throws(ArgumentsException.UnableToReceiveMonthChartData::class)
    private fun getMonthChartData(): String {
        return arguments?.getString(MONTH_CHART_DATA_KEY)
            ?: throw ArgumentsException.UnableToReceiveMonthChartData(getString(R.string.exception_unable_to_receive_month_chart_data))
    }

    /**
     * Retrieving of week chart data
     */
    @Throws(ArgumentsException.UnableToReceiveWeekChartData::class)
    private fun getWeekChartData(): String {
        return arguments?.getString(WEEK_CHART_DATA_KEY)
            ?: throw ArgumentsException.UnableToReceiveWeekChartData(getString(R.string.exception_unable_to_receive_week_chart_data))
    }
}