package com.example.testapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _weightLivedata = MutableLiveData<Pair<Double, Boolean>>()
    private val _dobLivedata = MutableLiveData<Date>()
    private val _photoLivedata = MutableLiveData<String>()
    private val _editPersonLivedata = MutableLiveData<Pair<Int, PersonData>>()
    private val _personLivedata = MediatorLiveData<PersonData>().also{ liveData ->
        liveData.value = PersonData()
//        var weight: Double = 0.0
//        var isKgUnits: Boolean = true
//        var dob: Date =Date()
//        var path: String =""
        liveData.addSource(_weightLivedata) { wl->
            wl?.let {
                liveData.value!!.weight = it.first
                liveData.value!!.isKgUnits = it.second
//                weight = it.first
//                isKgUnits = it.second
            }
        }
        liveData.addSource(_dobLivedata) { dl ->
            dl?.let { liveData.value!!.dob = it }
//            dl?.let { dob = it }
        }
        liveData.addSource(_photoLivedata) { pl ->
            pl?.let {liveData.value!!.image = it}
//            pl?.let {path = it}
        }
//        liveData.value = PersonData(weight = weight, image = path, isKgUnits = isKgUnits, dob = dob)
    }

    val personLiveData: LiveData<PersonData> get() = _personLivedata
    val editPersonLivedata: LiveData<Pair<Int, PersonData>> get() = _editPersonLivedata

    fun setWeight(weight: Double, units: Boolean){
        if (_editPersonLivedata.value != null) {
            _editPersonLivedata.value!!.second.weight = weight
            _editPersonLivedata.value!!.second.isKgUnits = units
        } else {
            _weightLivedata.value = Pair(weight, units)
        }
    }

    fun setDob(dob: Date){
        if (_editPersonLivedata.value != null) {
            _editPersonLivedata.value!!.second.dob = dob
        } else {
            _dobLivedata.value = dob
        }
    }
    fun setPhoto(photo: String){
        if (_editPersonLivedata.value != null) {
            _editPersonLivedata.value!!.second.image = photo
        } else {
            _photoLivedata.value = photo
        }
    }

    fun setEditPerson(ind:Int, personData: PersonData) {
        _editPersonLivedata.value = Pair(ind,personData)
    }
    fun clearPerson() {
        _personLivedata.value = PersonData()
        _weightLivedata.value = null
        _dobLivedata.value = null
        _photoLivedata.value = null
        _editPersonLivedata.value = null
    }

}