package com.example.chartapplication.utils.exceptions

sealed class ArgumentsException(message: String): Exception(message) {
    class UnableToReceiveChartType(message: String): ArgumentsException(message)
    class UnableToReceiveWeekChartData(message: String): ArgumentsException(message)
    class UnableToReceiveMonthChartData(message: String): ArgumentsException(message)
}