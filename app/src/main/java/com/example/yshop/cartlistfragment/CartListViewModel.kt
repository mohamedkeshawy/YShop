package com.example.yshop.cartlistfragment


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.yshop.R
import com.example.yshop.model.CartItemModel
import com.example.yshop.model.ProductModel
import com.example.yshop.utils.Constants
import com.example.yshop.utils.OptionBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartListViewModel : ViewModel() {

    var cartItemList        = MutableLiveData<ArrayList<CartItemModel>>()
    var mProductList        = MutableLiveData<ArrayList<ProductModel>>()
    var firebaseDatabase    = FirebaseDatabase.getInstance()
    var cartItemReference   = firebaseDatabase.getReference(Constants.CART_ITEM)
    var productReference = firebaseDatabase.getReference(Constants.PRODUCT)

    fun getCartItemList( rv_cart_items_list : RecyclerView
                         , ll_checkout : LinearLayout ,
                         tv_no_cart_item_found : TextView ,
                         tv_sub_total : TextView ,
                         tv_shipping_charge : TextView ,
                         tv_total_amount : TextView){

        var cartListArray : ArrayList<CartItemModel> = ArrayList()

        cartItemReference.orderByChild(Constants.USER_ID).equalTo(Constants.getCurrentUser()).addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for( ds in snapshot.children ){

                    var cartItem = ds.getValue(CartItemModel::class.java)!!
                    cartItem.id = ds.key.toString()
                    cartListArray.add(cartItem)
                }
                cartItemList.value = cartListArray

                if( cartItemList.value!!.size > 0){
                    rv_cart_items_list.visibility       = View.VISIBLE
                    ll_checkout.visibility              = View.VISIBLE
                    tv_no_cart_item_found.visibility    = View.GONE

                    var subTotal : Double = 0.0
                    for( item in cartItemList.value!!){

                            var price       = item.price.toDouble()
                            var quantity    = item.cartQuantity.toInt()

                            subTotal += ( price * quantity)
                    }
                    tv_sub_total.text = "$${subTotal}"
                    tv_shipping_charge.text = "$10.0"
                    if( subTotal > 0 ){
                        ll_checkout.visibility = View.VISIBLE
                        var total = subTotal + 10
                        tv_total_amount.text = "$${total}"
                    }else{
                        ll_checkout.visibility = View.GONE
                    }
                }else {

                    rv_cart_items_list.visibility       = View.GONE
                    ll_checkout.visibility              = View.GONE
                    tv_no_cart_item_found.visibility    = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun getAllProductList(){
        productReference.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var productList : ArrayList<ProductModel> = ArrayList()
                for ( ds in snapshot.children ){

                    var product = ds.getValue(ProductModel::class.java)!!

                    product.productId = ds.key.toString()

                    productList.add(product)
                }
                mProductList.value = productList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // fun remove item from cart
    fun removeItemFromCart( context: Context , cartId : String){
        OptionBuilder.showProgressDialog( context.resources.getString(R.string.please_wait) , context)
        cartItemReference.child(cartId).removeValue()
        Toast.makeText( context , context.resources.getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()

    }


    // Minus cart item
    fun minusCartItem( context: Context , cartId: String , cartQuantity : String){
        if( cartQuantity == "1"){

            removeItemFromCart( context , cartId)

        }else{

            OptionBuilder.showProgressDialog( context.resources.getString(R.string.please_wait) , context)
            var cartItemQuantity : Int = cartQuantity.toInt()
            var map = HashMap<String , Any>()
            map[Constants.CART_QUANTITY] = ( cartItemQuantity - 1).toString()
            cartItemReference.child(cartId).updateChildren(map)

        }
    }



    fun plusCartItem(  cartId : String , cartQuantity: String , cartStockQuantity : String ){

        var cartItemQuantity  = cartQuantity.toInt()

        var map = HashMap<String , Any>()
        map[Constants.CART_QUANTITY] = ( cartItemQuantity + 1 ).toString()
        cartItemReference.child(cartId).updateChildren(map)

        if( cartItemQuantity < cartStockQuantity.toInt() ){


        }
    }


    // get data form product
//    for( product in mProductList.value!!){
//        for( cart in cartItemModel.value!!){
//            if( product.productId == cart.productId){
//
//                cart.stockQuantity == product.stockQuantity
//                if( product.stockQuantity.toInt() == 0 ){
//                    cart.cartQuantity = product.stockQuantity
//                }
//            }
//        }
//    }
//    array = cartItemModel.value!!
}