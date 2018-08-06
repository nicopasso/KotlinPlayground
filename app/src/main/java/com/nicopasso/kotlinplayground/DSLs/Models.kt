package com.nicopasso.kotlinplayground.DSLs


data class Handover(var id: Int? = null,
                    var items: MutableList<Item> = mutableListOf()) {
    operator fun Item.unaryPlus(): Unit {
        items.add(this)
    }
}

data class Item(var id: Int? = null,
                        var name: String? = null)











































/*
    HANDOVER ITEMS w/o DSL
 */
val boringHandover = Handover(id = 1234, items = mutableListOf(
    Item(id = 1234, name = "SHS 200W"),
    Item(id = 45677, name = "Solar Radio"),
    Item(id = 98766, name = "Light Bulb")
))















































/*
    HANDOVER ITEMS with DSL
 */

fun handoverItem(block: Item.() -> Unit) = Item().apply(block)

val handoverItem = handoverItem {
    id = 12345
    name = "SHS 200W"
}









































/*
    HANDOVER  with DSL
 */
fun handover(block: Handover.() -> Unit) = Handover().apply(block)

val handover = handover {

    id = 987654

    val item1 = handoverItem {
        id = 333444
        name = "TV 14'"
    }

    val item2 = handoverItem {
        id = 555566666
        name = "Solar Radio"
    }

    val item3 = handoverItem {
        id = 77788888
        name = "Light Bulb"
    }

    items.add(item1)
    items.add(item2)
    items.add(item3)

}































/*
    HANDOVER  with DSL and Unary plus operator
 */

val fancierHandover = handover {

    id = 789872392

    +handoverItem { id = 333444; name = "TV 14'" }

    +handoverItem { id = 555566666; name = "Solar Radio" }

    +handoverItem { id = 77788888; name = "Light Bulb"}

}










































