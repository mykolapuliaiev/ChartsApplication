package com.example.chartapplication.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chartapplication.ui.chart.ChartFragment
import com.example.chartapplication.ui.models.ChartType
import com.example.chartapplication.utils.exceptions.ChartsAdapterException

class ChartsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        private const val ITEM_COUNT = 2

        private const val LINE_CHART_INDEX = 0
        private const val CANDLESTICK_CHART_INDEX = 1
    }

    private lateinit var weekChartData: String
    private lateinit var monthChartData: String

    override fun getItemCount(): Int = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        val chartType: ChartType = when (position) {
            LINE_CHART_INDEX -> ChartType.LINE
            CANDLESTICK_CHART_INDEX -> ChartType.CANDLESTICK
            else -> throw ChartsAdapterException.WrongFragmentIndex("Wrong index for ChartsAdapter")
        }
        return ChartFragment.newInstance(weekChartData, monthChartData, chartType)
    }

    fun setChartsData(weekChartData: String, monthChartData: String) {
        this.weekChartData = weekChartData
        this.monthChartData = monthChartData
        notifyDataSetChanged()
    }
}