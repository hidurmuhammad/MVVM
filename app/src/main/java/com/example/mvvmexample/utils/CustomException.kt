package com.example.mvvmexample.utils

class CustomException (exception: String, message: String) :
    Exception("Error : $exception, message : $message")