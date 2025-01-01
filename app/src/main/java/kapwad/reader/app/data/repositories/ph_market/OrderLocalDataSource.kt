package kapwad.reader.app.data.repositories.ph_market


import android.util.Log
import kapwad.reader.app.data.local.OrderDao
import kapwad.reader.app.data.model.ProductOrderListModelData
import javax.inject.Inject

class OrderLocalDataSource @Inject constructor(private val orderDao: OrderDao) {

    suspend fun createOrder(order: ProductOrderListModelData) {
        Log.d("OrderLocalDataSource", "Order to be inserted: $order")
        orderDao.createOrder(order)
    }

    suspend fun insertOrder(order: ProductOrderListModelData) {

        orderDao.insertOrder(order)
    }

//    suspend fun getAllUsers(): List<ProductOrderListModelData>{
//        return orderDao.getAllUsers()
//    }

    suspend fun getOrder() =
        orderDao.getOrders()

    suspend fun getTotal() =
        orderDao.getOverallTotal()


    suspend fun deleteAll() =
        orderDao.deleteAllOrders()

}