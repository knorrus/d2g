package org.d2g.model

/**
 * @author knorr
 */
case class Product(firstName: String,
                   lastName: String,
                   id: Option[Int] = None,
                   phoneNumber: Option[String] = None,
                   address: Option[String] = None,
                   city: Option[String] = Some("New York"),
                   country: Option[String] = Some("USA"),
                   zipcode: Option[String] = None) {
}

