package com.example.resellapp.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resellapp.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HomeViewModel: ViewModel() {

    private var database: FirebaseDatabase =  FirebaseDatabase.getInstance("https://androidkotlinresellapp-default-rtdb.europe-west1.firebasedatabase.app/")

    private var dbRef: DatabaseReference = database.getReference("Items")

    private  var userId = Firebase.auth.currentUser!!.uid


    private val _navigateToItemDetail = MutableLiveData<String?>()
    val navigateToItemDetail: LiveData<String?>
        get() = _navigateToItemDetail

    private val _itemsList = MutableLiveData<List<Item>>()
    val itemsList: LiveData<List<Item>>
        get() = _itemsList

    private val _itemsList2 = MutableLiveData<List<Item>>()
    val itemsList2: LiveData<List<Item>>
        get() = _itemsList2

    private val _itemsListClothing = MutableLiveData<List<Item>>()
    val itemsListClothing: LiveData<List<Item>>
        get() = _itemsListClothing

    private val _itemsListFootwear = MutableLiveData<List<Item>>()
    val itemsListFootwear: LiveData<List<Item>>
        get() = _itemsListFootwear

    private val _itemsListAcc = MutableLiveData<List<Item>>()
    val itemsListAcc: LiveData<List<Item>>
        get() = _itemsListAcc

    private val _hideFilters = MutableLiveData<Boolean?>()
    val hideFilters: LiveData<Boolean?>
        get() = _hideFilters

    private val _RBvalue = MutableLiveData<Int>()
    val RBvalue: LiveData<Int>
        get() = _RBvalue

    private val _subcategList = MutableLiveData<List<String>>()
    val subcategList: LiveData<List<String>>
        get() = _subcategList

    private val _sizeList = MutableLiveData<List<String>>()
    val sizeList: LiveData<List<String>>
        get() = _sizeList



    init{
        _RBvalue.value = 1

        _subcategList.value = emptyList()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val items: MutableList<Item> = mutableListOf()
                    for(json in snapshot.children)
                    {
                        val item = json.getValue(Item::class.java)
//                        Log.e("item","${item!!.userId}")
//                        Log.e("item","${userId}")

                        if(!userId!!.equals(item!!.userId) )
                        {
//                            Log.e("item","${item}")
                            item?.let{
                                if(it.bought != true)
                                    items.add(it)
                            }
                        }




                    }
                    _itemsList.value = items
                    _itemsList2.value = items

                    _itemsListClothing.value = filterListCateg("Clothing",items)
                    _itemsListFootwear.value = filterListCateg("Footwear",items)
                    _itemsListAcc.value = filterListCateg("Accessories",items)

                    // if newItem added _itemsList needs filters applyed
                    val ok1 = _subcategList.value?.size  ?: 0
                    Log.e("ordonator",ok1.toString())

                    if(ok1 > 0)
                    {
                        val filterList = _itemsList.value?.filter { inList(_subcategList.value ?: emptyList(),it.subcategory)  } ?: emptyList()
                        _itemsList.value = filterList
                    }
                    val ok2 = _sizeList.value?.size ?: 0
                    Log.e("ordonator",ok2.toString())

                    if(ok2 > 0)
                    {
                        val filterList = _itemsList.value?.filter { inList(_sizeList.value ?: emptyList(),it.size)  } ?: emptyList()
                        _itemsList.value = filterList
                    }
                    val ord = _RBvalue.value
                    Log.e("ordonator",ord.toString())


                    if(ord == 2)
                    {
                        val itemListByDate = _itemsList.value?.sortedByDescending { it.timestamp } ?: emptyList()
                        _itemsList.value = itemListByDate
                    }
                    if(ord == 3)
                    {
                        val itemListAsc = _itemsList.value?.sortedByDescending { it.price } ?: emptyList()
                        _itemsList.value = itemListAsc
                    }
                    if(ord == 4)
                    {
                        val itemListAsc = _itemsList.value?.sortedBy { it.price } ?: emptyList()
                        _itemsList.value = itemListAsc
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ItemViewModel", "loadItems:onCancelled", error.toException())
            }
        })
    }

    fun changeRBvalue(value:Int){
        _RBvalue.value = value
    }

    fun setSubcatList(list:List<String>){
        _subcategList.value = list
    }
    fun setSizeList(list:List<String>){
        _sizeList.value = list
    }




    fun listByPriceAsc(){
        val itemListAsc = _itemsList.value?.sortedBy { it.price } ?: emptyList()
        _itemsList.value = itemListAsc
    }

    fun listByPriceDesc(){
        val itemListAsc = _itemsList.value?.sortedByDescending { it.price } ?: emptyList()
        _itemsList.value = itemListAsc
    }

    fun listByDate(){
        val itemListByDate = _itemsList.value?.sortedByDescending { it.timestamp } ?: emptyList()
        _itemsList.value = itemListByDate
    }

    fun ResetFirstList(){
        _itemsList.value = _itemsList2.value
    }

    fun filterListSubateg(){
        val filterList = _itemsList.value?.filter { inList(_subcategList.value ?: emptyList(),it.subcategory)  } ?: emptyList()
        Log.e("list2","$filterList")

        _itemsList.value = filterList
    }

    fun filterListCateg(categ: String,items:List<Item>):List<Item>{
        val filterList = items?.filter { it.category == categ  } ?: emptyList()
        Log.e("listCATEG","$filterList")

        return filterList
    }



    fun filterListSize(){
        val filterList = _itemsList.value?.filter { inList(_sizeList.value ?: emptyList(),it.size)  } ?: emptyList()
        Log.e("list2","$filterList")

        _itemsList.value = filterList
    }




    fun getItemsList1(): LiveData<List<Item>>{
        return itemsList
    }


    fun clickOnItem(id: String?){
        _navigateToItemDetail.value = id

    }

    fun doneNavigatingDetails(){
        _navigateToItemDetail.value = null
    }

    fun setHideFilters(value:Boolean){
        _hideFilters.value = value
    }

    fun inList(list:List<String>,string: String?):Boolean{

        for(elem in list)
        {
            if(elem == string)
            {
                return true
            }
        }
        return false
    }


}