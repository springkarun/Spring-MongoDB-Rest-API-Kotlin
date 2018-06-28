package com.test.mongodb.model

import org.springframework.data.annotation.Id

data class Customer(@Id var id: String, var name: String,var location: String,var avatar:String)
