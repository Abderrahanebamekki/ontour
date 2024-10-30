package com.example.ontour.screen

import com.mapbox.geojson.Point

data class CustomFeature(
    val place_name: String,
    val geometry: Geometry
)



data class Geometry(
    val coordinates: List<Double>
)

data class Product(val name: String, val price: Float, val quantity: Int, val image: Int)

data class ProductS(
    val product_name: String,
    val product_category: Int,
    val quantity: Int,
    val price: Double
)


data class ServiceCategory(
    val id: Int,
    val category_name: String,
)

data class ProductD(
    val id : Int,
    val product_name : String
)

data class ServiceInfo(
    val name: String,
    val geographicalPoint: Point,
    val linkFacebook: String,
    val linkInstagram: String,
    val pictureLink: String,
    val isOpen: Boolean,
    val description: String
)

//"service": {
//    "name": "Updated Service Name",
//    "geographical_point": {
//        "latitude": 12.3456,
//        "longitude": 78.9012
//    },
//    "link_facebook": "https://facebook.com/anotherexample",
//    "link_instagram": "https://instagram.com/anotherexample",
//    "picture_link": "https://example.com/another-picture.jpg",
//    "is_open": false,
//    "description": "Updated description."
//}

