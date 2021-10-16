package com.example.mvvmexample.utils

class CustomApiException(code: String, url: String) :
    Exception("Error code : $code, Url : $url")