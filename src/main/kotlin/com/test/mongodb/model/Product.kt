package com.test.mongodb.model

import org.springframework.data.annotation.Id

data class Product(@Id var procID: String, var procName: String, var procPrice: String, var procAvatar:String)
