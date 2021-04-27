package com.pixlee.pixleesdk

import com.pixlee.pixleesdk.data.PXLProduct
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Created by sungjun on 2/1/21.
 */
class DateTest {
    @Test
    fun hasProduct() {
        val product = PXLProduct()
        product.salesPrice = 14.92.toBigDecimal()
        product.salesStartDate = Date(1619305336000)
        product.salesEndDate =   Date(1619823736000)
        Assert.assertEquals(true, product.hasAvailableSalesPrice())
    }

    @Test
    fun hasProductWithFuture() {
        val product = PXLProduct()
        product.salesPrice = 14.92.toBigDecimal()
        product.salesStartDate = Date(System.currentTimeMillis() + 10000)
        product.salesEndDate =   Date(System.currentTimeMillis() + 20000)
        Assert.assertEquals(false, product.hasAvailableSalesPrice())
    }

    @Test
    fun hasProductNotPreviousDate() {
        val product = PXLProduct()
        product.salesPrice = 14.92.toBigDecimal()
        product.salesStartDate = Date(System.currentTimeMillis() - 10000)
        product.salesEndDate =   Date(System.currentTimeMillis() - 20000)
        Assert.assertEquals(false, product.hasAvailableSalesPrice())
    }

    @Test
    fun noSalesPriceWithDate() {
        val product = PXLProduct()
        product.salesStartDate = Date(1619305336000)
        product.salesEndDate =   Date(1619823736000)
        Assert.assertEquals(false, product.hasAvailableSalesPrice())
    }



    @Test
    fun hasProductNoDate() {
        val product = PXLProduct()
        product.salesPrice = 14.92.toBigDecimal()
        Assert.assertEquals(false, product.hasAvailableSalesPrice())
    }

    @Test
    fun textColor(){
        fun lightenColor(color: Int, fraction: Double): Int {
            return Math.min(color + color * fraction, 255.0).toInt()
        }


        println("${lightenColor(70, 100.0)}")
    }
}