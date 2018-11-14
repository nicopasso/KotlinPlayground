package com.nicopasso.kotlinplayground.arrow

import arrow.optics.Lens
import arrow.optics.optics

/**************************************************************************

      ______   .______   .___________. __    ______     _______.
     /  __  \  |   _  \  |           ||  |  /      |   /       |
    |  |  |  | |  |_)  | `---|  |----`|  | |  ,----'  |   (----`
    |  |  |  | |   ___/      |  |     |  | |  |        \   \
    |  `--'  | |  |          |  |     |  | |  `----.----)   |
     \______/  | _|          |__|     |__|  \______|_______/



                              "Help me Arrow, you're my only hope!"
                    .==.     /
                   ()''()-. /
        .---.       ;--; /
      .'_:___". _..'.  __'.
      |__ --==|'-''' \'...;
      [  ]  :[|       |---\
      |__| I=[|     .'    '.
      / / ____|     :       '._
     |-/.____.'      | :       :
    /___\ /___\      '-'._----'


 *****************************************************************************/

// In Functional Programming the main two types of Optics are Lenses and Prisms.
// Both propose a way for "getting" and "setting" values in a data type.
// Lens works with product types (tuples, objects), Prism works mainly with arrays
// "A Lens is a first-class reference to a subpart of some data type. Three things you might want to do:
// - get a nested object
// - modify the parent object by changing a nested object
// - combine a lens with another lens to "look" even deeper
// "A Prism is like a Lens that deals with nullable (optional) values". It works only with selaed classes
// p.s. still trying to understand what they do and how they work. O_O


// Scope of this code is to change the amount of the price of the product inside the Order->SalesCase by using Lenses.

data class Price(val id: Int, val amount: Long)
data class Product(val id: Int, val price: Price)
data class User(val id: Int, val name: String, val lastName: String, val phoneNumber: String)
data class Order(val id: Int, val createdAt: String, val saleBy: User, val product: Product)
data class SalesCase(val id: Int, val order: Order)
// Those are all immutable values and immutable models. Therefore it gets complicated when we have to
// dig deeper to update nested data

// We have three ways to update those nested values
// 1) Kotlin synthetic copy
// 2) Arrow Lenses
// 3) Arrow Optics

class Lenses {

    val price = Price(1, 1000)
    val product = Product(1, price)
    val user = User(1, "John", "Smith", "2557123456789")
    val order = Order(1, "2018-11-12", user, product)
    val salesCase = SalesCase(1, order)


    init {
        //1) Take advantage of Kotlin data classes and their syntethic copy method
        salesCase.copy(
                order = salesCase.order.copy(
                        product = order.product.copy(
                                price = product.price.copy(
                                        amount = price.amount + 1000
                                )
                        )
                )
        )

        //2) Do immutable updates with composable Optics like Lens
        val salesCaseLens: Lens<SalesCase, Order> =
                Lens(
                        get = { salesCase -> salesCase.order },
                        set = { value -> { salesCase: SalesCase -> salesCase.copy(order = value) }}
                )

        val orderLens: Lens<Order, Product> =
                Lens(
                        get = { order -> order.product },
                        set = { value -> { order: Order -> order.copy(product = value) }}
                )

        val productLens: Lens<Product, Price> =
                Lens(
                        get = { product -> product.price },
                        set = { value -> { product: Product -> product.copy(price = value) }}
                )

        val priceLens: Lens<Price, Long> =
                Lens(
                        get = { price -> price.amount },
                        set = { value -> { price -> price.copy(amount = value) }}
                )

        // The great thing about lenses is that they COMPOSE
        val salesCasePriceAmount = salesCaseLens compose orderLens compose productLens compose priceLens

        salesCasePriceAmount.modify(salesCase) { it + 1000 }


    }

}






//3) Use the @optics annotation
@optics
data class PriceOptics(val id: Int, val amount: Long) {companion object}

@optics
data class ProductOptics(val id: Int, val price: PriceOptics) {companion object}

@optics
data class UserOptics(val id: Int, val name: String, val lastName: String, val phoneNumber: String) {companion object}

@optics
data class OrderOptics(val id: Int, val createdAt: String, val saleBy: UserOptics, val product: ProductOptics) {companion object}

@optics
data class SalesCaseOptics(val id: Int, val order: OrderOptics) { companion object }

class Optics {

    val price = PriceOptics(1, 1000)
    val product = ProductOptics(1, price)
    val user = UserOptics(1, "John", "Smith", "2557123456789")
    val order = OrderOptics(1, "2018-11-12", user, product)
    val salesCase = SalesCaseOptics(1, order)

    init {
        SalesCaseOptics.order.product.price.amount.modify(salesCase) { it + 1000 }
    }


}