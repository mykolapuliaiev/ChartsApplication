package com.example.chartapplication.utils.exceptions

sealed class ChartsAdapterException(message: String) : Exception(message) {
    class WrongFragmentIndex(message: String) : ChartsAdapterException(message)
}