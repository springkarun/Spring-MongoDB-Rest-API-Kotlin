package com.test.mongodb.repo

import com.test.mongodb.model.Product
import org.springframework.data.mongodb.repository.MongoRepository


interface ProductRepository : MongoRepository<Product, String>
