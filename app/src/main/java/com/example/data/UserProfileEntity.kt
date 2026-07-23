package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val isLoggedIn: Boolean = false,
    val hasLoggedOut: Boolean = false,
    val isOnboarded: Boolean = false,
    val userName: String = "Gaurav Sharma",
    val userEmail: String = "gs.gaurav0406@gmail.com",
    val age: Int = 28,
    val city: String = "Mumbai",
    val mobileNumber: String = "+91 98765 43210",
    val selectedCategories: String = "Credit Cards,ITR & Tax,Loans & FDs,Markets & Mutual Funds,RBI & Policy,Sports,Cars & EV",
    val autoPlayAudio: Boolean = false
)
