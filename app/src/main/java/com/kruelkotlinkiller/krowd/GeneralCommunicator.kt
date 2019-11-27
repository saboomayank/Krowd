package com.kruelkotlinkiller.krowd

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneralCommunicator : ViewModel(){
    val message = MutableLiveData<Any>()
    val id = MutableLiveData<Any>()
    val name = MutableLiveData<Any>()
    fun setMsgCommunicator(msg:String){
        message.value = msg
    }
    fun setIdCommunicator(id1 : String){
        id.value = id1
    }
    fun setNameCommunicator(studentName : String){
        name.value = studentName
    }
}