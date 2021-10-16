package com.example.mvvmexample.model.userInfo

import java.io.Serializable


class UserInfos : Serializable {
    var name: String? = null
    var email: String? = null
    var photoUrl: String? = null
    var uid: String? = null

    constructor()

    constructor(name: String?, email: String?, photoUrl: String?, userId: String) {
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
        this.uid = userId
    }
}