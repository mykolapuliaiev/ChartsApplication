package com.example.chartapplication.utils.exceptions

sealed class ChartDataException(message: String) : Exception(message) {
    class NotSet(message: String) : ChartDataException(message)
    class ParseFailed(message: String) : ChartDataException(message)
    class PerformanceCalculationFailed(message: String) : ChartDataException(message)
    class ChartTypeNotSet(message: String) : ChartDataException(message)
    class NullableChartType(message: String): ChartDataException(message)
}