package com.test.mongodb.repo

import com.test.mongodb.model.Customer
import org.springframework.data.mongodb.repository.MongoRepository



interface CustomerRepository : MongoRepository<Customer, String> {

        fun findByLocation(location: String): Customer
}
